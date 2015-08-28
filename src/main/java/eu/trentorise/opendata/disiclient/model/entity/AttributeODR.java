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
 * 
 * Dav 0.12 WARNING: removed conceptId, which was only used to convert to Attribute
 * This could cause breakages.
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it> 
 */
public class AttributeODR implements IAttribute {

    private IProtocolClient api;
    private List<IValue> values;
    private Long id;
    private IAttributeDef attrDef;
    private Long attrDefId;    
    private Long instanceID;
        
    public AttributeODR() {
    }

    public AttributeODR(IProtocolClient api, Attribute attribute) {
        this.api = api;
        if (attribute.getId() != null) {
            this.id = attribute.getId();
        }
        this.attrDefId = attribute.getDefinitionId();           
        this.instanceID = attribute.getInstanceId();
        this.values = convertToValueODR(attribute.getValues());
    }

    @Override
    public String toString() {
        return "AttributeODR [api=" + api + ", id=" + id
                + ", attrDef=" + attrDef + ", attrDefId=" + attrDefId
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

        List<IValue> vals = new ArrayList();
        vals.add(val);
        this.values = vals;

    }

    public AttributeODR(IAttributeDef attrDef, List<ValueODR> val) {
        this.attrDef = attrDef;
        this.attrDefId = attrDef.getGUID();        

        List<IValue> vals = new ArrayList();
        vals.addAll(val);
        this.values = vals;

    }

    public Long getGUID() {
        return id;
    }

    @Override
    public IAttributeDef getAttributeDefinition() {
        if (this.attrDef == null) {
            IAttributeDef ret = new AttributeDef(attrDefId);
            return ret;
        } else {
            return this.attrDef;
        }
    }

    @Override
    public void setAttributeDefinition(IAttributeDef ad) {
        //client side
        this.attrDef = ad;
        this.attrDefId = attrDef.getGUID();        
		//server side
        //	AttributeDef atrDef = (AttributeDef) ad;
        //	AttributeDefinition attrDef = atrDef.convertAttributeDefinition(); 
        //	Attribute atr = new Attribute();

        //AttributeClient atClient = new AttributeClient(api);
        //	Attribute attribute = atClient.readAttribute(this.id, null);
        //attribute.setDefinitionId(attrDef.getId()); 
        //	atClient.update(atr);
    }

    @Override
    public void addValue(IValue value) {
        //client side
        if (this.values != null) {
            this.values.add(value);
        } else {
            List<IValue> vals = new ArrayList();
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

    @Override
    public void removeValue(long valueID) {
        //remove from server side
        AttributeClient attrCl = new AttributeClient(api);
        Attribute attr = attrCl.readAttribute(this.id, null);
        Value atrVal = attrCl.readValue(attr.getId(), valueID, null);
        attrCl.delete(atrVal);
        //client side
        ArrayList<IValue> vals = (ArrayList) this.values;
        for (IValue val : vals) {
            if (val.getLocalID() == valueID) {
                vals.remove(val);
                return;
            }
        }
    }

    @Override
    public List<IValue> getValues() {
        return this.values;
    }

    @Override
    public IValue getFirstValue() {
        return this.values.iterator().next();
    }

    @Override
    public Long getValuesCount() {
        return (long) values.size();
    }

    private List<IValue> convertToValueODR(List<Value> vals) {
    	
        List<IValue> ret = new ArrayList();
        for (Value val : vals) {
            ValueODR value = new ValueODR(val);
            ret.add(value);
            value.setVocabularyId(val.getVocabularyId());
        }
        return ret;
    }

    public Attribute convertToAttribute() {

        Attribute attribute = new Attribute();        
        attribute.setId(this.id);
        attribute.setInstanceId(this.instanceID);
        attribute.setDefinitionId(this.attrDefId);
        attribute.setValues(this.convertValuesList());

        return attribute;
    }

    private List<Value> convertValuesList() {
        List<IValue> valuesODR = this.values;
        List<Value> vals = new ArrayList();
        for (IValue val : valuesODR) {
            ValueODR valODR = (ValueODR) val;
            Value value = valODR.convertToValue();
            vals.add(value);
        }
        return vals;
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
        Long vid = attr.getValues().iterator().next().getId();
        val.setId(vid);

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

    @Override
    public Long getLocalID() {
        return this.id;
    }

    public String getAttributeDefURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/attributes/" + this.id
                + "?locale=" + (WebServiceURLs.getClientProtocol()).getLocale();
        return url;
    }

    @Override
    public IAttributeDef getAttrDef() {
        return getAttributeDefinition();
    }

}
