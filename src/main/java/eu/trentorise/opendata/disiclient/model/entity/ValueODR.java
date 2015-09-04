package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClients;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 *
 */
public class ValueODR implements IValue {

    private static final Logger LOG = LoggerFactory.getLogger(ValueODR.class.getName());

    private Long localId;

    @Nullable
    private Object value;

    private Long attributeId;

    @Override
    public String toString() {
        return "ValueODR [id=" + localId + ", attrId=" + attributeId + ", value=" + value
                + "]";
    }

    public ValueODR(Long id, Long attributeId, Object value) {
        this.localId = id;
        this.attributeId = attributeId;
        this.value = value;
    }

    public ValueODR(Value value) {
        localId = value.getId();
        this.attributeId = value.getAttributeId();

        if (value.getClass().equals(Name.class)) {
            Instance instance = (Instance) value.getValue();
            //System.out.println(value.toString());

            StructureODR structure = DisiClients.getSingleton().getEntityService().readName(instance.getId());
            this.value = structure;
        } else {
            this.value = value.getValue();
        }

    }

    @Override
    public Long getLocalID() {
        return this.localId;
    }

    @Override
    public Object getValue() {
        if (this.value == null) {
            AttributeClient attrClient = new AttributeClient(SwebConfiguration.getClientProtocol());
            this.value = attrClient.readValue(this.attributeId, this.localId, null).getValue();

        } else {
            if (value.getClass().equals(Name.class)) {

                Instance instance = (Instance) this.value;
                StructureODR name = DisiClients.getSingleton().getEntityService().readName(instance.getId());
                this.value = name;
               
            }
            if (value.getClass().equals(it.unitn.disi.sweb.webapi.model.eb.Structure.class)) {
                                
                this.value = new StructureODR((it.unitn.disi.sweb.webapi.model.eb.Structure) value);
                
            }

            return this.value;

        }
        return value;
    }

    public Value asSwebValue() {
        Value ret = new Value();

        ret.setId(this.localId);
        ret.setValue(this.value);

        LOG.warn("TODO - HARD CODING VOCABULARY ID WHILE CONVERTING TO SWEB VALUE!");
        ret.setVocabularyId(1L);

        ret.setAttributeId(this.attributeId);
        return ret;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

}
