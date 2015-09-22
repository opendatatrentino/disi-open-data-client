package eu.trentorise.opendata.disiclient.test.services;


import com.google.common.collect.Lists;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;

import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.commons.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Struct;
import eu.trentorise.opendata.semantics.model.entity.Struct.Builder;
import eu.trentorise.opendata.semantics.model.entity.Val;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.EtypeService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;

import static eu.trentorise.opendata.disiclient.test.services.DisiTest.ekb;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.EbClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;

import it.unitn.disi.sweb.webapi.model.eb.EntityBase;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Structure;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.jena.riot.checker.CheckerBlankNodes;
import org.junit.After;
import org.junit.Assert;

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
public class EntityServiceIT extends DisiTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private IEntityService enServ;

    private IEtypeService ets;

    @Before
    public void before() {
        enServ = ekb.getEntityService();
        ets = ekb.getEtypeService();
    }

    @After
    public void after() {
        enServ = null;
        ets = null;
    }

    @Test
    public void testReadPalazzettoNameEtype() {

        Entity entity = ekb.getEntityService().readEntity(PALAZZETTO_URL);
        log.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n", (Object) entity);
                        
        AttrDef nameAttrDef = ets.readEtype(entity.getEtypeId()).nameAttrDef();
        Struct nameValue = (Struct) entity.attr(nameAttrDef.getId()).getValues().get(0).getObj();

        checker.checkStruct(nameValue);

        assertTrue(entity.getName().string(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testReadPalazzetto() {

        Entity entity = ekb.getEntityService().readEntity(PALAZZETTO_URL);
        log.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n", (Object) entity);
        /*               This stuff should be caught by the integrity checker 
         AttrDef nameAttrDef = entity.getEtype().getNameAttrDef();
         Struct nameValue = (Struct) entity.getAttribute(nameAttrDef.getURL()).getValues().strs(0).getValue();
         assertTrue(nameValue.getEtype() != null);
         */
        checker.checkEntity(entity);

        assertTrue(entity.getName().string(Locale.ITALIAN).length() > 0);
        // assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

    }

    @Test
    public void testReadNonExistingEntity() {
        try {
            ekb.getEntityService().readEntity(um.entityIdToUrl(10000000000000000L));
            Assert.fail("Shouldn't be able to read non existing entity!");
        } catch (OpenEntityNotFoundException ex){            
        }
    }

    @Test
    public void testReadNonExistingEntities() {

        List<String> entitieURLs = new ArrayList();
        entitieURLs.add(um.entityIdToUrl(10000000000000000L));
        entitieURLs.add(um.etypeIdToUrl(RAVAZZONE_ID));
        
        try {
            List<Entity> entities = enServ.readEntities(entitieURLs);
            Assert.fail("Shouldn't be able to read non existing entity!");
        } catch (OpenEntityNotFoundException ex){
            
        }
    }

    @Test
    public void testUpdateNonExistingEntity() {
        Entity.Builder entityB = Entity.builder();        
        entityB.setEtypeId(FACILITY_URL);        
        entityB.setId(um.entityIdToUrl(10000000000000000L));
        try {
            enServ.updateEntity(entityB.build());
            fail("Should have failed while updating non existing entity!");
        }
        catch (OpenEntityNotFoundException ex) {

        }
    }

    @Test
    public void testEntityReadByGlobalID() {
	
	
	it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity = ((EntityService) enServ).readEntityByGlobalId(10000466L);
	EntityType swebEntityType = ((EtypeService) ets).readSwebEntityType(swebEntity.getTypeId());
        Entity entity =  converter.swebEntityToOeEntity(swebEntity, swebEntityType);
        checker.checkEntity(entity);
        Etype etype = ets.readEtype(entity.getEtypeId());
        log.info(etype.getName().strings(Locale.ITALIAN).get(0));
        assertEquals(etype.getName().strings(Locale.ITALIAN).get(0), "Infrastruttura");
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
        Entity entity = enServ.readEntity(RAVAZZONE_URL);
        checker.checkEntity(entity);
        Etype etype = ekb.getEtypeService().readEtype(entity.getEtypeId());
        log.info(etype.getName().get(Locale.ITALIAN).get(0));
        assertEquals(etype.getName().get(Locale.ITALIAN).get(0), "Località");
    }

    @Test
    public void testReadCampanilPartenza() {

        Entity entity = enServ.readEntity(CAMPANIL_PARTENZA_URL);
        checker.checkEntity(entity);
        Etype etype = ets.readEtype(entity.getEtypeId());
        log.info(etype.getName().get(Locale.ITALIAN).get(0));

        assertTrue(entity.getName().get(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().get(Locale.ITALIAN).get(0).length() > 0);
        assertTrue(entity.getDescription().get(Locale.ENGLISH).get(0).length() > 0);
        assertNotNull(((SemText) entity.attr(ATTR_DEF_DESCRIPTION_URL).getValues().get(0).getObj()).getLocale());
    }

    @Test
    public void testReadStructure() {
        Structure struct = (Structure) ((EntityService) enServ).readSwebStructure(64001L);
        
        ComplexType etype = ((EtypeService) ets).readSwebComplexType(struct.getTypeId());
        log.info(etype.getName().get("it"));
        assertEquals(etype.getName().get("it"), "Nome");
    }

    @Test
    public void testReadEntities() {

        List<String> entitieURLs = new ArrayList();
        entitieURLs.add(PALAZZETTO_URL);

        entitieURLs.add(RAVAZZONE_URL);
        // entitieURLs.add(POVO_URL);
        List<Entity> entities = enServ.readEntities(entitieURLs);
        for (Entity entity : entities) {
            checker.checkEntity(entity);
        }

        log.info(entities.get(0).getName().get(Locale.ITALIAN).get(0));
        assertEquals("PALAZZETTO DELLO SPORT", entities.get(1).getName().get(Locale.ITALIAN).get(0));
        String name = readEtype(entities.get(1)).getName().get(Locale.ITALIAN).get(0);
        log.info(name);
        assertEquals("Infrastruttura", name);
    }

    @Test
    public void testReadZeroEntities() {
        assertEquals(enServ.readEntities(new ArrayList<String>()).size(), 0);
    }

    @Test
    @Ignore
    public void testUpdateEntity() {
	throw new UnsupportedOperationException("TODO IMPLEMENT ME!");
	/*
        Entity entity =  enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                //                AttrDef atDef = new AttributeDef(atr.getDefinitionId());
                //                AttributeODR attr = enServ.createAttribute(atDef, 11.466f);
                //                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                //                AttrDef atDef = new AttributeDef(atr.getDefinitionId());
                //                AttributeODR attr = enServ.createAttribute(atDef, 46.289f);
                //                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);

            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                Concept concept =  ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
                AttrDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, concept);
                Attribute a = attr.asSwebAttribute();

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

        assertEquals(4, newEntityODR.getAttributes().size());

        AttributeDef openHourAD = new AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR);
        AttributeDef closeHourAD = new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);

        HashMap<AttributeDef, Object> attrMap = new HashMap();
        attrMap.put(openHourAD, "8:00");
        attrMap.put(closeHourAD, "8.00" + System.currentTimeMillis());
        AttrDef attrDef = new AttributeDef(66L);

        Attr attr = enServ.createAttribute(attrDef, attrMap);

        List<Attr> attributes = newEntityODR.getAttributes();
        attributes.add(attr);
        newEntityODR.setAttributes(attributes);

        enServ.updateEntity(newEntityODR);

        Entity updatedEntity = enServ.readEntity(id);
        assertEquals(5, updatedEntity.getAttributes().size());
        //--------Entity Update Test end---------
        //--------Value Update Test start--------
        Float testNewValue = 0.0f;
        ValueODR val = new ValueODR(null, null, testNewValue);

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
*/
    }

    @Test
    public void testSwebCreateMinimalEntity() {
                
	it.unitn.disi.sweb.webapi.model.eb.Entity ent = new it.unitn.disi.sweb.webapi.model.eb.Entity();
	EntityType swebEtype = ((EtypeService)  ets).readSwebEntityType(ROOT_ENTITY_ID);
	Long conceptId = swebEtype.getConceptId();
	it.unitn.disi.sweb.webapi.model.kb.concepts.Concept concept = ((KnowledgeService)  ekb.getKnowledgeService()).readConceptById(conceptId);
        ent.setTypeId(ROOT_ENTITY_ID);
        Value val = new Value();
        val.setValue(concept);
        Attribute attr = new Attribute();
        attr.setDefinitionId(ATTR_DEF_CLASS_ID);
        attr.setValues(Lists.newArrayList(val));
        ent.setAttributes(Lists.newArrayList(attr));
        InstanceClient instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
        instanceClient.create(ent);
    }

    @Test
    public void testCreateAttributeEntity() {

        Etype etype = ekb.getEtypeService().readEtype(FACILITY_URL);
        
        Entity.Builder enb = Entity.builder();                

        for (AttrDef atd : etype.getAttrDefs().values()) {

            String adName = atd.getName().str(Locale.ENGLISH);
            
            if (adName.equals("Name")) {
                enb.putAttrs(atd.getId(), Attr.ofObject(atd, "TestName"));
            } else if (adName.equals("Class")) {                
                enb.putAttrs(atd.getId(), Attr.ofObject(atd, 123L));
            } else if (adName.equals("Latitude")) {
        	enb.putAttrs(atd.getId(), Attr.ofObject(atd, 12.123F));
            } else if (adName.equals("Longitude")) {
        	enb.putAttrs(atd.getId(), Attr.ofObject(atd, 56.567F));
            } else if (adName.equals("Opening hours")) {
                //     logger.info(atd.getName());
                //      logger.info(atd.getURL());
        	
        	Struct.Builder strub = Struct.builder();
        	
        	Etype opHoursEt = ets.readEtype(OPENING_HOURS_URL);
        	
                AttrDef openHourAD = opHoursEt.attrDefById(ATTR_DEF_HOURS_OPENING_HOUR_URL);
                AttrDef closeHourAD = opHoursEt.attrDefById(ATTR_DEF_HOURS_CLOSING_HOUR_URL);

                HashMap<AttrDef, Object> attrMap = new HashMap();
                strub.putAttrs(ATTR_DEF_HOURS_OPENING_HOUR_URL, Attr.ofObject(openHourAD, "8:00"));
                strub.putAttrs(ATTR_DEF_HOURS_CLOSING_HOUR_URL, Attr.ofObject(closeHourAD, "18:00"));
                
                enb.putAttrs(ATTR_DEF_FACILITY_OPENING_HOURS_URL, Attr.ofObject(etype.attrDefById(ATTR_DEF_FACILITY_OPENING_HOURS_URL), strub.build()));
            }

        }
        
        
        enb.setEtypeId(LOCATION_URL);
        
        Entity newEntity = enServ.createEntity(enb.build());
        checker.checkEntity(newEntity);
        log.info("Entity id:" + newEntity.getId());
        
        enServ.deleteEntity(newEntity.getId());
    }
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Etype readEtype(Entity en) {
        return ekb.getEtypeService().readEtype(en.getEtypeId());
    }

    /**
     * Andalo is nasty as it has a name type "Place Name" with ID 23, instead of
     * the usual one with ID 10
     */
    @Test
    public void testReadAndalo() {

        Entity en = enServ.readEntity(ANDALO_URL);

        checker.checkEntity(en);

        Etype etype = readEtype(en);
        
        AttrDef nameAttrDef = etype.nameAttrDef();
        
        log.info("nameAttrDefURL = " + nameAttrDef.getId());

        Attr nameAttr = en.attr(nameAttrDef.getId());

        assertEquals(nameAttrDef.getId(), nameAttr.getAttrDefId());

        Struct nameStruct = (Struct) nameAttr.getValues().get(0).getObj();

        assertEquals(nameAttrDef.getType().getEtypeId(), nameStruct.getEtypeId());

    }

    @Test
    public void testReadRavazzone() {
        Entity en = enServ.readEntity(RAVAZZONE_URL);
        checker.checkEntity(en);
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
    public void TestResidenceDesAlpes() {
        Entity en = enServ.readEntity(RESIDENCE_DES_ALPES_URL);
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
