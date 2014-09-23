package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.attrDefIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.conceptIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.entityIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.etypeIDToURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.Structure;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class TestEntityService {

    public static final long OPENING_HOURS = 7L;
    public static final String OPENING_HOURS_URL = etypeIDToURL(OPENING_HOURS);

    public static final long ATTR_DEF_FACILITY_OPENING_HOURS = 66L;
    public static final String ATTR_DEF_FACILITY_OPENING_HOURS_URL = attrDefIDToURL(ATTR_DEF_FACILITY_OPENING_HOURS);
    public static final long ATTR_DEF_HOURS_OPENING_HOUR = 31L;
    public static final String ATTR_DEF_HOURS_OPENING_HOUR_URL = attrDefIDToURL(ATTR_DEF_HOURS_OPENING_HOUR);
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR = 30L;
    public static final String ATTR_DEF_HOURS_CLOSING_HOUR_URL = attrDefIDToURL(ATTR_DEF_HOURS_CLOSING_HOUR);

    /**
     * Palazzetto doesn't have description. Its concept is gymnasium.
     */
    public static final long PALAZZETTO_ID = 64000L;
    public static final String PALAZZETTO_URL = entityIDToURL(PALAZZETTO_ID);
    public static final String PALAZZETTO_NAME_IT = "PALAZZETTO DELLO SPORT";
    public static final long GYMNASIUM_CONCEPT_ID = 18565L;
    public static final String GYMNASIUM_CONCEPT_URL = conceptIDToURL(GYMNASIUM_CONCEPT_ID);

    /**
     * Ravazzone is a cool district of Mori.
     */
    public static final long RAVAZZONE_ID = 15001L;
    public static final String RAVAZZONE_URL = entityIDToURL(RAVAZZONE_ID);
    public static final String RAVAZZONE_NAME_IT = "Ravazzone";
    public static final String RAVAZZONE_NAME_EN = "Ravazzone";
    public static final long ADMINISTRATIVE_DISTRICT_CONCEPT_ID = 10001L;
    public static final String ADMIN_DISTRICT_CONCEPT_URL = conceptIDToURL(ADMINISTRATIVE_DISTRICT_CONCEPT_ID);

    /**
     * "Campanil partenza" is a Facility. Entity concept is Detachable
     * chairlift. Has attributes orari. Has descriptions both in Italian and
     * English. Name is only in Italian.
     */
    public static final long CAMPANIL_PARTENZA_ID = 1L;
    public static final String CAMPANIL_PARTENZA_URL = entityIDToURL(CAMPANIL_PARTENZA_ID);
    public static final long DETACHABLE_CHAIRLIFT_CONCEPT_ID = 111009L;
    public static final String DETACHABLE_CHAIRLIFT_CONCEPT_URL = conceptIDToURL(DETACHABLE_CHAIRLIFT_CONCEPT_ID);
    public static final String CAMPANIL_PARTENZA_NAME_IT = "Campanil partenza";

    /**
     * Andalo is one of those nasty locations with "Place Name" as Name type
     */
    public static final long ANDALO_ID = 2089L;
    public static final String ANDALO_URL = entityIDToURL(ANDALO_ID);

    public static final long CLASS_CONCEPT_ID = 21987L;
    public static final String CLASS_CONCEPT_ID_URL = conceptIDToURL(CLASS_CONCEPT_ID);

    public static final long ROOT_ENTITY_ID = 21L;
    public static final String ROOT_ENTITY_URL = etypeIDToURL(ROOT_ENTITY_ID);

    public static final long FACILITY_ID = 12L;
    public static final String FACILITY_URL = etypeIDToURL(FACILITY_ID);

    public static final long ATTR_DEF_LATITUDE_ID = 69L;
    public static final String ATTR_DEF_LATITUDE_URL = attrDefIDToURL(ATTR_DEF_LATITUDE_ID);
    public static final long ATTR_DEF_LONGITUDE_ID = 68L;
    public static final String ATTR_DEF_LONGITUDE_URL = attrDefIDToURL(ATTR_DEF_LONGITUDE_ID);
    public static final long ATTR_DEF_CLASS = 58L;
    public static final String ATTR_DEF_CLASS_URL = attrDefIDToURL(ATTR_DEF_CLASS);
    public static final long ATTR_DEF_DESCRIPTION = 62L;
    public static final String ATTR_DEF_DESCRIPTION_URL = attrDefIDToURL(ATTR_DEF_DESCRIPTION);
    public static final long ATTR_DEF_PART_OF = 60L;
    public static final String ATTR_DEF_PART_OF_URL = attrDefIDToURL(ATTR_DEF_PART_OF);

    public static final long NAME_ID = 10L;
    public static final String NAME_URL = etypeIDToURL(NAME_ID);

    private IProtocolClient api;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void getClientProtocol() {
        this.api = WebServiceURLs.getClientProtocol();

    }

    @Test
    public void testPalazzettoReadNameEtype() {
        IEkb disiEkb = new DisiEkb();

        EntityODR entity = (EntityODR) disiEkb.getEntityService().readEntity(PALAZZETTO_URL);
        logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
        IAttributeDef nameAttrDef = entity.getEtype().getNameAttrDef();
        IStructure nameValue = (IStructure) entity.getAttribute(nameAttrDef.getURL()).getValues().get(0).getValue();

        assertTrue(nameValue.getEtypeURL() != null);

        assertTrue(entity.getName().getString(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testPalazzettoRead() {
        IEkb disiEkb = new DisiEkb();

        EntityODR entity = (EntityODR) disiEkb.getEntityService().readEntity(PALAZZETTO_URL);
        logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
        /*               This stuff should be caught by the integrity checker 
         IAttributeDef nameAttrDef = entity.getEtype().getNameAttrDef();
         IStructure nameValue = (IStructure) entity.getAttribute(nameAttrDef.getURL()).getValues().get(0).getValue();
         assertTrue(nameValue.getEtype() != null);
         */
        IntegrityChecker.checkEntity(entity);

        assertTrue(entity.getName().getString(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testReadNonExistingEntity() {
        IEkb disiEkb = new DisiEkb();
        assertEquals(disiEkb.getEntityService().readEntity("http://blabla.com"), null);
    }

    @Test
    public void testReadNonExistingEntities() {
        EntityService es = new EntityService(api);
        List<String> entitieURLs = new ArrayList<String>();
        entitieURLs.add("non-existing-url");
        entitieURLs.add(WebServiceURLs.entityIDToURL(RAVAZZONE_ID));
        thrown.expect(DisiClientException.class);
        List<IEntity> entities = es.readEntities(entitieURLs);
        assertEquals(entities.get(0), null);
        logger.info(entities.get(1).getEtype().getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(entities.get(1).getName().getStrings(Locale.ITALIAN).get(0), "Ravazzone");
    }

    @Test
    public void testUpdateNonExistingEntity() {
        EntityODR entity = new EntityODR();
        IEkb ekb = new DisiEkb();
        IEntityService es = ekb.getEntityService();
        entity.setEntityAttributes(new ArrayList<IAttribute>());
        entity.setEtype(ekb.getEntityTypeService().getEntityType(FACILITY_URL));
        entity.setEntityBaseId(1L);
        entity.setURL("http://blabla.org");
        try {
            es.updateEntity(entity);
            fail("Should have failed while updating non existing entity!");
        } catch (NotFoundException ex) {

        }
    }

    @Test
    public void testEntityReadByGlobalID() {
        EntityService es = new EntityService(api);
        EntityODR entity = (EntityODR) es.readEntityByGUID(10000466L);
        IntegrityChecker.checkEntity(entity);
        logger.info(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0), "Infrastruttura");
    }

    @Test
    public void testCreateDeleteEntity() {

        //initialising variables
        EntityService es = new EntityService(api);
        InstanceClient instanceClient = new InstanceClient(api);
        AttributeClient attrClient = new AttributeClient(api);
        ComplexTypeClient ctypecl = new ComplexTypeClient(api);

        Instance inst = instanceClient.readInstance(15007L, null);

        EntityODR entityToCreate = new EntityODR();
        List<Attribute> attributes = new ArrayList<Attribute>();
        ComplexType cType = ctypecl.readComplexType(inst.getTypeId(), null);
        EntityType etype = new EntityType(cType);
		//List<Name> names = new ArrayList<Name>();

        //instantiation of variables
        attributes = attrClient.readAttributes(15007L, null, null);
        //EntityTypeService es = new EntityTypeService();
        //	EntityType etype= es.getEntityType(e.getTypeId());

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        Long attrDefClassAtrID = null;
        for (IAttributeDef adef : attrDefs) {

            if (adef.getName().getString(Locale.ENGLISH).equalsIgnoreCase("class")) {
                attrDefClassAtrID = adef.getGUID();
                break;
            }
        }

        //	boolean isExistAttrClass=false;
        ArrayList<Attribute> attrsEntityToCreate = new ArrayList<Attribute>();

        for (Attribute a : attributes) {

            if (a.getDefinitionId() != attrDefClassAtrID) {
                System.out.println(a.getName().get("en"));
                attrsEntityToCreate.add(a);
            }
        }
        //logger.info("Etype id: "+inst.getTypeId());
        //assigning variables
        entityToCreate.setAttributes(attrsEntityToCreate);
        entityToCreate.setEtype(etype);
        entityToCreate.setEntityBaseId(1L);
        //  logger.info("entity: " + entity.toString());
        //es.createEntity(entity);

        EbClient ebc = new EbClient(api);
        EntityBase eb = ebc.readEntityBase(1L, null);
        int instanceNum = eb.getInstancesNumber();

        String entityURL = es.createEntityURL(entityToCreate);
        //        es.ge
        //        inst = instanceClient.readInstance(id, null);
        EntityBase ebafter = ebc.readEntityBase(1L, null);
        int instanceNumAfter = ebafter.getInstancesNumber();
        assertEquals(instanceNum + 1, instanceNumAfter);

        es.deleteEntity(entityURL);
        EntityBase ebafterDel = ebc.readEntityBase(1L, null);
        int instanceNumAfterDel = ebafterDel.getInstancesNumber();
        assertEquals(instanceNumAfterDel, instanceNumAfterDel);

    }

    @Test
    public void testReadEntityRavazzone() {
        EntityService es = new EntityService(api);
        IEntity entity = es.readEntity(RAVAZZONE_URL);
        IntegrityChecker.checkEntity(entity);
        logger.info(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0), "Località");
    }

    @Test
    public void testReadCampanilPartenza() {
        EntityService es = new EntityService(api);
        EntityODR entity = (EntityODR) es.readEntity(CAMPANIL_PARTENZA_URL);
        IntegrityChecker.checkEntity(entity);
        logger.info(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));

        assertTrue(entity.getName().getStrings(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().getStrings(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().getStrings(Locale.ENGLISH).get(0).length() > 0);
        assertNotNull(((ISemanticText) entity.getAttribute(ATTR_DEF_DESCRIPTION_URL).getValues().get(0).getValue()).getLocale());
    }


    @Test
    public void testReadStructure() {
        EntityService es = new EntityService(api);
        Structure structure = (Structure) es.readStructure(64001L);
        IntegrityChecker.checkStructure(structure);
        logger.info(structure.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(structure.getEtype().getName().getStrings(Locale.ITALIAN).get(0), "Nome");
    }

    @Test
    public void testReadEntities() {
        EntityService es = new EntityService(api);
        List<String> entitieURLs = new ArrayList<String>();
        entitieURLs.add(PALAZZETTO_URL);

        entitieURLs.add(RAVAZZONE_URL);
        List<IEntity> entities = es.readEntities(entitieURLs);
        for (IEntity entity : entities) {
            IntegrityChecker.checkEntity(entity);
        }
        
        logger.info(entities.get(0).getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(entities.get(1).getName().getStrings(Locale.ITALIAN).get(0), "PALAZZETTO DELLO SPORT");
        logger.info(entities.get(1).getEtype().getName().getStrings(Locale.ITALIAN).get(0));
        assertEquals(entities.get(0).getName().getStrings(Locale.ITALIAN).get(0), "Ravazzone");
    }

    @Test
    public void testReadZeroEntities() {
        EntityService es = new EntityService(api);
        assertEquals(es.readEntities(new ArrayList<String>()).size(), 0);
    }

    @Test
    public void testUpdateEntity() {
        EntityService es = new EntityService(api);
        EntityODR entity = (EntityODR) es.readEntity(64000L);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
//                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
//                AttributeODR attr = es.createAttribute(atDef, 11.466f);
//                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
//                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
//                AttributeODR attr = es.createAttribute(atDef, 46.289f);
//                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);

            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                ConceptODR concept = new ConceptODR();
                concept = concept.readConcept(GYMNASIUM_CONCEPT_ID);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = es.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();

                attrs1.add(a);
            }
        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);
        Long id = es.createEntity(ent);

        IEntity newEntity = es.readEntity(id);
        EntityODR newEntityODR = (EntityODR) newEntity;
        List<Attribute> newAttrs = newEntityODR.getAttributes();
        //--------Entity Update Test start

        assertEquals(4, newEntity.getStructureAttributes().size());

        AttributeDef openHourAD = new AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR);
        AttributeDef closeHourAD = new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);

        HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef, Object>();
        attrMap.put(openHourAD, "8:00");
        attrMap.put(closeHourAD, "8.00" + System.currentTimeMillis());
        IAttributeDef attrDef = new AttributeDef(66L);

        AttributeODR attr = es.createAttribute(attrDef, attrMap);

        List<IAttribute> attributes = newEntity.getStructureAttributes();
        attributes.add(attr);
        newEntity.setStructureAttributes(attributes);

        es.updateEntity(newEntity);

        IEntity updatedEntity = es.readEntity(id);
        assertEquals(5, updatedEntity.getStructureAttributes().size());
        //--------Entity Update Test end---------
        //--------Value Update Test start--------
        ValueODR val = new ValueODR();
        Float testNewValue = 0.0f;
        val.setValue(testNewValue);

        for (Attribute atr : newAttrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                AttributeODR attrODR = new AttributeODR(api, atr);
                //				ValueODR val = (ValueODR) attrODR.getValues().get(0);
                //				val.setValue(value);
                es.updateAttributeValue(newEntityODR, attrODR, val);
            }
        }

        EntityODR entityUpdValue = (EntityODR) es.readEntity(id);

        List<Attribute> updAttributes = entityUpdValue.getAttributes();

        for (Attribute atr : updAttributes) {
            if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                assertEquals(testNewValue, atr.getValues().get(0).getValue());
            }
        }

        es.deleteEntity(id);

    }

    @Test
    public void testCreateAttributeEntity() {
        EntityService es = new EntityService(api);
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = ets.getEntityType(FACILITY_ID);
        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList<Attribute>();

        for (IAttributeDef atd : attrDefList) {
            //			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
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

            if (atd.getName().getString(Locale.ENGLISH).equals("Name")) {
                //  logger.info(atd.getName());
                AttributeODR attr = es.createAttribute(atd, "TestName");
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().getString(Locale.ENGLISH).equals("Class")) {
                //  logger.info(atd.getName());
                AttributeODR attr = es.createAttribute(atd, 123L);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().getString(Locale.ENGLISH).equals("Latitude")) {
                //       logger.info(atd.getName());
                AttributeODR attr = es.createAttribute(atd, 12.123F);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().getString(Locale.ENGLISH).equals("Longitude")) {
                //     logger.info(atd.getName());
                AttributeODR attr = es.createAttribute(atd, 56.567F);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            } else if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")) {
                //     logger.info(atd.getName());
                //      logger.info(atd.getURL());

                AttributeDef openHourAD = new AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR);
                AttributeDef closeHourAD = new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);

                HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef, Object>();
                attrMap.put(openHourAD, "8:00");
                attrMap.put(closeHourAD, "18:00");

                AttributeODR attr = es.createAttribute(atd, attrMap);
                Attribute a = attr.convertToAttribute();
                attrs.add(a);
            }

        }
        EntityODR e = new EntityODR();
        e.setEntityBaseId(1L);
        e.setTypeId(18L);
        e.setAttributes(attrs);
        long id = es.createEntity(e);
        logger.info("Entity id:" + id);
        assertTrue(id > 0);
        es.deleteEntity(id);
    }
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createNameStructure() {

        EntityService es = new EntityService();
        EntityODR en = (EntityODR) es.readEntity(15001L);
//    	
//    	AttributeDef atrDef = new AttributeDef(169);
//    	AttributeODR nameAtr = es.createNameAttributeODR(atrDef, name);

        for (IAttribute a : ((IStructure) en).getStructureAttributes()) {

//    		System.out.println(a.getAttrDef().getName().getString(Locale.ENGLISH));
//    		//System.out.println(a.getAttrDef().getRangeEtypeURL());
//    		//System.out.println(a.getAttrDef().getEType());
//    		System.out.println(a.getAttrDef().getGUID());
//    		System.out.println(a.getAttrDef().getConcept().getGUID());
        }
    }

    @Test
    public void testReadEntity_2() {
        EntityService es = new EntityService();
        es.readEntity("http://opendata.disi.unitn.it:8080/odr/instances/41950");
    }

    /**
     * Andalo is nasty as it has a name type "Place Name" with ID 23, instead of
     * the usual one with ID 10
     */
    @Test
    public void testReadAndalo() {
        EntityService es = new EntityService();
        IEntity en = es.readEntity(ANDALO_URL);

        IntegrityChecker.checkEntity(en);
        
        IAttributeDef nameAttrDef = en.getEtype().getNameAttrDef();
        String nameAttrDefURL = nameAttrDef.getURL();
        logger.info("nameAttrDefURL = " + nameAttrDefURL);

        IAttribute nameAttr = en.getAttribute(nameAttrDefURL);

        assertEquals(nameAttrDefURL, nameAttr.getAttrDef().getURL());

        IStructure nameStruct = (IStructure) nameAttr.getValues().get(0).getValue();

        assertEquals(nameAttrDef.getRangeEtypeURL(), nameStruct.getEtypeURL());

    }
   
    //@Test 
    public void testDisify(){
    	EntityService es = new EntityService();
        IEntity en = es.readEntity(CAMPANIL_PARTENZA_URL);
        EntityODR e = (EntityODR) en;
        IEntity ent = EntityODR.disify(e, true);
        Long l=es.createEntity(ent);
        logger.info(l.toString());
        assertNotNull(ent);
    }
}
