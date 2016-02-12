package eu.trentorise.opendata.disiclient.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import eu.trentorise.opendata.disiclient.Converter;
import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.impl.model.SearchResult;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.AbstractApiClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.VocabularyClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;
import it.unitn.disi.sweb.webapi.model.kb.vocabulary.Vocabulary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityService implements IEntityService {

    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private IProtocolClient api;
    private DisiEkb disiEkb;

    @Nullable
    private InstanceClient instanceClient;
    public static String NEW_INSTANCE_PREFIX = "instances/new/";

    public EntityService(IProtocolClient api) {
        this.api = api;
    }

    public EntityService() {
        if (this.api == null) {
            api = WebServiceURLs.getClientProtocol();
        }
    }

    public Long createEntity(IEntity entity) {

        EntityODR ent = EntityODR.disify(entity, true);

        Entity e = ent.convertToEntity();
        InstanceClient instanceCl = new InstanceClient(this.api);
        logger.info(e.toString());
        EntityTypeService es = new EntityTypeService();
        EntityType etype = es.getEntityType(e.getTypeId());

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        Long attrDefClassAtrID = null;
        for (IAttributeDef adef : attrDefs) {

            if (adef.getName().getString(Locale.ENGLISH).equalsIgnoreCase("class")) {
                attrDefClassAtrID = adef.getGUID();
                break;
            }
        }

        boolean isExistAttrClass = false;

        for (Attribute a : e.getAttributes()) {

            if (a.getDefinitionId() == attrDefClassAtrID) {
                isExistAttrClass = true;
                break;
            }
        }

        if (!isExistAttrClass) {
            Attribute a = createClassAttribute(attrDefClassAtrID, etype.getConceptID());
            e.getAttributes().add(a);
            logger.warn("Default class attribute is assigned");
        }

        //System.out.println("Class exists: truw");
        Long id = null;

        // filthy hack to purge ids...
        e.setId(null);
        e.setGlobalId(null);
        for (Attribute a : e.getAttributes()) {
            a.setId(null);
            a.setInstanceId(null);
            for (Value v : a.getValues()) {
                v.setId(null);
                v.setAttributeId(null);
            }
        }

        try {
            id = instanceCl.create(e);
        }
        catch (NotFoundException ex) {
        }
        return id;

    }

    public Attribute createClassAttribute(Long attrDefClassAtrID, Long conceptID) {
        IAttributeDef atrDef = new AttributeDef(attrDefClassAtrID);
        
        ConceptODR concept = new KnowledgeService().readConcept(conceptID);
        AttributeODR atrODR = createAttribute(atrDef, concept);
        Attribute a = atrODR.convertToAttribute();
        return a;

    }

    public Long createEntity(Name name) {
        InstanceClient instanceCl = new InstanceClient(this.api);
        Long id = instanceCl.create(name);
        return id;
    }

    public void updateEntity(Name name) {

        //EntityODR ent = (EntityODR) name;
        //Entity en=(Entity)ent;
        InstanceClient instanceCl = new InstanceClient(this.api);
        //	Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
        //
        //		instance.setTypeId(ent.getEtype().getGUID());
        //		instance.setId(entity.getLocalID());
        //		List<IAttribute> attrs = entity.getStructureAttributes();
        //		List<Attribute> attributes = ent.convertToAttributes(attrs);
        //		instance.setAttributes(attributes);
        //Entity e = ent.convertToEntity();
        instanceCl.update(name);
    }

    public void deleteEntity(long entityID) {
        InstanceClient instanceCl = new InstanceClient(this.api);
        Instance instance = instanceCl.readInstance(entityID, null);
        instanceCl.delete(instance);
    }

    public void deleteEntity(String entityURL) {
        InstanceClient instanceCl = new InstanceClient(this.api);
        Long entityID = WebServiceURLs.urlToEntityID(entityURL);
        Instance instance = instanceCl.readInstance(entityID, null);
        instanceCl.delete(instance);
    }

    public EntityODR readEntity(long entityID) {
        InstanceClient instanceCl = new InstanceClient(this.api);

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);
        Instance instance = instanceCl.readInstance(entityID, instFilter);

        Entity entity = (Entity) instance;
        EntityODR en = new EntityODR(this.api, entity);
        //   IntegrityChecker.checkEntity(en);
        return en;
    }

    public Long readEntityGlobalID(long globalID) {
        InstanceClient instanceCl = new InstanceClient(this.api);
        Instance instance = instanceCl.readEntityByGloabalId(1L, globalID, null);

        Entity entity = (Entity) instance;
        return entity.getId();
    }

    public List<IEntity> readEntities(List<String> entityURLs) {

        if (entityURLs.size() == 0) {
            return new ArrayList<IEntity>();
        }

        List<Long> entityIDs = new ArrayList<Long>();

        for (String entityURL : entityURLs) {
            entityIDs.add(WebServiceURLs.urlToEntityID(entityURL));
        }

        InstanceClient instanceCl = new InstanceClient(this.api);

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        List instances = instanceCl.readInstancesById(entityIDs, instFilter);
        List<Entity> entities = (List<Entity>) instances;
        List<IEntity> ret = new ArrayList<IEntity>();
        for (Entity epEnt : entities) {
            EntityODR en = new EntityODR(this.api, epEnt);
            IntegrityChecker.checkEntity(en);
            ret.add(en);
        }
        return ret;
    }

    public StructureODR readName(long entityID) {
        InstanceClient instanceCl = new InstanceClient(this.api);

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        Instance instance = instanceCl.readInstance(entityID, instFilter);

        Name name = (Name) instance;
        StructureODR structureName = new StructureODR();
        List<Attribute> atrs = name.getAttributes();

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

        structureName.setAttributes(name.getAttributes());
        structureName.setId(name.getId());
        structureName.setTypeId(name.getTypeId());
        structureName.setEntityBaseId(name.getEntityBaseId());

        return structureName;
    }

    public StructureODR readStructure(long entityID) {
        InstanceClient instanceCl = new InstanceClient(this.api);

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        Instance instance = instanceCl.readInstance(entityID, instFilter);

        it.unitn.disi.sweb.webapi.model.eb.Structure structure = (it.unitn.disi.sweb.webapi.model.eb.Structure) instance;
        StructureODR ret = new StructureODR();
        ret.setAttributes(structure.getAttributes());
        ret.setTypeId(structure.getTypeId());
        ret.setEntityBaseId(1L);
        ret.setId(structure.getId());
        //EntityODR en = new EntityODR(this.api,entity);

        return ret;
    }

    public void addAttribute(IEntity entity, IAttribute attribute) {
        EntityODR ent = (EntityODR) entity;
        ent.addAttribute(attribute);
    }

    public void addAttributeValue(IEntity entity, IAttribute attribute,
            IValue value) {
        AttributeODR atrODr = (AttributeODR) attribute;
        atrODr.addValue(value);
        IAttribute atr = atrODr;
        EntityODR ent = (EntityODR) entity;
        ent.addAttribute(atr);

    }

    /**
     * @param value Note: can be a Collection
     */
    public AttributeODR createAttribute(IAttributeDef attrDef, Object value) {
        AttributeDef ad = (AttributeDef) attrDef;

        if (ad.getName(Locale.ENGLISH).equals("Name")) {
            return createNameAttributeODR(attrDef, value);

        } else if (ad.getName(Locale.ENGLISH).equals("Description")) {
            return createDescriptionAttributeODR(attrDef, value);

        } else if (attrDef.getDataType().equals(DataTypes.STRUCTURE)) {
            if (value instanceof Collection) { // notice in Java a Map is *NOT* an instance of Collection
                return createStructureAttribute(attrDef, (Collection) value);
            } else {
                List<HashMap<IAttributeDef, Object>> hashMaps = new ArrayList();
                hashMaps.add((HashMap<IAttributeDef, Object>) value);
                return createStructureAttribute(attrDef, hashMaps);
            }

        } else if (value instanceof Collection) {
            List<ValueODR> valsODR = new ArrayList<ValueODR>();
            for (Object obj : (Collection) value) {
                ValueODR valODR = new ValueODR();
                if (obj instanceof IEntity) {
                    valODR.setValue(EntityODR.disify((IEntity) obj, false));
                } else {
                    valODR.setValue(obj);
                }

                valsODR.add(valODR);
            }
            return new AttributeODR(attrDef, valsODR);
        } else {
            ValueODR valODR = new ValueODR();
            if (value instanceof IEntity) {
                valODR.setValue(EntityODR.disify((IEntity) value, false));
            } else {
                valODR.setValue(value);
            }
            return new AttributeODR(attrDef, valODR);
        }
    }

    /**
     * @param descr either a String or a SemanticText instance
     * @return the description as SemanticText
     * @throws IllegalArgumentException if descr is not of the proper type
     */
    private SemanticText descrToSemText(Object descr) {
        if (descr instanceof String) {
            /* david there should be only SemanticText 
             descr= new SemanticString();
             String s = (String) value;
             descr.setText(s); */
            return new SemanticText((String) descr);
        } else if (descr instanceof SemanticText) {
            /* david  there should be only SemanticText 
             SemanticText st= (SemanticText) value;
             descr = SemanticTextFactory.semanticString(st);
             */
            return (SemanticText) descr;
        } else {
            throw new IllegalArgumentException("Wrong value for the attribute is given! Accepted values are String and SemanticText.");
        }
    }

    /**
     *
     * @param descr either a String, a SemanticText, or a Collection of String
     * or SemanticText
     * @throws IllegalArgumentException if descr is not of the proper type
     */
    private AttributeODR createDescriptionAttributeODR(IAttributeDef attrDef,
            Object descr) {
        if (descr instanceof Collection) {
            List<ValueODR> valsODR = new ArrayList<ValueODR>();
            for (Object obj : (Collection) descr) {
                ValueODR valODR = new ValueODR();
                valODR.setValue(descrToSemText(obj));
                valsODR.add(valODR);
                logger.warn("HARDCODING VOCABULARY TO '1' WHILE BUILDING DESCRIPTION ATTRIBUTE");                
                valODR.setVocabularyId(1L);
            }
            return new AttributeODR(attrDef, valsODR);
        } else {
            ValueODR val = new ValueODR();
            val.setValue(descrToSemText(descr));
            logger.warn("HARDCODING VOCABULARY TO '1' WHILE BUILDING DESCRIPTION ATTRIBUTE");                
            val.setVocabularyId(1L);
            return new AttributeODR(attrDef, val);
        }
    }

    private StructureODR createStructure(IAttributeDef attrDef,
            HashMap<IAttributeDef, Object> atributes) {
        List<Attribute> attrs = new ArrayList<Attribute>();
        StructureODR attributeStructure = new StructureODR();
        logger.warn("Hardcoded entity base id 1");
        attributeStructure.setEntityBaseId(1L);

        AttributeDef adef = (AttributeDef) attrDef;
        attributeStructure.setTypeId(adef.getRangeEntityTypeID());

        for (Iterator<IAttributeDef> it = atributes.keySet().iterator(); it.hasNext();) {
            IAttributeDef ad = it.next();
            AttributeODR aodr = createAttribute(ad, atributes.get(ad));
            attrs.add(aodr.convertToAttribute());
        }
        attributeStructure.setAttributes(attrs);
        return attributeStructure;
    }

    private AttributeODR createStructureAttribute(IAttributeDef attrDef,
            Collection<HashMap<IAttributeDef, Object>> structs) {

        Attribute nAtr = new Attribute();
        nAtr.setDefinitionId(attrDef.getGUID());
        List<Value> values = new ArrayList<Value>();

        for (HashMap<IAttributeDef, Object> structMap : structs) {
            values.add(new Value(createStructure(attrDef, structMap)));
        }

        nAtr.setValues(values);

        AttributeODR a = new AttributeODR(api, nAtr);

        return a;
    }

    public HashMap<String, Long> getVocabularies() {
        HashMap<String, Long> mapVocabs = new HashMap<String, Long>();
        VocabularyClient vc = new VocabularyClient(api);        
        List<Vocabulary> vocabs = vc.readVocabularies(1L, null, null);
        for (Vocabulary v : vocabs) {
            mapVocabs.put(v.getLanguageCode(), v.getId());
        }
        return mapVocabs;
    }

    /**
     * Creates Attribute from Name.class
     *
     * @param name
     */
    public AttributeODR createNameAttribute(IAttributeDef attrDef, Name name) {

        Attribute nAtr = new Attribute();
        nAtr.setDefinitionId(attrDef.getGUID());
        List<Value> values = new ArrayList<Value>();
        values.add(new Value(name));
        nAtr.setValues(values);
        AttributeODR a = new AttributeODR(api, nAtr);
        return a;

    }

    /**
     *
     * @param name a String or an IDict
     * @return a Value representing the name
     * @throws IllegalArgumentException if name is not of the proper class
     */
    private List<Value> nameToValue(Object name) {
        List<Value> nameValues = new ArrayList();
        if (name instanceof String) {
            String nameInput = (String) name;
            logger.warn("No Locale is provided for name" + name + "The vocabulary is set to '1'");
            nameValues.add(new Value(nameInput, 1L));
            return nameValues;
        } else if (name instanceof IDict) {
            Dict nameDict = (Dict) name;
            HashMap<String, Long> vocabs = getVocabularies();
            Set<Locale> locs = nameDict.getLocales();
            for (Locale l : locs) {
                nameValues.add(new Value(nameDict.getString(l), vocabs.get(TraceProvUtils.localeToLanguageTag(l))));//dav so Java 6 doesn't bother us l.toLanguageTag())));
            }
            return nameValues;
        } else {
            throw new IllegalArgumentException("Wrong Name object is given. "
                    + "Name object should be an instance of String or IDict classes. Found instead instance of class " + name.getClass().getSimpleName());
        }

    }

    /**
     *
     * @param attrDef
     * @param name can be a String, an IDict or a Collection of String or IDict.
     */
    public AttributeODR createNameAttributeODR(IAttributeDef attrDef, Object name) {

        Attribute entityNameAttribute = new Attribute();
        entityNameAttribute.setDefinitionId(attrDef.getGUID());

        Name nameStructure = new Name();
        nameStructure.setEntityBaseId(1L);
        logger.warn("TODO HARDCODED ENTITY BASE ID TO 1.");
        long etypeID;
//		if(attrDef.getRangeEtypeURL()==null){
//			etypeID=10L;
//		}else 
        etypeID = WebServiceURLs.urlToEtypeID(attrDef.getRangeEtypeURL());
        nameStructure.setTypeId(etypeID);

        EntityTypeService ets = new EntityTypeService();
        EntityType etype = (EntityType) ets.readEntityType(WebServiceURLs.etypeIDToURL(etypeID));
        List<IAttributeDef> etypeAtrDefs = etype.getAttributeDefs();
        Long atrDefId = null;
        for (IAttributeDef atrdef : etypeAtrDefs) {
            if (atrdef.getName().getString(LocaleUtils.toLocale("en")).equalsIgnoreCase("Name")) {
                atrDefId = atrdef.getGUID();
            }
        }

        List<Attribute> nameAttributes = new ArrayList<Attribute>();

        Attribute nameAttribute = new Attribute();
        nameAttribute.setDefinitionId(atrDefId);
        nameAttribute.setConceptId(attrDef.getConcept().getGUID());

        List<Value> nameValues = new ArrayList<Value>();
        //Vocabularies 

        if (name instanceof Collection) {
            for (Object n : (Collection) name) {
                nameValues.addAll(nameToValue(n));
            }
        } else {
            nameValues.addAll(nameToValue(name));
        }

        nameAttribute.setValues(nameValues);
        nameAttributes.add(nameAttribute);
        nameStructure.setAttributes(nameAttributes);

        List<Value> entityNameValues = new ArrayList<Value>();

        entityNameValues.add(new Value(nameStructure)); // here is your link to the name structure, if you want you can put the id copyOf the name instance (if you created it before) but make sure the data type is COMPLEX_TYPE
        entityNameAttribute.setValues(entityNameValues);
        AttributeODR a = new AttributeODR(api, entityNameAttribute);
        return a;

    }

    public void updateAttributeValue(IEntity entity, IAttribute attribute,
            IValue newValue) {
        AttributeODR attr = (AttributeODR) attribute;
        attr.updateValue(newValue);

    }

    public void updateEntity(IEntity entity) {
        EntityODR ent;

        ent = EntityODR.disify(entity, true);

        Entity e = ent.convertToEntity();
        InstanceClient instanceCl = new InstanceClient(this.api);
        try {
            instanceCl.update(e);
        }
        catch (IllegalArgumentException ex) {
            throw new NotFoundException("Such an entity does not exists.");
        }
    }

    public EntityODR readEntity(String URL) {

        Long typeID;
        try {
            typeID = WebServiceURLs.urlToEntityID(URL);
        }
        catch (Exception e) {
            return null;
        }

        return readEntity(typeID);
    }

    public String createEntityURL(IEntity entity) {
        Long id = createEntity(entity);
        return WebServiceURLs.entityIDToURL(id);
    }

    public void exportToRdf(List<String> entityURLs, Writer writer) {
        if (entityURLs.isEmpty()) {
            throw new IllegalArgumentException("The list of entities for export is empty");
        }

        String filename = "test" + System.currentTimeMillis();
        EntityExportService ees = new EntityExportService();
        List<Long> entitiesID = new ArrayList<Long>();

        for (String entityURL : entityURLs) {

            Long eID = WebServiceURLs.urlToEntityID(entityURL);
            entitiesID.add(eID);
        }

        Long fileId = null;
        try {
            fileId = ees.methodPostRDF(entitiesID, filename);
        }
        catch (ClientProtocolException e) {
            throw new DisiClientException("Error while getting fileId", e);
        }
        catch (IOException e) {
            throw new DisiClientException("Error while getting fileId", e);
        }

        InputStream is = null;
        try {
            is = ees.methodGet(fileId, "sem" + filename);
        }
        catch (ClientProtocolException e) {
            throw new DisiClientException("Error while getting input stream", e);
        }
        catch (IOException e) {
            throw new DisiClientException("Error while getting input stream", e);
        }

        BufferedWriter bw = new BufferedWriter(writer);
        int letter;
        try {
            while ((letter = is.read()) != -1) {
                bw.write((char) letter);
                bw.flush();
            }
        }
        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void exportToJsonLd(List<String> entityURLs, Writer writer) throws DisiClientException {

        if (entityURLs.isEmpty()) {
            throw new IllegalArgumentException("The list of entities to export is empty");
        }

        String filename = "test" + System.currentTimeMillis();
        EntityExportService ees = new EntityExportService();
        List<Long> entitiesID = new ArrayList<Long>();

        for (String entityURL : entityURLs) {
            entitiesID.add(WebServiceURLs.urlToEntityID(entityURL));
        }

        Long fileId = null;
        try {
            fileId = ees.methodPost(entitiesID, filename);
        }
        catch (ClientProtocolException e) {
            throw new DisiClientException("Error while getting fileId", e);
        }
        catch (IOException e) {
            throw new DisiClientException("Error while getting fileId", e);
        }
        InputStream is = null;
        try {
            is = ees.methodGet(fileId, "sem" + filename);
        }
        catch (ClientProtocolException e) {
            throw new DisiClientException("Error while getting input stream", e);
        }
        catch (IOException e) {
            throw new DisiClientException("Error while getting input stream", e);
        }
        try {
            ees.convertToJsonLd(is, writer);
        }
        catch (IOException e) {
            throw new DisiClientException("Error while creating jsonLd", e);
        }

    }

    public void exportToCsv(List<String> entityURLs, Writer writer) {
        //TODO exportToCsv
        throw new UnsupportedOperationException("todo to implement");

    }

    public EntityODR readEntityByGUID(Long guid) {
        InstanceClient instanceCl = new InstanceClient(this.api);

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);
        Entity entity = instanceCl.readEntityByGloabalId(1L, guid, instFilter);
        //Entity entity =  (Entity)instance; 
        EntityODR en = new EntityODR(this.api, entity);
        return en;
    }

    private InstanceClient getInstanceClient() {
        if (instanceClient == null) {
            instanceClient = new InstanceClient(WebServiceURLs.getClientProtocol());
        }
        return instanceClient;
    }

    @Override
    public List<ISearchResult> searchEntities(String partialName, @Nullable String etypeURL, Locale locale) {

        if (locale == null || locale.equals(Locale.ROOT)) {

            logger.warn("TODO - Setting hard coded locale ENGLISH and ITALIAN");
            List<Locale> defaultLocales = Lists.newArrayList(Locale.ENGLISH, Locale.ITALIAN);
            locale = defaultLocales.get(0);
        }

        logger.warn("TODO - SETTING ENTITY PARTIAL NAME TO LOWERCASE");
        String lowerCasedPartialName = partialName.toLowerCase(locale).trim();

        List<ISearchResult> ret = new ArrayList();
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(locale);
        srf.setIncludeAttributesAsProperties(true);
        Long swebEtypeId = null;
        if (etypeURL != null) {
            swebEtypeId = WebServiceURLs.urlToEtypeID(etypeURL);
        }

        List<Instance> instances;

        if (lowerCasedPartialName.isEmpty()) {
            logger.warn("TODO - USING HARD CODED ENTITY BASE '1' IN SEARCH");
            instances = getInstanceClient().readInstances(1L, swebEtypeId, null, null, null);
            List<it.unitn.disi.sweb.webapi.model.eb.Entity> swebEntities = 
                    Converter.swebInstancesToSwebEntities(instances);

            for (it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity : swebEntities) {
                SearchResult res = Converter.makeSearchResult(swebEntity);
                ret.add(res);
            }

        } else {
            return new SearchEntityNameClient().searchEntitiesByName(partialName, etypeURL, locale);
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see eu.trentorise.opendata.semantics.services.IEntityService#isTemporaryURL(java.lang.String)
     */
    public boolean isTemporaryURL(String entityURL) {
        return entityURL.contains(NEW_INSTANCE_PREFIX);
    }

    private class SearchEntityNameClient extends AbstractApiClient<SwebEntitySearchResultWrapper> {

        public SearchEntityNameClient() {
            super(WebServiceURLs.getClientProtocol(),
                    SwebEntitySearchResultWrapper.class,
                    "/search/byName",
                    "SwebEntitySearchResult");
        }

        /**
         * @since 0.11.1
         */
        public List<ISearchResult> searchEntitiesByName(String partialName, @Nullable String etypeURL, @Nullable Locale locale) {
            // BASE_URL/search/byName?query=borgo%20valsu&isPrefix=true&entityBase=1&includeCount=false&idsOnly=false&pageIndex=1&pageSize=10&maxDepth=1&includeSemantics=false&maxValues=10&includeAttributes=false&createAttributeMap=false&attributeFilterType=ATTRIBUTE_DEF_ID&includeAttributesAsProperties=true&includeTimestamps=false&locale=all

            Map<String, String> params = new HashMap();
            params.put("query", partialName);

            logger.warn("TODO - SETTING ENTITY BASE TO 1 IN SEARCH");

            params.put("isPrefix", "true");
            params.put("entityBase", "1");
            params.put("includeAttributesAsProperties", "true");

            if (etypeURL != null) {
                params.put("type", String.valueOf(WebServiceURLs.urlToEtypeID(etypeURL)));
            }

            logger.warn("TODO - SETTING LOCALE TO 'all' IN SEARCH");
            params.put("locale", "all");

            SwebEntitySearchResultWrapper results = this.read("/search/byName", params);

            List<ISearchResult> ret = new ArrayList();

            Dict dict;
            for (SwebEntitySearchResult swebSr : results.results) {

                if (swebSr.names == null) {
                    dict = new Dict();
                } else {
                    dict = new Dict();
                    for (SwebNameResult name : swebSr.names) {
                        dict = dict.merge(Converter.multimapToDict(name.names));
                    }
                }

                ret.add(new SearchResult(WebServiceURLs.entityIDToURL(swebSr.id), dict));
            }

            return ret;

        }

        @Override
        protected String getIdPath(long id) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SwebNameResult {

        public Map<String, List<String>> names;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SwebEntitySearchResultWrapper {

        public List<SwebEntitySearchResult> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SwebEntitySearchResult {

        public long id;
        public long entityBaseId;
        public long typeId;
        public List<SwebNameResult> names;
        public long globalId;
    }

}
