package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.urlToEntityID;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DictFactory;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.semtext.SemText;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Duration;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Moment;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Structure;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class EntityODR extends StructureODR implements IEntity {

    private static final Logger logger = LoggerFactory.getLogger(EntityODR.class.getName());

    public static final Long PART_OF_CONCEPT_ID1 = 5l;
    public static final Long PART_OF_CONCEPT_ID2 = 22l;

    private List<Name> names;

    private Map<String, List<SemText>> descriptions;

    private Moment start;

    private Moment end;

    private Duration duration;

    private Long classConceptId;

    private Long partOfId;

    private Long globalId;

    private Long localId;

    private String sUrl;

    private IEntityType etype;

    private IProtocolClient api;

    public EntityODR() {
    }

    public EntityODR(IProtocolClient api, Entity entity) {

        this.api = api;
        super.setId((Long) entity.getId());
        this.setTypeId(entity.getTypeId());
        super.setEntityBaseId(entity.getEntityBaseId());
        List<Attribute> attrs = entity.getAttributes();

        logger.warn("TRYING TO CREATE EntityODR FROM SWEB Entity IN EntityODR CONSTRUCTOR. TODO REVIEW THIS CODE!");
        for (Attribute at : attrs) {
            if (at.getConceptId() == null) {
                continue;
            } else if (at.getConceptId() == KnowledgeService.DESCRIPTION_CONCEPT_ID) {
                List<Value> vals = at.getValues();
                List<Value> fixedVals = new ArrayList<Value>();

                for (Value val : vals) {
                    if (val.getValue() instanceof SemText) {
                        fixedVals.add(val);
                    } else {                        
                        SemText semtext = NLPService.getSemanticStringConverter().semText((SemanticString) val.getSemanticValue(), true);
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
                logger.warn("IN EntityODR CONSTRUCTOR: WE NEED GENERIC CODE FOR RELATIONAL ATTRIBUTES! TODO REVIEW!");
                if ((at.getConceptId() == PART_OF_CONCEPT_ID1) && (at.getValues().size() != 0)) { // todo hardcoded long
                    List<Value> vals = at.getValues();
                    List<Value> fixedVals = new ArrayList<Value>();
                    EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
                    logger.info("PART_OF attrbiute can not have multiple values, we take the first (and only) one");
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

        super.setAttributes(attrs);
        this.setNames(entity.getNames());
        this.setDescriptions(convertDescriptionToODR(entity.getDescriptions()));
        this.setPartOfId(entity.getPartOfId());
        this.setURL(entity.getsUrl());
        this.setDuration(entity.getDuration());
        this.setStart(entity.getStart());
        this.setEnd(entity.getEnd());
        this.setClassConceptId(entity.getClassConceptId());
        this.setGlobalId(entity.getGlobalId());
        //this.setId(entity.);

    }

    /**
     * IT'S NEVER CALLED - TODO REVIEW
     */
    private void fixConcept(Attribute at) {
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
    }

    //	public EntityODR(IProtocolClient api, Instance instance){
    //
    //		this.api=api;
    //		super.setId(instance.getId());
    //		this.setTypeId(instance.getTypeId());
    //		super.setEntityBaseId(instance.getEntityBaseId()) ;
    //	}
    private void setClassConceptId(Long classConceptId) {
        this.classConceptId = classConceptId;

    }

    @Override
    public String toString() {
        String str = "EntityODR [\n" + "id" + super.getId()
                + "names=" + names
                + ", descriptions=" + descriptions
                + ", start=" + start
                + ", end=" + end + ", duration="
                + duration + ", classConceptId="
                + classConceptId
                + ", partOfId=" + partOfId
                + ", globalId=" + globalId
                + ", sUrl=" + sUrl + ","
                + "\n etype=" + etype + ",\nattributes="
                + "[\n";
        for (IAttribute attr : super.getStructureAttributes()) {
            str += "\t" + attr + "\n";
        }

        str += "]";
        return str;
    }

    public Long getGUID() {
        return globalId;
    }

    public Long getLocalID() {

        if (super.getId() != null) {
            return super.getId();
        } else {
            return this.localId;
        }

    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public String getURL() {
        if (this.localId != null) {
            return WebServiceURLs.entityIDToURL(this.localId);
        } else {
            return WebServiceURLs.entityIDToURL(this.getId());
        }
    }

    public void setURL(String sUrl) {
        this.sUrl = sUrl;
    }

    public List<Name> getNames() {
        return names;
    }

    public Dict getName() {

        if ((this.names == null) && (super.getId() == null)) {
            return Dict.of();
        } else if (this.names == null) {
            EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
            EntityODR e = (EntityODR) es.readEntity(super.getId());
            this.names = e.getNames();
            this.descriptions = e.getDescriptions();
            this.classConceptId = e.getClassConceptId();
        }

        return DictFactory.namesToDict(this.names);
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public Map<String, List<SemText>> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<String, List<SemText>> descriptions) {
        this.descriptions = descriptions;
    }

    public Long getClassConceptId() {
        return classConceptId;
    }

    public void setClassConceptId(Long classConceptId, AttributeDef classAttributeDef) {
        this.classConceptId = classConceptId;
        List<IAttribute> attributes = super.getStructureAttributes();
        if (attributes.size() != 0) {
            for (IAttribute attr : attributes) {
                AttributeDef ad = (AttributeDef) attr;
                if (ad.getName(Locale.ENGLISH).equals("Class")) {
                    ValueODR val = new ValueODR();
                    val.setValue(classConceptId);
                    attr.addValue(val);
                } else {
                    ValueODR val = new ValueODR();
                    val.setValue(classConceptId);
                    AttributeODR at = new AttributeODR(classAttributeDef, val);
                    attributes.add(at);
                }
            }
        } else {
            List<IAttribute> attrs = new ArrayList<IAttribute>();
            ValueODR val = new ValueODR();
            val.setValue(classConceptId);
            AttributeODR at = new AttributeODR(classAttributeDef, val);
            attrs.add(at);
            super.setStructureAttributes(attrs);
        }

    }

    public Long getPartOfId() {
        return partOfId;
    }

    public void setPartOfId(Long partOfId) {
        this.partOfId = partOfId;
    }

    public Moment getStart() {
        return start;
    }

    public void setStart(Moment start) {
        this.start = start;
    }

    public Moment getEnd() {
        return end;
    }

    public void setEnd(Moment end) {
        this.end = end;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public List<IAttribute> getEntityAttributes() {
        if (super.getAttributes() != null) {
            List<IAttribute> atrs = convertToAttributeODR(super.getAttributes());
            return atrs;
        } else {
            AttributeClient attrCl = new AttributeClient(this.api);
            Long id = super.getId();
            if (id == null || getTypeId() == null) {
                return new ArrayList();
            } else {
                List<Attribute> attrs = attrCl.readAttributes(id, null, null);
                super.setAttributes(attrs);
                List<IAttribute> attrsODR = convertToAttributeODR(attrs);
                return attrsODR;
            }
        }
    }

    public void setEntityAttributes(List<IAttribute> attributes) {
        //client side
        super.setAttributes(convertToAttributes(attributes));
        //server side
        //		InstanceClient instanceCl= new  InstanceClient(api);
        //		Instance instance = instanceCl.readInstance(super.getId(), null);
        //		List<Attribute> attrs = new ArrayList<Attribute>();
        //		for (IAttribute attr:attributes ){
        //			AttributeODR attrODR = (AttributeODR)attr;
        //			attrs.add(attrODR.convertToAttribute());
        //		}
        //		instance.setAttributes(attrs);
        //instanceCl.update(instance);
    }

    public void addAttribute(IAttribute attribute) {
        //client side
        AttributeODR attrODR = (AttributeODR) attribute;
        Attribute attr = attrODR.convertToAttribute();
        List<Attribute> attrs = super.getAttributes();
        attrs.add(attr);
        super.setAttributes(attrs);
        //server side - create attr 
        AttributeClient attrCl = new AttributeClient(api);
        attrCl.create(attr);
        // add attr to the list copyOf existing attrs

    }

    public IEntityType getEtype() {
        if (this.etype != null) {
            return this.etype;
        } else {
            ComplexTypeClient ctc = new ComplexTypeClient(this.api);
            Long typeId = super.getTypeId();
            if (typeId == null) {
                return null;
            }
            ComplexType ctype = ctc.readComplexType(typeId, null);
            EntityType etype = new EntityType(ctype);
            this.etype = etype;
        }
        return etype;
    }

    public void setEtype(IEntityType type) {
        //locally
        EntityType etype = (EntityType) type;
        this.etype = etype;
        super.setTypeId(etype.getGUID());
        //on the server
        //		InstanceClient instanceCl= new  InstanceClient(this.api);
        //		Instance instance = instanceCl.readInstance(super.getId(), null);
        //		instance.setTypeId(type.getGUID());
        //	instanceCl.update(instance);
    }

    private List<IAttribute> convertToAttributeODR(List<Attribute> attributes) {
        List<IAttribute> attributesODR = new ArrayList<IAttribute>();
        for (Attribute attr : attributes) {
            AttributeODR attrODR = new AttributeODR(api, attr);
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

    public Entity convertToEntity() {
        Entity entity = new Entity();
        entity.setTypeId(this.getTypeId());
        entity.setDuration(this.duration);
        List<Attribute> attrs = super.getAttributes();
        if (attrs != null) {
            List<Attribute> attrsFixed = new ArrayList<Attribute>();

            for (Attribute at : attrs) {
                Attribute atFixed = new Attribute();
                atFixed.setCategoryId(at.getCategoryId());
                atFixed.setConceptId(at.getConceptId());
                atFixed.setDataType(at.getDataType());
                atFixed.setDefinitionId(at.getDefinitionId());
                atFixed.setId(at.getId());
                atFixed.setInstanceId(at.getInstanceId());
                atFixed.setName(at.getName());

                if ((at.getConceptId() != null) && (at.getConceptId() == KnowledgeService.DESCRIPTION_CONCEPT_ID)) {
                    List<Value> vals = at.getValues();
                    List<Value> fixedVals = new ArrayList<Value>();

                    for (Value val : vals) {
                        if (val.getValue() instanceof String) {
                            fixedVals.add(val);
                            logger.warn("No vocabulary is provided. Vocabulary is set to default '1");
                            val.setVocabularyId(1L);
                        } else {
                            EntityService es = new EntityService();
                            HashMap<String, Long> vocabularyMap = es.getVocabularies();

                            SemText st = (SemText) val.getValue();
                            SemanticString sstring = NLPService.getSemanticStringConverter().semanticString(st);
                            Locale l = st.getLocale();
                            Long vocabularyID = vocabularyMap.get(OdtUtils.localeToLanguageTag(l));

                            Value fixedVal = new Value();
                            fixedVal.setSemanticValue(sstring);
                            fixedVal.setValue(sstring.getText());
                            fixedVal.setId(val.getId());
                            fixedVals.add(fixedVal);

                            fixedVal.setVocabularyId(vocabularyID);
                        }
                    }
                    atFixed.setValues(fixedVals);
                } else {
                    List<Value> fixedVals = new ArrayList<Value>();
                    for (Value val : at.getValues()) {
                        if (val.getValue() instanceof EntityODR) {
                            EntityODR enodr = (EntityODR) val.getValue();
                            Entity en = enodr.convertToEntity();
                            Value fixedVal = new Value();
                            fixedVal.setValue(en);
                            fixedVal.setId(val.getId());
                            fixedVals.add(fixedVal);
                        } else if (val.getValue() instanceof StructureODR) {
                            StructureODR structureODR = (StructureODR) val.getValue();
                            Structure ebStr = structureODR.convertToSwebStructure(structureODR);
                            Value fixedVal = new Value();
                            fixedVal.setValue(ebStr);
                            fixedVal.setId(val.getId());
                            fixedVals.add(fixedVal);
                        } else {
                            fixedVals.add(val);
                        }
                    }

                    atFixed.setValues(fixedVals);

                }

                attrsFixed.add(atFixed);
            }

            entity.setAttributes(attrsFixed);
        }
        entity.setDescriptions(convertDescriptionToSWEB(this.descriptions));
        entity.setEnd(this.end);
        entity.setGlobalId(this.globalId);
        entity.setId(super.getId());
        entity.setNames(this.names);
        entity.setEntityBaseId(this.getEntityBaseId());
        entity.setStart(this.start);
        entity.setPartOfId(this.partOfId);
        entity.setEntityBaseId(this.getEntityBaseId());
        entity.setsUrl(this.sUrl);

        return entity;
    }

    public String getName(Locale locale) {
        logger.info("Returing the first value of the name. Hovewer, the number of values is: " + this.names.size());
        Map<String, List<String>> name = this.names.get(0).getNames();
        List<String> stName = name.get(OdtUtils.localeToLanguageTag(locale));
        return stName.get(0);
    }

    public void setName(Locale locale, List<String> names) {
        //	this.names = names;
        throw new UnsupportedOperationException("todo to implement");

    }

    public Dict getDescription() {
        if (this.descriptions == null) {
            return Dict.of();
        }
        return DictFactory.semtextsToDict(this.descriptions);
    }

    /**
     * Converts description attribute from EntityPedia datatype to ODR datatype.
     */
    private Map<String, List<SemText>> convertDescriptionToODR(Map<String, List<SemanticString>> descriptionSString) {

        Map<String, List<SemText>> odrDescriptionMap = new HashMap<String, List<SemText>>();
        if (descriptionSString == null) {
            return odrDescriptionMap;
        }        
        

        Iterator<?> it = descriptionSString.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            List<SemText> sTextList = new ArrayList<SemText>();

            List<SemanticString> SStringList = (List<SemanticString>) pairs.getValue();
            for (SemanticString sstring : SStringList) {
                SemText stext = NLPService.getSemanticStringConverter().semText(sstring, true);
                sTextList.add(stext);
            }
            odrDescriptionMap.put((String) pairs.getKey(), sTextList);
            it.remove(); // avoids a ConcurrentModificationException
        }

        return odrDescriptionMap;

    }



    public Map<String, List<SemanticString>> convertDescriptionToSWEB(Map<String, List<SemText>> descriptionSText) {

        Map<String, List<SemanticString>> epDescriptionMap = new HashMap<String, List<SemanticString>>();
        if (descriptionSText == null) {
            return epDescriptionMap;
        }
        Iterator<?> it = descriptionSText.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            List<SemanticString> sStringList = new ArrayList<SemanticString>();

            List<SemText> SStringList = (List<SemText>) pairs.getValue();
            for (SemText stext : SStringList) {
                SemanticString sstring = NLPService.getSemanticStringConverter().semanticString(stext);

                sStringList.add(sstring);
            }
            epDescriptionMap.put((String) pairs.getKey(), sStringList);
            it.remove(); // avoids a ConcurrentModificationException
        }

        return epDescriptionMap;
    }

    public void setName(Locale locale, String name) {
        throw new UnsupportedOperationException("todo to implement");

    }

    public void setDescription(Locale language, String description) {
        throw new UnsupportedOperationException("todo to implement");

    }

    /**
     * Converts from object in values of IEntity to disi client format.
     */
    private static Object disifyObject(Object obj) {
        if (obj instanceof IEntity) {
            return disify((IEntity) obj, false);
        } else if (obj instanceof IStructure) {
            return disifyStructure((IStructure) obj);
        } else {
            return obj;
        }
    }

    /**
     * Converts from IStructure to disi client format.
     */
    private static Map<IAttributeDef, Object> disifyStructure(IStructure structure) {

        HashMap<IAttributeDef, Object> map = new HashMap<IAttributeDef, Object>();

        for (IAttribute subattr : structure.getStructureAttributes()) {
            for (IValue val : subattr.getValues()) {
                map.put(subattr.getAttrDef(), disifyObject(val.getValue()));
            }

        }
        return map;
    }

    /**
     * Converts from IEntity to disi client format.
     *
     * @param root When true, all first level attributes are copied to output
     * entity. Eventual subentities in IValue are copied as non-root. When
     * false, only URL and etype are copied to output entity.
     * @throws IllegalArgumentException if provided entity is not valid
     */
    public static EntityODR disify(IEntity entity, boolean root) {

        if (entity instanceof EntityODR) {
            return (EntityODR) entity;
        }

        EntityODR enodr = new EntityODR();

        EntityService es = new EntityService();
        List<IAttribute> newAttrs = new ArrayList<IAttribute>();

        if (root) {
            Checker.checkEntity(entity, true);
            Object nameAttrDefURL = entity.getEtype().getNameAttrDef().getURL();
            for (IAttribute attr : entity.getStructureAttributes()) {
                if (attr.getValuesCount() > 0) {
                    IAttributeDef attrDef = attr.getAttrDef();
                    AttributeODR attrODR;

                    List<Object> objects = new ArrayList<Object>();

                    if (DataTypes.ENTITY.equals(attrDef.getDataType())) {
                        List<EntityODR> ensODR = new ArrayList();
                        for (IValue v : attr.getValues()) {
                            ensODR.add(disify((IEntity) v.getValue(), false));
                        }
                        attrODR = es.createAttribute(attrDef, ensODR);
                        newAttrs.add(attrODR);
                    } else {

                        if (attrDef.getURL().equals(nameAttrDefURL)) {
                            objects.add(entity.getName().anyString(new DisiEkb().getDefaultLocales())); // todo find way to link entity service to DisiEkb
                        } else {
                            for (IValue val : attr.getValues()) {
                                objects.add(disifyObject(val.getValue()));
                            }
                        }

                        attrODR = es.createAttribute(attrDef, objects);
                        newAttrs.add(attrODR);
                    }

                }

            }

            enodr.setEntityAttributes(newAttrs);
            enodr.setEtype(entity.getEtype());
        }

        /*
         if (entity.getEtype() == null) {
         throw new IllegalArgumentException("Provided entity must have etype! Entity URL is " + entity.getURL());
         }
         */
        logger.warn("SETTING HARD CODED ENTITY BASE ID = 1 IN DISIFY METHOD.");
        enodr.setEntityBaseId(1L);

        logger.info("disifying entity.getURL = " + entity.getURL());

        if (entity.getURL() != null && entity.getURL().length() > 0 && !(es.isTemporaryURL(entity.getURL()))) {
            enodr.setId(urlToEntityID(entity.getURL()));
            enodr.setURL(entity.getURL());
        }

        return enodr;
    }

}
