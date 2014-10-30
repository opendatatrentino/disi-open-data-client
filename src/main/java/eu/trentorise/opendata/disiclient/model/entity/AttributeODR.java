package eu.trentorise.opendata.disiclient.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IValue;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 13 Mar 2014
 *
 */
public class AttributeODR implements IAttribute {

    private IProtocolClient api;
    private List<IValue> values;
    private Long id;
    private IAttributeDef attrDef;
    private Long attrDefId;
    private Long conceptId;
    private Long instanceID;

    public AttributeODR() {
    }

    public AttributeODR(IProtocolClient api, Attribute attribute) {
        this.api = api;
        if (attribute.getId() != null) {
            this.id = attribute.getId();
        }

        this.attrDefId = attribute.getDefinitionId();
        this.conceptId = attribute.getConceptId();
        this.instanceID = attribute.getInstanceId();
        this.values = convertToValueODR(attribute.getValues());
    }

    @Override
    public String toString() {
        return "AttributeODR [api=" + api + ", id=" + id
                + ", attrDef=" + attrDef + ", attrDefId=" + attrDefId
                + ", conceptId=" + conceptId
                + ", instanceID=" + instanceID
                + ", values=" + values
                + "]";
    }

    public AttributeODR(IProtocolClient api) {
        this.api = api;
    }

    public AttributeODR(IAttributeDef attrDef, ValueODR val) {
        this.attrDef = attrDef;
        this.attrDefId = attrDef.getGUID();
        this.conceptId = attrDef.getConcept().getGUID();

        List<IValue> vals = new ArrayList<IValue>();
        vals.add(val);
        this.values = vals;

    }

    public AttributeODR(IAttributeDef attrDef, List<ValueODR> val) {
        this.attrDef = attrDef;
        this.attrDefId = attrDef.getGUID();
        this.conceptId = attrDef.getConcept().getGUID();

        List<IValue> vals = new ArrayList<IValue>();
        vals.addAll(val);
        this.values = vals;

    }

    public Long getGUID() {
        return id;
    }

    public IAttributeDef getAttributeDefinition() {
        if (this.attrDef == null) {
            IAttributeDef attrDef = new AttributeDef(attrDefId);
            return attrDef;
        } else {
            return this.attrDef;
        }
    }

    public void setAttributeDefinition(IAttributeDef ad) {
        //client side
        this.attrDef = ad;
        this.attrDefId = attrDef.getGUID();
        this.conceptId = attrDef.getConcept().getGUID();
		//server side
        //	AttributeDef atrDef = (AttributeDef) ad;
        //	AttributeDefinition attrDef = atrDef.convertAttributeDefinition(); 
        //	Attribute atr = new Attribute();

        //AttributeClient atClient = new AttributeClient(api);
        //	Attribute attribute = atClient.readAttribute(this.id, null);
        //attribute.setDefinitionId(attrDef.getId()); 
        //	atClient.update(atr);
    }

    public void addValue(IValue value) {
        //client side
        if (this.values != null) {
            this.values.add(value);
        } else {
            List<IValue> vals = new ArrayList<IValue>();
            vals.add(value);
            this.values = vals;

        }
        //server side
        //		ValueODR valODR = (ValueODR)value;
        //		Value val = valODR.convertToValue();
        //		AttributeClient attrCl = new AttributeClient(api);
        //		Attribute attr = attrCl.readAttribute(this.id, null);
        //		List<Value> vals = attr.getValues();
        //		vals.add(val);
        //		attr.setValues(vals);
    }

    public void removeValue(long valueID) {
        //remove from server side
        AttributeClient attrCl = new AttributeClient(api);
        Attribute attr = attrCl.readAttribute(this.id, null);
        Value atrVal = attrCl.readValue(attr.getId(), valueID, null);
        attrCl.delete(atrVal);
        //client side
        ArrayList<IValue> values = (ArrayList<IValue>) this.values;
        for (IValue val : values) {
            if (val.getLocalID() == valueID) {
                values.remove(val);
                return;
            }

        }
    }

    public List<IValue> getValues() {
        return this.values;
    }

    public IValue getFirstValue() {
        return this.values.iterator().next();
    }

    public Long getValuesCount() {
        return (long) values.size();
    }

    private List<IValue> convertToValueODR(List<Value> vals) {
    	
        List<IValue> values = new ArrayList<IValue>();
        for (Value val : vals) {
            ValueODR value = new ValueODR(val);
            values.add(value);
            value.setVocabularyId(val.getVocabularyId());
        }
        return values;
    }

    public Attribute convertToAttribute() {

        Attribute attribute = new Attribute();
        attribute.setConceptId(this.conceptId);
        attribute.setId(this.id);
        attribute.setInstanceId(this.instanceID);
        attribute.setDefinitionId(this.attrDefId);
        attribute.setValues(this.convertValuesList());

        return attribute;
    }

    private List<Value> convertValuesList() {
        List<IValue> valuesODR = this.values;
        List<Value> values = new ArrayList<Value>();
        for (IValue val : valuesODR) {
            ValueODR valODR = (ValueODR) val;
            Value value = valODR.convertToValue();
            values.add(value);
        }
        return values;
    }

    public void updateValue(IValue newValue) {
        //update from server side
        AttributeClient attrCl = new AttributeClient(api);
        Attribute attr = attrCl.readAttribute(this.id, null);

        for (IValue value : values) {
            if (value.getLocalID() == newValue.getLocalID()) {
                values.remove(value);
                return;
            }
        }

        ValueODR val = (ValueODR) newValue;
        Long id = attr.getValues().iterator().next().getId();
        val.setId(id);

        if (values.size() == 1) {
            values.remove(0);
        }
        //val.convertToValue();
        val.setAttributeId(attr.getId());
        Value value = val.convertToValue();
        attrCl.update(value);
        //client side
        values.add(val);

    }

    public Long getLocalID() {
        return this.id;
    }

    public String getAttributeDefURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/attributes/" + this.id
                + "?locale=" + (WebServiceURLs.getClientProtocol()).getLocale();
        return url;
    }

    public IAttributeDef getAttrDef() {
        return getAttributeDefinition();
    }

}
