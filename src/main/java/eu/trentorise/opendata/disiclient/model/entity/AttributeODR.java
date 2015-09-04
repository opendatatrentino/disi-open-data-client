package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClients;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IValue;

/**
 *
 * Dav 0.12 WARNING: removed conceptId, which was only used to convert to
 * Attribute This could cause breakages.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 */
public class AttributeODR implements IAttribute {

    private List<IValue> values;
    private Long id;
    private Long attrDefId;
    private Long instanceID;

    public AttributeODR(Attribute attribute) {
        if (attribute.getId() != null) {
            this.id = attribute.getId();
        }
        this.attrDefId = attribute.getDefinitionId();
        this.instanceID = attribute.getInstanceId();
        this.values = convertToValueODR(attribute.getValues());
    }

    public AttributeODR() {
    }

    public AttributeODR(Long attrDefId, ValueODR val) {

        this.attrDefId = attrDefId;

        List<IValue> vals = new ArrayList();
        vals.add(val);
        this.values = vals;

    }

    public AttributeODR(Long attrDefId, List<ValueODR> val) {
        this.attrDefId = attrDefId;
        List<IValue> vals = new ArrayList();
        vals.addAll(val);
        this.values = vals;

    }

    public Long getGUID() {
        return id;
    }

    public void addValue(IValue value) {
        //client side
        if (this.values != null) {
            this.values.add(value);
        } else {
            List<IValue> vals = new ArrayList();
            vals.add(value);
            this.values = vals;
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
        AttributeClient attrCl = new AttributeClient(SwebConfiguration.getClientProtocol());
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

    @Override
    public String getAttrDefUrl() {
        return DisiClients.getClient().getEntityTypeService().readAttrDef(attrDefId).getURL();
    }

}
