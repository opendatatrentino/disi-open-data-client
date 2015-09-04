package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.UrlMapper;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.commons.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.EbClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.EntityBase;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class TestEntityService {
    
    private static final UrlMapper um = SwebConfiguration.getUrlMapper();

    public static final long OPENING_HOURS = 7L;
    public static final String OPENING_HOURS_URL = um.etypeIdToUrl(OPENING_HOURS);

    public static final long ATTR_DEF_FACILITY_OPENING_HOURS = 66L;
    public static final long ATTR_DEF_FACILITY_OPENING_HOURS_CONCEPT_ID = 111008L;
    public static final String ATTR_DEF_FACILITY_OPENING_HOURS_URL = um.attrDefIdToUrl(ATTR_DEF_FACILITY_OPENING_HOURS, ATTR_DEF_FACILITY_OPENING_HOURS_CONCEPT_ID);
    public static final long ATTR_DEF_HOURS_OPENING_HOUR = 31L;
     public static final long ATTR_DEF_HOURS_OPENING_HOUR_CONCEPT_ID = 111011L;
    public static final String ATTR_DEF_HOURS_OPENING_HOUR_URL = um.attrDefIdToUrl(ATTR_DEF_HOURS_OPENING_HOUR, ATTR_DEF_HOURS_OPENING_HOUR_CONCEPT_ID);
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR = 30L;
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR_CONCEPT_ID = 73048L;
    public static final String ATTR_DEF_HOURS_CLOSING_HOUR_URL = um.attrDefIdToUrl(ATTR_DEF_HOURS_CLOSING_HOUR, ATTR_DEF_HOURS_CLOSING_HOUR_CONCEPT_ID);

    static final long PALAZZETTO_ID = 64000L;
    /**
     * Palazzetto is a Facility. It doesn't have description. Its concept is gymnasium.
     */    
    public static final String PALAZZETTO_URL = um.entityIdToUrl(PALAZZETTO_ID);
    public static final String PALAZZETTO_NAME_IT = "PALAZZETTO DELLO SPORT";
    public static final long GYMNASIUM_CONCEPT_ID = 18565L;
    public static final long GYMNASIUM_CONCEPT_GLOBAL_ID = 18937L;
    
    public static final String GYMNASIUM_CONCEPT_URL = um.conceptIdToUrl(GYMNASIUM_CONCEPT_ID);

    
    static final long RAVAZZONE_ID = 15001L;
    /**
     * Ravazzone is a cool district of Mori.
     */
    public static final String RAVAZZONE_URL = um.entityIdToUrl(RAVAZZONE_ID);
    public static final String RAVAZZONE_NAME_IT = "Ravazzone";
    public static final String RAVAZZONE_NAME_EN = "Ravazzone";
   
    public static final long RESIDENCE_DES_ALPES_ID = 66206L;
    public static final String RESIDENCE_DES_ALPES_URL = um.entityIdToUrl(RESIDENCE_DES_ALPES_ID);

    
    public static final long COMANO_ID = 15007L;
    public static final String COMANO_URL = um.entityIdToUrl(COMANO_ID);
    
    
    public static final long POVO_ID = 1024;
    public static final String POVO_URL = um.entityIdToUrl(POVO_ID);

    
    static final long CAMPANIL_PARTENZA_ID = 64235L;
    /**
     * "Campanil partenza" is a Facility. Entity concept is Detachable
     * chairlift. Has attributes orari. Has descriptions both in Italian and
     * English. Name is only in Italian.
     */
    public static final String CAMPANIL_PARTENZA_URL = um.entityIdToUrl(CAMPANIL_PARTENZA_ID);
    public static final long DETACHABLE_CHAIRLIFT_CONCEPT_ID = 111009L;
    public static final long DETACHABLE_CHAIRLIFT_GLOBAL_CONCEPT_ID = 120783L;
    public static final String DETACHABLE_CHAIRLIFT_CONCEPT_URL = um.conceptIdToUrl(DETACHABLE_CHAIRLIFT_CONCEPT_ID);
    public static final String CAMPANIL_PARTENZA_NAME_IT = "Campanil partenza";

    
    static final long ANDALO_ID = 2089L;
    /**
     * Andalo is one of those nasty locations with "Place Name" as Name type
     */
    public static final String ANDALO_URL = um.entityIdToUrl(ANDALO_ID);

    
    public static final long ROOT_ENTITY_ID = 21L;
    public static final String ROOT_ENTITY_URL = um.etypeIdToUrl(ROOT_ENTITY_ID);

    public static final Long LOCATION_ID = 18L;
    public static final String LOCATION_URL = um.etypeIdToUrl(LOCATION_ID);
    
    
    // Facility
    public static final long FACILITY_ID = 12L;
    public static final String FACILITY_URL = um.etypeIdToUrl(FACILITY_ID);

    public static final long ATTR_DEF_LATITUDE_ID = 69L;
    public static final long ATTR_DEF_LATITUDE_CONCEPT_ID = 45421L;
    public static final String ATTR_DEF_LATITUDE_CONCEPT_URL = um.conceptIdToUrl(ATTR_DEF_LATITUDE_CONCEPT_ID);
    public static final String ATTR_DEF_LATITUDE_URL = um.attrDefIdToUrl(ATTR_DEF_LATITUDE_ID, ATTR_DEF_LATITUDE_CONCEPT_ID);
    public static final long ATTR_DEF_LONGITUDE_ID = 68L;    
    public static final long ATTR_DEF_LONGITUDE_CONCEPT_ID = 45427L;
    public static final String ATTR_DEF_LONGITUDE_CONCEPT_URL = um.conceptIdToUrl(ATTR_DEF_LONGITUDE_CONCEPT_ID);
    public static final String ATTR_DEF_LONGITUDE_URL = um.attrDefIdToUrl(ATTR_DEF_LONGITUDE_ID, ATTR_DEF_LONGITUDE_CONCEPT_ID);
    public static final long ATTR_DEF_CLASS = 58L;
    public static final long ATTR_DEF_CLASS_CONCEPT_ID = 42806L;
    public static final String ATTR_DEF_CLASS_URL = um.attrDefIdToUrl(ATTR_DEF_CLASS, ATTR_DEF_CLASS_CONCEPT_ID);
    public static final long ATTR_DEF_DESCRIPTION = 62L;
    public static final String ATTR_DEF_DESCRIPTION_URL = um.attrDefIdToUrl(ATTR_DEF_DESCRIPTION, ATTR_DEF_CLASS_CONCEPT_ID);
    
    public static final long ATTR_DEF_PART_OF = 60L;
    public static final long ATTR_DEF_PART_OF_CONCEPT_ID = 5L;
    /** Part-of has {@link #ROOT_ENTITY_URL} as range */
    public static final String ATTR_DEF_PART_OF_URL = um.attrDefIdToUrl(ATTR_DEF_PART_OF, ATTR_DEF_PART_OF_CONCEPT_ID);

    public static final long NAME_ID = 10L;
    public static final String NAME_URL = um.etypeIdToUrl(NAME_ID);

    // Shopping facility
    public static final long SHOPPING_FACILITY_ID = 1L;
    public static final String SHOPPING_FACILITY_URL = um.etypeIdToUrl(SHOPPING_FACILITY_ID);    
    
    // Certified product stuff 
    public static final long CERTIFIED_PRODUCT_ID = 17L;
    public static final String CERTIFIED_PRODUCT_URL = um.etypeIdToUrl(CERTIFIED_PRODUCT_ID);

    public static final long ATTR_TYPE_OF_CERTIFICATE = 110L;
    public static final long ATTR_TYPE_OF_CERTIFICATE_CONCEPT_ID = 111103L;
    public static final String ATTR_TYPE_OF_CERTIFICATE_URL = um.attrDefIdToUrl(ATTR_TYPE_OF_CERTIFICATE, ATTR_TYPE_OF_CERTIFICATE_CONCEPT_ID);

    /**
     * It is of type 'Certified product' NOTE: CREATED WITH ODR, WILL DISAPPEAR
     * FROM SERVER ONCE IT IS REGENERATED
     */
    public static final long MELA_VAL_DI_NON = 75167L;
    /**
     * NOTE: CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS REGENERATED
     */
    public static final String MELA_VAL_DI_NON_URL = um.etypeIdToUrl(MELA_VAL_DI_NON);

    private IProtocolClient api;        

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IEntityService enServ;
    
    private IEkb ekb;
    Checker checker;
    
    @Before
    public void before() {
        ekb = ConfigLoader.init();
        api = SwebConfiguration.getClientProtocol();        
        enServ = ekb.getEntityService();
        checker = Checker.of(ekb);
    }
    
    @After
    public void after(){
        api = null;
        enServ = null;
        ekb = null;
        checker = null;
    }

    @Test
    public void testPalazzettoReadNameEtype() {        

        EntityODR entity = (EntityODR) ekb.getEntityService().readEntity(PALAZZETTO_URL);
        logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
        IAttributeDef nameAttrDef = entity.getEtype().getNameAttrDef();
        IStructure nameValue = (IStructure) entity.getAttribute(nameAttrDef.getURL()).getValues().get(0).getValue();

        assertTrue(nameValue.getEtypeURL() != null);

        assertTrue(entity.getName().string(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testPalazzettoRead() {        

        EntityODR entity = (EntityODR) ekb.getEntityService().readEntity(PALAZZETTO_URL);
        logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
        /*               This stuff should be caught by the integrity checker 
         IAttributeDef nameAttrDef = entity.getEtype().getNameAttrDef();
         IStructure nameValue = (IStructure) entity.getAttribute(nameAttrDef.getURL()).getValues().strs(0).getValue();
         assertTrue(nameValue.getEtype() != null);
         */
         checker.checkEntity(entity);

        assertTrue(entity.getName().string(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testReadNonExistingEntity() {        
        assertEquals(ekb.getEntityService().readEntity("http://blabla.com"), null);
    }

    /**
     * todo review - non existing urls shouldn't throw exceptions but the test
     * here even expects the exception to be thrown!
     */
    @Test
    @Ignore
    public void testReadNonExistingEntities() {
        
        List<String> entitieURLs = new ArrayList();
        entitieURLs.add("non-existing-url");
        entitieURLs.add(um.etypeIdToUrl(RAVAZZONE_ID));
        thrown.expect(IllegalArgumentException.class);
        List<IEntity> entities = enServ.readEntities(entitieURLs);
        assertEquals(entities.get(0), null);
        
        assertEquals(entities.get(1).getName().strings(Locale.ITALIAN).get(0), "Ravazzone");
    }

    @Test
    public void testUpdateNonExistingEntity() {
        EntityODR entity = new EntityODR();        
        IEntityService es = ekb.getEntityService();
        entity.setEntityAttributes(new ArrayList<IAttribute>());
        entity.setEtype(ekb.getEntityTypeService().readEntityType(FACILITY_URL));
        entity.setEntityBaseId(1L);
        entity.setURL("http://blabla.org");
        try {
            enServ.updateEntity(entity);
            fail("Should have failed while updating non existing entity!");
        }
        catch (NotFoundException ex) {

        }
    }

    @Test
    public void testEntityReadByGlobalID() {
        
        EntityODR entity = (EntityODR) ((EntityService) enServ).readEntityByGlobalId(10000466L);
        checker.checkEntity(entity);
        logger.info(entity.getEtype().getName().strings(Locale.ITALIAN).get(0));
        assertEquals(entity.getEtype().getName().strings(Locale.ITALIAN).get(0), "Infrastruttura");
    }
    
    

    @Test
    public void testCreateDeleteEntity() {

        //initialising variables
        
        InstanceClient instanceClient = new InstanceClient(api);
        AttributeClient attrClient = new AttributeClient(api);
        ComplexTypeClient ctypecl = new ComplexTypeClient(api);

        Instance inst = instanceClient.readInstance(COMANO_ID, null);

        EntityODR entityToCreate = new EntityODR();
        List<Attribute> attributes = new ArrayList();
        ComplexType cType = ctypecl.readComplexType(inst.getTypeId(), null);
        EntityType etype = new EntityType(cType);		

        //instantiation of variables
        attributes = attrClient.readAttributes(COMANO_ID, null, null);

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        Long attrDefClassAtrID = null;
        for (IAttributeDef adef : attrDefs) {

            if (adef.getName().string(Locale.ENGLISH).equalsIgnoreCase("class")) {
                attrDefClassAtrID = attrDefUrlToId(adef.getURL());
                break;
            }
        }


        ArrayList<Attribute> attrsEntityToCreate = new ArrayList();

        for (Attribute a : attributes) {

            if (a.getDefinitionId() != attrDefClassAtrID) {                
                
                a.setInstanceId(null);
            	a.setId(null);
            	a.getValues().get(0).setId(null);
            	a.getValues().get(0).setAttributeId(null);
            	System.out.println(a.getValues());
            //	a.setValues(null);
                 attrsEntityToCreate.add(a);		                 
            } break;
        }
        
        //assigning variables
        entityToCreate.setAttributes(attrsEntityToCreate);
        entityToCreate.setEtype(etype);
        entityToCreate.setEntityBaseId(1L);


        EbClient ebc = new EbClient(api);
        EntityBase eb = ebc.readEntityBase(1L, null);
        int instanceNum = eb.getInstancesNumber();
        String entityURL = null;

        try {
            entityURL = enServ.createEntityURL(entityToCreate);

            EntityBase ebafter = ebc.readEntityBase(1L, null);
            int instanceNumAfter = ebafter.getInstancesNumber();
            assertEquals(instanceNum + 1, instanceNumAfter);
        }
        finally {
            if (entityURL != null) {
                enServ.deleteEntity(entityURL);
            }
        }
        EntityBase ebafterDel = ebc.readEntityBase(1L, null);
        int instanceNumAfterDel = ebafterDel.getInstancesNumber();
        assertEquals(instanceNumAfterDel, instanceNumAfterDel);

    }

    /**
     * Null values are not admitted by OpenEntity API and should always be
     * rejected wth an exception TODO IMPLEMNT IT
     */
    @Test
    @Ignore
    public void testNullValues() {
        throw new UnsupportedOperationException("TODO IMPLEMENT ME");
    }

    @Test
    public void testReadEntityRavazzone() {
        IEntity entity = enServ.readEntity(RAVAZZONE_URL);
        checker.checkEntity(entity);
        IEntityType etype = ekb.getEntityTypeService().readEntityType(entity.getEtypeURL());
        logger.info(etype.getName().strings(Locale.ITALIAN).get(0));
        assertEquals(etype.getName().strings(Locale.ITALIAN).get(0), "Località");
    }

    @Test
    public void testReadCampanilPartenza() {
        
        EntityODR entity = (EntityODR) enServ.readEntity(CAMPANIL_PARTENZA_URL);
        checker.checkEntity(entity);
        logger.info(entity.getEtype().getName().strings(Locale.ITALIAN).get(0));

        assertTrue(entity.getName().strings(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().strings(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().strings(Locale.ENGLISH).get(0).length() > 0);
        assertNotNull(((SemText) entity.getAttribute(ATTR_DEF_DESCRIPTION_URL).getValues().get(0).getValue()).getLocale());
    }

    @Test
    public void testReadStructure() {        
        StructureODR structure = (StructureODR) ((EntityService) enServ).readStructure(64001L);
        checker.checkStructure(structure);
        logger.info(structure.getEtype().getName().strings(Locale.ITALIAN).get(0));
        assertEquals(structure.getEtype().getName().strings(Locale.ITALIAN).get(0), "Nome");
    }

    @Test
    public void testReadEntities() {
        
        List<String> entitieURLs = new ArrayList();
        entitieURLs.add(PALAZZETTO_URL);

        entitieURLs.add(RAVAZZONE_URL);
        // entitieURLs.add(POVO_URL);
        List<IEntity> entities = enServ.readEntities(entitieURLs);
        for (IEntity entity : entities) {
            checker.checkEntity(entity);
        }

        logger.info(entities.get(0).getName().strings(Locale.ITALIAN).get(0));
        assertEquals(entities.get(1).getName().strings(Locale.ITALIAN).get(0), "PALAZZETTO DELLO SPORT");
        String name = readEtype(entities.get(1)).getName().strings(Locale.ITALIAN).get(0);
        logger.info(name);
        assertEquals(name, "Ravazzone");
    }

    @Test
    public void testReadZeroEntities() {        
        assertEquals(enServ.readEntities(new ArrayList<String>()).size(), 0);
    }

    @Test
    public void testUpdateEntity() {
        
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                //                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                //                AttributeODR attr = enServ.createAttribute(atDef, 11.466f);
                //                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                //                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                //                AttributeODR attr = enServ.createAttribute(atDef, 46.289f);
                //                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);

            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                ConceptODR concept = (ConceptODR) ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();

                attrs1.add(a);
            }
        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        EntityODR ent = new EntityODR(en);
        String id = enServ.createEntityURL(ent);

        EntityODR newEntityODR = (EntityODR) enServ.readEntity(id);
        
        List<Attribute> newAttrs = newEntityODR.getAttributes();
        //--------Entity Update Test start

        assertEquals(4, newEntityODR.getStructureAttributes().size());

        AttributeDef openHourAD = new AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR);
        AttributeDef closeHourAD = new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);

        HashMap<AttributeDef, Object> attrMap = new HashMap();
        attrMap.put(openHourAD, "8:00");
        attrMap.put(closeHourAD, "8.00" + System.currentTimeMillis());
        IAttributeDef attrDef = new AttributeDef(66L);

        IAttribute attr = enServ.createAttribute(attrDef, attrMap);

        List<IAttribute> attributes = newEntityODR.getStructureAttributes();
        attributes.add(attr);
        newEntityODR.setStructureAttributes(attributes);

        enServ.updateEntity(newEntityODR);

        IEntity updatedEntity = enServ.readEntity(id);
        assertEquals(5, updatedEntity.getStructureAttributes().size());
        //--------Entity Update Test end---------
        //--------Value Update Test start--------
        Float testNewValue = 0.0f;
        ValueODR val = new ValueODR(null,null, testNewValue);
        
        val.setValue(testNewValue);

        for (Attribute atr : newAttrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                AttributeODR attrODR = new AttributeODR(atr);
                //				ValueODR val = (ValueODR) attrODR.getValues().strs(0);
                //				val.setValue(value);
                enServ.updateAttributeValue(newEntityODR, attrODR, val);
            }
        }

        EntityODR entityUpdValue = (EntityODR) enServ.readEntity(id);

        List<Attribute> updAttributes = entityUpdValue.getAttributes();

        for (Attribute atr : updAttributes) {
            if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                assertEquals(testNewValue, atr.getValues().get(0).getValue());
            }
        }

        enServ.deleteEntity(id);

    }

    @Test
    public void testCreateAttributeEntity() {
        
        
        IEntityType etype = ekb.getEntityTypeService().readEntityType(FACILITY_URL);
        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList();

        for (IAttributeDef atd : attrDefList) {
            //			if (atd.getName().string(Locale.ENGLISH).equals("Name")){
            //				logger.info(atd.getName());
            //				logger.info(atd.getGUID());
            //				logger.info(atd.getDataType());
            //				if (atd.getDataType().equals(DataTypes.STRUCTURE)){
            //					logger.info(atd.getRangeEType().getURL());
            //					EntityType etpe =	ets.getEntityType(atd.getRangeEType().getURL());
            //					List<IAttributeDef>atsd = etpe.getAttributeDefs();
            //					for (IAttributeDef a:atsd){
            //						logger.info(a.getGUID());
            //					}
            //
            //				}

            if (atd.getName().string(Locale.ENGLISH).equals("Name")) {
                //  logger.info(atd.getName());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atd, "TestName");
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().string(Locale.ENGLISH).equals("Class")) {
                //  logger.info(atd.getName());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atd, 123L);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().string(Locale.ENGLISH).equals("Latitude")) {
                //       logger.info(atd.getName());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atd, 12.123F);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().string(Locale.ENGLISH).equals("Longitude")) {
                //     logger.info(atd.getName());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atd, 56.567F);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().string(Locale.ENGLISH).equals("Opening hours")) {
                //     logger.info(atd.getName());
                //      logger.info(atd.getURL());

                AttributeDef openHourAD = new AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR);
                AttributeDef closeHourAD = new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);

                HashMap<AttributeDef, Object> attrMap = new HashMap();
                attrMap.put(openHourAD, "8:00");
                attrMap.put(closeHourAD, "18:00");

                AttributeODR attr = (AttributeODR) enServ.createAttribute(atd, attrMap);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            }

        }
        EntityODR e = new EntityODR();
        e.setEntityBaseId(1L);
        e.setTypeId(18L);
        e.setAttributes(attrs);
        long id = enServ.createEntity(e);
        logger.info("Entity id:" + id);
        assertTrue(id > 0);
        enServ.deleteEntity(id);
    }
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createNameStructure() {

        
        EntityODR en = (EntityODR) enServ.readEntity(RAVAZZONE_URL);
        //    	
        //    	AttributeDef atrDef = new AttributeDef(169);
        //    	AttributeODR nameAtr = enServ.createNameAttributeODR(atrDef, name);

        for (IAttribute a : ((IStructure) en).getStructureAttributes()) {

            //    		System.out.println(a.getAttrDef().getName().string(Locale.ENGLISH));
            //    		//System.out.println(a.getAttrDef().getRangeEtypeURL());
            //    		//System.out.println(a.getAttrDef().getEType());
            //    		System.out.println(a.getAttrDef().getGUID());
            //    		System.out.println(a.getAttrDef().getConcept().getGUID());
        }
    }

    @Test
    public void testReadEntity_2() {        
        enServ.readEntity(RAVAZZONE_URL);
    }

    IEntityType readEtype(IEntity en){
        return ekb.getEntityTypeService().readEntityType(en.getEtypeURL());
    }
    
    /**
     * Andalo is nasty as it has a name type "Place Name" with ID 23, instead of
     * the usual one with ID 10
     */
    @Test
    public void testReadAndalo() {
        
        IEntity en = enServ.readEntity(ANDALO_URL);

        checker.checkEntity(en);

        IAttributeDef nameAttrDef = readEtype(en).getNameAttrDef();
        String nameAttrDefURL = nameAttrDef.getURL();
        logger.info("nameAttrDefURL = " + nameAttrDefURL);

        IAttribute nameAttr = en.getAttribute(nameAttrDefURL);

        assertEquals(nameAttrDefURL, nameAttr.getAttrDefUrl());

        IStructure nameStruct = (IStructure) nameAttr.getValues().get(0).getValue();

        assertEquals(nameAttrDef.getRangeEtypeURL(), nameStruct.getEtypeURL());

    }

    @Test
    public void testDisify() {
        
        EntityODR en = (EntityODR) enServ.readEntity(POVO_URL);        
        
        AttributeODR a = (AttributeODR) en.getStructureAttributes().get(2);
        IValue val = new ValueODR(null, null, 15.2f); 
        
        a.addValue(val);

        IEntity ent = EntityODR.disify(en, true);
        assertNotNull(ent);        
        String URL = null;
        try {
            URL = enServ.createEntityURL(ent);
        }
        finally {
            if (URL != null) {
                enServ.deleteEntity(URL);
            }
        }
    }


    @Test
    public void testEntitySearch() {        
        String etypeURL = um.etypeIdToUrl(18L);
        Locale locale = OdtUtils.languageTagToLocale("it");
        List<SearchResult> sResults = enServ.searchEntities("Povo", etypeURL, locale);
        for (SearchResult sr : sResults) {
            assertNotNull(sr.getId());
            assertNotNull(sr.getName());
        }
    }
    
    @Test
    public void testEntitySearchAndalo() {        
        String etypeURL = um.etypeIdToUrl(18L);
        Locale locale = OdtUtils.languageTagToLocale("it");
        List<SearchResult> sResults = enServ.searchEntities("Andalo", etypeURL, locale);
        assertTrue(sResults.size() > 0);
        
        assertEquals("Andalo", sResults.get(0).getName().string(Locale.ITALIAN));       
    }
    
    @Test
    public void TestResidenceDesAlpes(){        
        IEntity en = enServ.readEntity(RESIDENCE_DES_ALPES_URL);
        checker.checkEntity(en);
    }
    
  @Test
    public void testSearchIncompleteEntity() {        
        List<SearchResult> res = enServ.searchEntities("roveret", null, Locale.ITALIAN);
        assertTrue(res.size() > 0);
    }
        
    @Test
    public void testSearchMultiWordEntity() {        
        List<SearchResult> res = enServ.searchEntities("borgo valsugana", null, Locale.ITALIAN);
        assertTrue(res.size() > 0);
    }
    
    @Test
    public void testSearchIncompleteMultiWordEntity() {        
        
        List<SearchResult> res = enServ.searchEntities("borgo valsu", null, Locale.ITALIAN);
        assertTrue(res.size() > 0);
               
    }        

    private long attrDefUrlToId(String url) {
        return SwebConfiguration.getUrlMapper().attrDefUrlToId(url);
    }
    
}
