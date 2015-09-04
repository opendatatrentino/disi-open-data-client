package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.commons.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.services.SearchResult;

import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.UrlMapper;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.VocabularyClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
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
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityService implements IEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(EntityService.class);

    private DisiEkb ekb;

    @Nullable
    private InstanceClient instanceClient;

    EntityService() {
    }

    private InstanceClient getInstanceClient() {
        if (instanceClient == null) {
            instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
        }
        return instanceClient;
    }

    EntityService(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
    }

    @Override
    public Long createEntity(IEntity entity) {

        EntityODR ent = EntityODR.disify(entity, true);

        Entity e = ent.convertToEntity();

        LOG.info(e.toString());
        EntityTypeService es = new EntityTypeService();
        EntityType etype = es.readEntityType(e.getTypeId());

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        Long attrDefClassAtrID = null;
        for (IAttributeDef adef : attrDefs) {

            if (adef.getName().string(Locale.ENGLISH).equalsIgnoreCase("class")) {
                attrDefClassAtrID = attrDefUrlToId(adef.getURL());
                break;
            }
        }

        boolean isExistAttrClass = false;

        for (Attribute a : e.getAttributes()) {

            if (a.getDefinitionId().equals(attrDefClassAtrID)) {
                isExistAttrClass = true;
                break;
            }
        }

        if (!isExistAttrClass) {
            Attribute a = createClassAttribute(attrDefClassAtrID, etype.getConceptID());
            e.getAttributes().add(a);
            LOG.warn("Default class attribute is assigned");
        }

        //System.out.println("Class exists: truw");
        Long id = null;
        try {
            id = getInstanceClient().create(e);
        }
        catch (NotFoundException ex) {
        }
        return id;

    }

    public Attribute createClassAttribute(Long attrDefClassAtrID, Long conceptID) {
        IAttributeDef atrDef = new AttributeDef(attrDefClassAtrID);        
        IConcept concept = ekb.getKnowledgeService().readConcept(SwebConfiguration.getUrlMapper().conceptIdToUrl(conceptID));
        AttributeODR atrODR = createAttribute(atrDef, concept);
        Attribute a = atrODR.convertToAttribute();
        return a;

    }

    public Long createEntity(Name name) {        
        Long id = getInstanceClient().create(name);
        return id;
    }

    public void updateEntity(Name name) {

        //EntityODR ent = (EntityODR) name;
        //Entity en=(Entity)ent;
        //	Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
        //
        //		instance.setTypeId(ent.getEtype().getGUID());
        //		instance.setId(entity.getLocalID());
        //		List<IAttribute> attrs = entity.getStructureAttributes();
        //		List<Attribute> attributes = ent.convertToAttributes(attrs);
        //		instance.setAttributes(attributes);
        //Entity e = ent.convertToEntity();
        getInstanceClient().update(name);
    }

    @Override
    public void deleteEntity(long entityID) {
        Instance instance = getInstanceClient().readInstance(entityID, null);
        getInstanceClient().delete(instance);
    }

    @Override
    public void deleteEntity(String entityUrl) {
        Instance instance = getInstanceClient().readInstance(SwebConfiguration.getUrlMapper().entityUrlToId(entityUrl), null);
        getInstanceClient().delete(instance);
    }

    /**
     * Returns null if entity is not found on the server
     *
     * @param entityID
     * @return
     */
    @Nullable
    public EntityODR readEntity(long entityID) {
        InstanceClient instanceCl = new InstanceClient(SwebConfiguration.getClientProtocol());

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);
        Instance instance = instanceCl.readInstance(entityID, instFilter);
        if (instance == null) {
            return null;
        } else {
            Entity entity = (Entity) instance;
            EntityODR en = new EntityODR(entity);
            //   Checker.checkEntity(en);
            return en;
        }

    }


    @Override
    public List<IEntity> readEntities(List<String> entityUrls) {

        if (entityUrls.isEmpty()) {
            return new ArrayList();
        }

        List<Long> entityIDs = new ArrayList();

        for (String entityURL : entityUrls) {
            entityIDs.add(SwebConfiguration.getUrlMapper().entityUrlToId(entityURL));
        }

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        List instances = getInstanceClient().readInstancesById(entityIDs, instFilter);
        List<Entity> entities = (List<Entity>) instances;
        List<IEntity> ret = new ArrayList();
        for (Entity epEnt : entities) {
            EntityODR en = new EntityODR(epEnt);
            Checker.of(ekb).checkEntity(en);
            ret.add(en);
        }
        return ret;
    }

    public StructureODR readName(long entityID) {

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        Instance instance = getInstanceClient().readInstance(entityID, instFilter);

        Name name = (Name) instance;
        StructureODR structureName = new StructureODR();
        List<Attribute> atrs = name.getAttributes();

        for (Attribute a : atrs) {

            if (a.getDataType() == DataType.CONCEPT) {
                List<Value> vals = a.getValues();
                List<Value> fixedVals = new ArrayList();

                for (Value val : vals) {

                    if (val.getValue().getClass().equals(ConceptODR.class)) {
                        fixedVals.add(val);
                        continue;
                    }
                    Concept c = (Concept) val.getValue();
                    ConceptODR codr = new ConceptODR(c);

                    ValueODR fixedVal = new ValueODR(val.getId(), null, codr);
                    
                    // fixedVal.setDataType(IConcept.class);
                    
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

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);

        Instance instance = getInstanceClient().readInstance(entityID, instFilter);

        it.unitn.disi.sweb.webapi.model.eb.Structure structure = (it.unitn.disi.sweb.webapi.model.eb.Structure) instance;
        StructureODR structureName = new StructureODR();
        structureName.setAttributes(structure.getAttributes());
        structureName.setTypeId(structure.getTypeId());
        structureName.setEntityBaseId(1L);
        structureName.setId(structure.getId()); 

        return structureName;
    }
    
    public void addAttribute(IEntity entity, IAttribute attribute) {
        EntityODR ent = (EntityODR) entity;
        ent.addAttribute(attribute);
    }


    /**
     * @param value Note: can be a Collection
     */
    @Override
    public AttributeODR createAttribute(IAttributeDef attrDef, Object value) {
        AttributeDef ad = (AttributeDef) attrDef;

        if (ad.getName(Locale.ENGLISH).equals("Name")) {
            return createNameAttributeODR(attrDef, value);

        } else if (ad.getName(Locale.ENGLISH).equals("Description")) {
            return createDescriptionAttributeODR(attrDef, value);

        } else if (attrDef.getDatatype().equals(DataTypes.STRUCTURE)) {
            if (value instanceof Collection) { // notice in Java a Map is *NOT* an instance of Collection
                return createStructureAttribute(attrDef, (Collection) value);
            } else {
                List<HashMap<IAttributeDef, Object>> hashMaps = new ArrayList();
                hashMaps.add((HashMap<IAttributeDef, Object>) value);
                return createStructureAttribute(attrDef, hashMaps);
            }

        } else {
            
            long attrDefId = attrDefUrlToId(attrDef.getURL());
            if (value instanceof Collection) {
                List<ValueODR> valsODR = new ArrayList();
                for (Object obj : (Collection) value) {
                    ValueODR valODR;
                    if (obj instanceof IEntity) {
                         valODR = new ValueODR(null, null, EntityODR.disify((IEntity) obj, false));
                        
                    } else {
                        valODR = new ValueODR(null, null, obj);                        
                    }

                    valsODR.add(valODR);
                }
                return new AttributeODR(attrDefId, valsODR);
            } else {
                ValueODR valODR;
                if (value instanceof IEntity) {
                     valODR = new ValueODR(null, null, EntityODR.disify((IEntity) value, false));
                    
                } else {
                    valODR = new ValueODR(null, null, value);                    
                }
                return new AttributeODR(attrDefId, valODR);
            }

        }
    }

    /**
     * @param descr either a String or a SemText instance
     * @return the description as SemText
     * @throws IllegalArgumentException if descr is not of the proper type
     */
    private SemText descrToSemText(Object descr) {
        if (descr instanceof String) {
            /* david there should be only SemText 
             descr= new SemanticString();
             String s = (String) value;
             descr.setText(s); */
            return SemText.of((String) descr);
        } else if (descr instanceof SemText) {
            /* david  there should be only SemText 
             SemText st= (SemText) value;
             descr = SemTextFactory.semanticString(st);
             */
            return (SemText) descr;
        } else {
            throw new IllegalArgumentException("Wrong value for the attribute is given! Accepted values are String and SemText.");
        }
    }

    /**
     *
     * @param descr either a String, a SemText, or a Collection of String or
     * SemText
     * @throws IllegalArgumentException if descr is not of the proper type
     */
    private AttributeODR createDescriptionAttributeODR(IAttributeDef attrDef,
            Object descr) {
        
        long attrDefId = attrDefUrlToId(attrDef.getURL());
        if (descr instanceof Collection) {
            List<ValueODR> valsODR = new ArrayList();
            for (Object obj : (Collection) descr) {
                ValueODR valODR = new ValueODR(null, null, descrToSemText(obj));                
                valsODR.add(valODR);
                LOG.warn("Vocabulary id is set to default '1'.");
                valODR.setVocabularyId(1L);
            }
            return new AttributeODR(attrDefId, valsODR);
        } else {
            ValueODR val = new ValueODR(null, null, descrToSemText(descr));            
            LOG.warn("Vocabulary id is set to default '1'.");
            val.setVocabularyId(1L);
            return new AttributeODR(attrDefId, val);
        }
    }

    private StructureODR createStructure(IAttributeDef attrDef,
            HashMap<IAttributeDef, Object> atributes) {
        List<Attribute> attrs = new ArrayList();
        StructureODR attributeStructure = new StructureODR();
        LOG.warn("Hardcoded entity base id 1");
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
        nAtr.setDefinitionId(attrDefUrlToId(attrDef.getURL()));
        List<Value> values = new ArrayList();

        for (HashMap<IAttributeDef, Object> structMap : structs) {
            values.add(new Value(createStructure(attrDef, structMap)));
        }

        nAtr.setValues(values);

        AttributeODR a = new AttributeODR(nAtr);

        return a;
    }

    public HashMap<String, Long> getVocabularies() {
        HashMap<String, Long> mapVocabs = new HashMap();
        VocabularyClient vc = new VocabularyClient(SwebConfiguration.getClientProtocol());
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
        nAtr.setDefinitionId(SwebConfiguration.getUrlMapper().attrDefUrlToId(attrDef.getURL()));
        List<Value> values = new ArrayList();
        values.add(new Value(name));
        nAtr.setValues(values);
        AttributeODR a = new AttributeODR(nAtr);
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
            LOG.warn("No Locale is provided for name" + name + "The vocabulary is set to '1'");
            nameValues.add(new Value(nameInput, 1L));
            return nameValues;
        } else if (name instanceof Dict) {
            Dict nameDict = (Dict) name;
            HashMap<String, Long> vocabs = getVocabularies();
            Set<Locale> locs = nameDict.locales();
            for (Locale l : locs) {
                nameValues.add(new Value(nameDict.string(l), vocabs.get(OdtUtils.localeToLanguageTag(l))));//dav so Java 6 doesn't bother us l.toLanguageTag())));
            }
            return nameValues;
        } else {
            throw new IllegalArgumentException("Wrong Name object is given. "
                    + "Name object should be an instance of String or IDict classes. Found instead instance of class " + name.getClass().getSimpleName());
        }

    }
    
    
    private static long attrDefUrlToId(String attrDefUrl){
        return SwebConfiguration.getUrlMapper().attrDefUrlToId(attrDefUrl);
    }
    
    /**
     *
     * @param attrDef
     * @param name can be a String, an IDict or a Collection of String or IDict.
     */
    public AttributeODR createNameAttributeODR(IAttributeDef attrDef, Object name) {

        UrlMapper um = SwebConfiguration.getUrlMapper();

        Attribute entityNameAttribute = new Attribute();
        entityNameAttribute.setDefinitionId(attrDefUrlToId(attrDef.getURL()));

        Name nameStructure = new Name();
        nameStructure.setEntityBaseId(1L);
        LOG.warn("TODO HARDCODED ENTITY BASE ID TO 1.");
        long etypeID;
//		if(attrDef.getRangeEtypeURL()==null){
//			etypeID=10L;
//		}else 
        etypeID = um.etypeUrlToId(attrDef.getRangeEtypeURL());
        nameStructure.setTypeId(etypeID);

        EntityTypeService ets = new EntityTypeService();
        EntityType etype = (EntityType) ets.readEntityType(um.etypeIdToUrl(etypeID));
        List<IAttributeDef> etypeAtrDefs = etype.getAttributeDefs();
        Long atrDefId = null;
        for (IAttributeDef atrdef : etypeAtrDefs) {
            if (atrdef.getName().string(LocaleUtils.toLocale("en")).equalsIgnoreCase("Name")) {
                atrDefId = attrDefUrlToId(atrdef.getURL());
            }
        }

        List<Attribute> nameAttributes = new ArrayList();

        Attribute nameAttribute = new Attribute();
        nameAttribute.setDefinitionId(atrDefId);
        // 0.12 nameAttribute.setConceptId(attrDef.getConcept().getGUID());

        List<Value> nameValues = new ArrayList();
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

        List<Value> entityNameValues = new ArrayList();

        entityNameValues.add(new Value(nameStructure)); // here is your link to the name structure, if you want you can put the id copyOf the name instance (if you created it before) but make sure the data type is COMPLEX_TYPE
        entityNameAttribute.setValues(entityNameValues);
        AttributeODR a = new AttributeODR(entityNameAttribute);
        return a;

    }

    @Override
    public void updateAttributeValue(IEntity entity, IAttribute attribute,
            IValue newValue) {
        AttributeODR attr = (AttributeODR) attribute;
        attr.updateValue(newValue);

    }

    @Override
    public void updateEntity(IEntity entity) {
        EntityODR ent;

        ent = EntityODR.disify(entity, true);

        Entity e = ent.convertToEntity();

        try {
            getInstanceClient().update(e);
        }
        catch (IllegalArgumentException ex) {
            throw new NotFoundException("Such an entity does not exists.");
        }
    }

    @Override
    public EntityODR readEntity(String URL) {

        Long typeID;
        try {
            typeID = SwebConfiguration.getUrlMapper().entityUrlToId(URL);
        }
        catch (Exception e) {
            return null;
        }

        return readEntity(typeID);
    }

    @Override
    public String createEntityURL(IEntity entity) {
        Long id = createEntity(entity);
        return SwebConfiguration.getUrlMapper().entityIdToUrl(id);
    }

    @Override
    public void exportToRdf(List<String> entityURLs, Writer writer) {

        UrlMapper um = SwebConfiguration.getUrlMapper();

        if (entityURLs.isEmpty()) {
            throw new IllegalArgumentException("The list of entities for export is empty");
        }

        String filename = "test" + System.currentTimeMillis();
        EntityExportService ees = new EntityExportService();
        List<Long> entitiesID = new ArrayList();

        for (String entityURL : entityURLs) {

            Long eID = um.entityUrlToId(entityURL);
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
        catch (IOException ex) {

            throw new DisiClientException("Error while writing file!", ex);
        }

    }

    @Override
    public void exportToJsonLd(List<String> entityURLs, Writer writer) throws DisiClientException {

        UrlMapper um = SwebConfiguration.getUrlMapper();

        if (entityURLs.isEmpty()) {
            throw new IllegalArgumentException("The list of entities to export is empty");
        }

        String filename = "test" + System.currentTimeMillis();
        EntityExportService ees = new EntityExportService();
        List<Long> entitiesID = new ArrayList();

        for (String entityURL : entityURLs) {
            entitiesID.add(um.entityUrlToId(entityURL));
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

    @Override
    public void exportToCsv(List<String> entityURLs, Writer writer) {
        //TODO exportToCsv
        throw new UnsupportedOperationException("todo to implement");

    }

    public EntityODR readEntityByGlobalId(Long globalId) {

        InstanceFilter instFilter = new InstanceFilter();
        instFilter.setIncludeAttributes(true);
        instFilter.setIncludeAttributesAsProperties(true);
        instFilter.setIncludeSemantics(true);
        LOG.warn("TODO - USING FIXED ENTITYBASE WITH ID 1");
        Entity entity = getInstanceClient().readEntityByGloabalId(1L, globalId, instFilter);         
        EntityODR en = new EntityODR(entity);
        return en;
    }

    @Override
    public List<SearchResult> searchEntities(String partialName, @Nullable String etypeURL, Locale locale) {

        LOG.warn("TODO - SETTING ENTITY PARTIAL NAME TO LOWERCASE");
        String lowerCasepartialName = partialName.toLowerCase(locale);

        List<SearchResult> entities;

        Search search = new Search();
        entities = search.searchEntities(lowerCasepartialName, etypeURL, locale);

        return entities;
    }

    /* (non-Javadoc)
     * @see eu.trentorise.opendata.semantics.services.IEntityService#isTemporaryURL(java.lang.String)
     */
    @Override
    public boolean isTemporaryURL(String entityURL) {
        return entityURL.contains("instances/new/");
    }
}
