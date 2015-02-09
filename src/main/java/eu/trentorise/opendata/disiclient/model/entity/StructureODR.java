package eu.trentorise.opendata.disiclient.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.semtext.SemText;


public class StructureODR extends Instance implements IStructure {    
	 public static final Long PART_OF_CONCEPT_ID1 = 5l;
	    public static final Long PART_OF_CONCEPT_ID2 = 22l;
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
        //++++++++++++++
        for (Attribute at : attributes) {
            if (at.getConceptId() == null) {
                continue;
            } else if (at.getConceptId() == KnowledgeService.DESCRIPTION_CONCEPT_ID) {
                List<Value> vals = at.getValues();
                List<Value> fixedVals = new ArrayList<Value>();

                for (Value val : vals) {
                    if (val.getValue() instanceof SemText) {
                        fixedVals.add(val);
                    } else {
                        SemText semtext = NLPService.getSemanticStringConverter().semText((SemanticString) val.getSemanticValue());
                        Locale loc = OdtUtils.languageTagToLocale(val.getLanguageCode()); // dav so java 6 doesn't bother us Locale.forLanguageTag(val.getLanguageCode());
                        SemText stext = semtext.with(loc);
                        Value fixedVal = new Value();
                        fixedVal.setValue(stext);
                        fixedVal.setId(val.getId());
                        fixedVals.add(fixedVal);
                    }
                }
                at.setValues(fixedVals);
            } else if (at.getValues().get(0).getValue() instanceof Name) {
                List<Value> values = at.getValues();
                for (Value v : values) {
                    Name n = (Name) v.getValue();
                    List<Attribute> atrs = n.getAttributes();
                    for (Attribute a : atrs) {

                        if (a.getDataType() == DataType.CONCEPT) {
                            List<Value> vals = a.getValues();
                            List<Value> fixedVals = new ArrayList<Value>();

                            for (Value val : vals) {

                                if (val.getValue().getClass().equals(ConceptODR.class)) {
                                    fixedVals.add(val);
                                    continue;
                                }
                                Concept c = (Concept) val.getValue();
                                ConceptODR codr = new ConceptODR(c);

                                ValueODR fixedVal = new ValueODR();
                                fixedVal.setId(val.getId());
                                // fixedVal.setDataType(IConcept.class);
                                fixedVal.setValue(codr);
                                fixedVals.add(fixedVal);
                            }
                            a.setValues(fixedVals);
                        }
                    }
                }
            } else if (at.getDataType() == DataType.CONCEPT) {
                List<Value> vals = at.getValues();
                List<Value> fixedVals = new ArrayList<Value>();

                for (Value val : vals) {

                    if (val.getValue().getClass().equals(ConceptODR.class)) {
                        fixedVals.add(val);
                        continue;
                    }
                    Concept c = (Concept) val.getValue();
                    ConceptODR codr = new ConceptODR(c);

                    ValueODR fixedVal = new ValueODR();
                    fixedVal.setId(val.getId());
                    // fixedVal.setDataType(IConcept.class);
                    fixedVal.setValue(codr);
                    fixedVals.add(fixedVal);
                }
                at.setValues(fixedVals);
            } else {
                if ((at.getConceptId() == PART_OF_CONCEPT_ID1) && (at.getValues().size() != 0)) { // todo hardcoded long
                    List<Value> vals = at.getValues();
                    List<Value> fixedVals = new ArrayList<Value>();
                    EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
                    if (vals.size() > 0) {
                        Instance inst = (Instance) vals.get(0).getValue();
                        IEntity e = es.readEntity(inst.getId());
                        //	EntityODR enodr = new EntityODR(WebServiceURLs.getClientProtocol(), e);

                        for (Value v : vals) {
                            Value fixedVal = new Value();
                            fixedVal.setId(v.getId());
                            fixedVal.setValue(e);
                            fixedVals.add(fixedVal);
                        }
                    }
                    at.setValues(fixedVals);
                } else if (at.getConceptId() == 111001L) { // todo hardcoded long
                    List<Value> vals = at.getValues();
                    List<Value> fixedVals = new ArrayList<Value>();
                    EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
                    for (Value v : vals) {
                        Instance inst = (Instance) v.getValue();
                        IStructure e = es.readStructure(inst.getId());

                        Value fixedVal = new Value();
                        fixedVal.setId(v.getId());
                        fixedVal.setValue(e);

                        fixedVals.add(fixedVal);

                        at.setValues(fixedVals);
                    }
                }
            }
        }
        
        //++++++++++++++
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
