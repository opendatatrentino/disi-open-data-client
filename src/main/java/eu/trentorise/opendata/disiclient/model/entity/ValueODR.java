package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClients;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import eu.trentorise.opendata.semantics.model.entity.IValue;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 *
 */
public class ValueODR extends Value implements IValue {

    private Long id;
    private Long attrId;
    private Object value;    

    @Override
    public String toString() {
        return "ValueODR [id=" + id + ", attrId=" + attrId + ", value=" + value
                + "]";
    }

    public ValueODR(Long id, Long attrId, Object value) {
        this.id = id;
        this.attrId = attrId;
        this.value = value;
    }
    

    public ValueODR(Value value) {
        this.id = value.getId();
        this.attrId = value.getAttributeId();

        if (value.getClass().equals(Name.class)) {
            Instance instance = (Instance) this.value;
            //System.out.println(value.toString());
            
            StructureODR structure = DisiClients.getSingleton().getEntityService().readName(instance.getId());
            //Structure structure = 
            this.value = structure;
        } else {
            this.value = value.getValue();
        }

    }


    @Override
    public Long getLocalID() {
        return this.id;
    }

    @Override
    public Object getValue() {
        if (this.value != null) {
            if (value.getClass().equals(Name.class)) {
                Instance instance = (Instance) this.value;
                
                StructureODR name = DisiClients.getSingleton().getEntityService().readName(instance.getId());
                this.value = name;
            }
            if (value.getClass().equals(it.unitn.disi.sweb.webapi.model.eb.Structure.class)) {
                StructureODR s = new StructureODR();
                s = s.convertToStructure((it.unitn.disi.sweb.webapi.model.eb.Structure) value);
                this.value = s;
            }

            return this.value;
        } else {
            AttributeClient attrClient = new AttributeClient(SwebConfiguration.getClientProtocol());
            this.value = attrClient.readValue(this.attrId, this.id, null).getValue();

        }
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    public Value convertToValue() {
        Value val = new Value();
        if (super.getId() != null) {
            val.setId(super.getId());
        } else {
            val.setId(this.id);
        }
        if (super.getValue() != null) {
            val.setValue(super.getValue());
        } else {
            val.setValue(this.value);
        }
        if (super.getVocabularyId() != null) {
            val.setVocabularyId(super.getVocabularyId());
        } else {
        	 val.setVocabularyId(1L);
        }

        
        val.setAttributeId(super.getAttributeId());
        return val;
    }

}
