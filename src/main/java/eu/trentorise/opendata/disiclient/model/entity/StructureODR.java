package eu.trentorise.opendata.disiclient.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;


public class StructureODR extends Instance implements IStructure {    

    public Long getLocalID() {
        return super.getId();
    }

    public StructureODR() {
    }


    public List<IAttribute> getStructureAttributes() {
        if (super.getAttributes() != null) {
            List<IAttribute> atrs = convertToAttributeODR(super.getAttributes());
            return atrs;
        } else {
            AttributeClient attrCl = new AttributeClient(getClientProtocol());
            List<Attribute> attrs = attrCl.readAttributes(super.getId(), null, null);
            super.setAttributes(attrs);
            List<IAttribute> attrODR = convertToAttributeODR(attrs);
            return attrODR;
        }
    }

    public void setStructureAttributes(List<IAttribute> attributes) {
        super.setAttributes(convertToAttributes(attributes));
    }

    public IEntityType getEtype() {
        EntityTypeService ets = new EntityTypeService();
        Long id = super.getTypeId();
        if (id == null) {
            throw new RuntimeException("Got a null id for super.getTypeId() in Structure.java!");
        } else {
            return ets.getEntityType((long) id);
        }
    }

    public void setEtype(IEntityType type) {
        throw new UnsupportedOperationException("todo to implement");
    }

    private List<IAttribute> convertToAttributeODR(List<Attribute> attributes) {
        List<IAttribute> attributesODR = new ArrayList<IAttribute>();
        for (Attribute attr : attributes) {
            AttributeODR attrODR = new AttributeODR(getClientProtocol(), attr);
            attributesODR.add(attrODR);
        }
        return attributesODR;
    }

    public List<Attribute> convertToAttributes(List<IAttribute> attributes) {
        List<Attribute> attrs = new ArrayList<Attribute>();
        for (IAttribute attr : attributes) {
            AttributeODR attribute = (AttributeODR) attr;
            Attribute at = attribute.convertToAttribute();
            attrs.add(at);
        }
        return attrs;
    }

    private IProtocolClient getClientProtocol() {
        return WebServiceURLs.getClientProtocol();
    }

    public String getURL() {
        String fullUrl = WebServiceURLs.getURL();
        //if(super.getId()!=null){
        String url = fullUrl + "/instances/" + super.getId();
        return url;
    }

    public void setURL(String url) {
        throw new UnsupportedOperationException("todo to implement");

    }

    public IAttribute getAttribute(String attrDefURL) {
        List<IAttribute> attributes = getStructureAttributes();
        for (IAttribute attribute : attributes) {
            if (attribute.getAttributeDefinition().getURL().equals(attrDefURL)) {
                return attribute;
            }
        }
        throw new DisiClientException("There is no attribute having attributeDef URL: " + attrDefURL + " in the structure with URL " + getURL());
    }

    public String getEtypeURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/types/" + super.getTypeId();
        return url;
    }

    public StructureODR convertToStructure(it.unitn.disi.sweb.webapi.model.eb.Structure st) {
        StructureODR s = new StructureODR();
        s.setAttributes(st.getAttributes());
        s.setEntityBaseId(st.getEntityBaseId());
        s.setTypeId(st.getTypeId());
        s.setId(st.getId());
        return s;
    }

    public it.unitn.disi.sweb.webapi.model.eb.Structure convertToSwebStructure(StructureODR s) {

        it.unitn.disi.sweb.webapi.model.eb.Structure strSweb = new it.unitn.disi.sweb.webapi.model.eb.Structure();
        List<Attribute> attrs = s.getAttributes();
        List<Attribute> attrsFixed = new ArrayList<Attribute>();

        for (Attribute a : attrs) {
            Attribute atFixed = new Attribute();
            atFixed.setCategoryId(a.getCategoryId());
            atFixed.setConceptId(a.getConceptId());
            atFixed.setDataType(a.getDataType());
            atFixed.setDefinitionId(a.getDefinitionId());
            atFixed.setId(a.getId());
            atFixed.setInstanceId(a.getInstanceId());
            atFixed.setName(a.getName());

            if ((a.getValues().get(0).getValue() instanceof EntityODR)) {
                List<Value> vals = a.getValues();
                List<Value> valsF = new ArrayList();
                for (Value v : vals) {
                    EntityODR e = (EntityODR) v.getValue();
                    Value vf = new Value();
                    vf.setValue(e.convertToEntity());
                    valsF.add(vf);
                }
                atFixed.setValues(valsF);

            } else if (((a.getValues().get(0).getValue() instanceof StructureODR))) {
                List<Value> vals = a.getValues();
                List<Value> valsF = new ArrayList();

                for (Value v : vals) {
                    StructureODR strODR = (StructureODR) v.getValue();
                    it.unitn.disi.sweb.webapi.model.eb.Structure strFixed = convertToSwebStructure(strODR);

                    Value vf = new Value();
                    vf.setValue(strFixed);
                    valsF.add(vf);
                }
                atFixed.setValues(valsF);
            } else {
                List<Value> vals = a.getValues();
                List<Value> valsF = new ArrayList();

                for (Value v : vals) {
                    Value vf = new Value();                    
                    vf.setValue(v.getValue());
                    valsF.add(vf);
                }
                atFixed.setValues(valsF);
            }
            attrsFixed.add(atFixed);
        }
        strSweb.setAttributes(attrsFixed);

        strSweb.setEntityBaseId(s.getEntityBaseId());
        strSweb.setId(s.getId());
        strSweb.setTypeId(s.getTypeId());

        return strSweb;
    }
}
