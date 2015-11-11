package eu.trentorise.opendata.disiclient.test.services;

import com.google.common.collect.Lists;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;

import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.AStruct;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Struct;
import eu.trentorise.opendata.semantics.model.entity.Struct.Builder;
import eu.trentorise.opendata.semantics.model.entity.Val;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.traceprov.types.Concept;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.semantics.services.mock.MockEntityService;
import eu.trentorise.opendata.commons.TodUtils;
import eu.trentorise.opendata.disiclient.services.EtypeService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;

import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.disiclient.test.services.DisiTest.ekb;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.EntityQuery;
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
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;

import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceIT.class);

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
	LOG.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n",
		(Object) entity);

	AttrDef nameAttrDef = ets.readEtype(entity.getEtypeId()).nameAttrDef();
	Struct nameValue = (Struct) entity.attr(nameAttrDef.getId()).getValues().get(0).getObj();

	checker.checkStruct(nameValue);

	assertTrue(entity.getName().str(Locale.ITALIAN).length() > 0);
	// assertTrue(entity.getDescription().getString(Locale.ITALIAN).length()
	// > 0);

    }

    @Test
    public void testReadPalazzetto() {

	Entity entity = ekb.getEntityService().readEntity(PALAZZETTO_URL);
	LOG.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n",
		(Object) entity);
	/*
	 * This stuff should be caught by the integrity checker AttrDef
	 * nameAttrDef = entity.getEtype().getNameAttrDef(); Struct nameValue =
	 * (Struct)
	 * entity.getAttribute(nameAttrDef.getURL()).getValues().strs(0).
	 * getValue(); assertTrue(nameValue.getEtype() != null);
	 */
	checker.checkEntity(entity);

	assertTrue(entity.getName().str(Locale.ITALIAN).length() > 0);
	// assertTrue(entity.getDescription().getString(Locale.ITALIAN).length()
	// > 0);

    }

    /**
     * Punte di campiglio had urls instead of Structure instance
     */
    @Test
    public void testCreatePunteDiCampiglio() {
	Entity en = enServ.readEntity(PUNTE_DI_CAMPIGLIO_URL);
	Entity newEntity = enServ.createEntity(en);
	checker.checkEntity(newEntity);
    }

    @Test
    public void testCreateName() {

	Dict.Builder namesBuilder = Dict.builder();
	Dict newNames = namesBuilder.put(Locale.ITALIAN, "Buon Giorno")
		.put(Locale.ENGLISH, "Hello")
		.put(Locale.FRENCH, "Bonjour").build();
	LOG.info(newNames.toString());

	Entity entity = enServ.readEntity(PALAZZETTO_URL);
	Entity.Builder enb = Entity.builder();
	Etype etype = ets.readEtype(entity.getEtypeId());

	enb.setNameAttr(newNames, entity.getEtypeId(), ets);
	enb.putObj(etype.attrDefByName("Longitude"), 11.466894f);
	enb.putObj(etype.attrDefByName("Latitude"), 46.289413f);

	enb.setEtypeId(FACILITY_URL);
	Entity newEn = enServ.createEntity(enb.build());
	checker.checkEntity(newEn);

    }

    /**
     * Shows reading non existing instances in sweb doesn't throw but just skips
     * the result.
     */
    @Test
    public void readSwebNonExistingInstances() {
	InstanceClient instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
	InstanceFilter instFilter = new InstanceFilter();
	List<Instance> instances = instanceClient.readInstancesById(Lists.newArrayList(-1L), instFilter);
	assertTrue(instances.isEmpty());
    }

    @Test
    public void testReadNonExistingEntity() {
	try {
	    ekb.getEntityService().readEntity(makeNonExistingEntityUrl());
	    Assert.fail("Shouldn't be able to read non existing entity!");
	} catch (OpenEntityNotFoundException ex) {
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
	} catch (OpenEntityNotFoundException ex) {

	}
    }

    @Test
    public void TestReadResidenceDesAlpes() {
	Entity en = enServ.readEntity(RESIDENCE_DES_ALPES_URL);
	checker.checkEntity(en);
    }

    @Test
    @Ignore
    public void testUpdateNonExistingEntity() {
	Entity.Builder entityB = Entity.builder();
	entityB.setEtypeId(FACILITY_URL);
	entityB.setId(um.entityIdToUrl(10000000000000000L));
	try {
	    enServ.updateEntity(entityB.build());
	    fail("Should have failed while updating non existing entity!");
	} catch (OpenEntityNotFoundException ex) {

	}
    }

    @Test
    public void testEntityReadByGlobalID() {

	it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity = ((EntityService) enServ).readEntityByGlobalId(10000466L);
	EntityType swebEntityType = ((EtypeService) ets).readSwebEntityType(swebEntity.getTypeId());
	Entity entity = converter.swebEntityToOeEntity(swebEntity, swebEntityType);
	checker.checkEntity(entity);
	Etype etype = ets.readEtype(entity.getEtypeId());
	LOG.info(etype.getName().strings(Locale.ITALIAN).get(0));
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
	LOG.info(etype.getName().get(Locale.ITALIAN).get(0));
	assertEquals(etype.getName().get(Locale.ITALIAN).get(0), "Località");
    }

    @Test
    public void testReadCampanilPartenza() {

	Entity entity = enServ.readEntity(CAMPANIL_PARTENZA_URL);
	checker.checkEntity(entity);
	Etype etype = ets.readEtype(entity.getEtypeId());
	LOG.info(etype.getName().get(Locale.ITALIAN).get(0));

	assertTrue(entity.getName().get(Locale.ITALIAN).get(0).length() > 0);
	assertTrue(entity.getDescription().get(Locale.ITALIAN).get(0).length() > 0);
	assertTrue(entity.getDescription().get(Locale.ENGLISH).get(0).length() > 0);
	assertNotNull(((SemText) entity.attr(ATTR_DEF_DESCRIPTION_URL).getValues().get(0).getObj()).getLocale());
    }

    @Test
    public void testReadStructure() {
	Structure struct = (Structure) ((EntityService) enServ).readSwebStructure(64001L);

	ComplexType etype = ((EtypeService) ets).readSwebComplexType(struct.getTypeId());
	LOG.info(etype.getName().get("it"));
	assertEquals(etype.getName().get("it"), "Nome");
    }
    

    @Test
    public void testReadStruct() {

        AStruct structure = enServ.readStruct(um.entityIdToUrl(DisiTest.KINDERGARDEN_CONTACT_ID));
        Attr attr = structure.attr(EntityServiceIT.ATTR_DEF_TELEPHONE_URL);
        checkNotNull(attr);
        
//		String url = structure.getEtypeURL();
//		System.out.println(url);
        assertEquals(EntityServiceIT.ATTR_DEF_TELEPHONE_URL, attr.getAttrDefId());
        checker.checkStruct(structure, false);
        assertNotNull(attr);
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

	LOG.info(entities.get(0).getName().get(Locale.ITALIAN).get(0));
	assertEquals("PALAZZETTO DELLO SPORT", entities.get(1).getName().get(Locale.ITALIAN).get(0));
	String name = readEtype(entities.get(1)).getName().get(Locale.ITALIAN).get(0);
	LOG.info(name);
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
	 * Entity entity = enServ.readEntity(PALAZZETTO_URL); List<Attribute>
	 * attrs = entity.getAttributes(); List<Attribute> attrs1 = new
	 * ArrayList();
	 * 
	 * for (Attribute atr : attrs) { if
	 * (atr.getName().get("en").equalsIgnoreCase("Name")) { attrs1.add(atr);
	 * } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
	 * // AttrDef atDef = new AttributeDef(atr.getDefinitionId()); //
	 * AttributeODR attr = enServ.createAttribute(atDef, 11.466f); //
	 * Attribute a = attr.convertToAttribute(); attrs1.add(atr); } else if
	 * (atr.getName().get("en").equalsIgnoreCase("Latitude")) { // AttrDef
	 * atDef = new AttributeDef(atr.getDefinitionId()); // AttributeODR attr
	 * = enServ.createAttribute(atDef, 46.289f); // Attribute a =
	 * attr.convertToAttribute(); attrs1.add(atr);
	 * 
	 * } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
	 * Concept concept =
	 * ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL); AttrDef
	 * atDef = new AttributeDef(atr.getDefinitionId()); AttributeODR attr =
	 * (AttributeODR) enServ.createAttribute(atDef, concept); Attribute a =
	 * attr.asSwebAttribute();
	 * 
	 * attrs1.add(a); } } Entity en = new Entity(); en.setEntityBaseId(1L);
	 * en.setTypeId(FACILITY_ID); en.setAttributes(attrs1); EntityODR ent =
	 * new EntityODR(en); String id = enServ.createEntityURL(ent);
	 * 
	 * EntityODR newEntityODR = (EntityODR) enServ.readEntity(id);
	 * 
	 * List<Attribute> newAttrs = newEntityODR.getAttributes();
	 * //--------Entity Update Test start
	 * 
	 * assertEquals(4, newEntityODR.getAttributes().size());
	 * 
	 * AttributeDef openHourAD = new
	 * AttributeDef(ATTR_DEF_HOURS_OPENING_HOUR); AttributeDef closeHourAD =
	 * new AttributeDef(ATTR_DEF_HOURS_CLOSING_HOUR);
	 * 
	 * HashMap<AttributeDef, Object> attrMap = new HashMap();
	 * attrMap.put(openHourAD, "8:00"); attrMap.put(closeHourAD, "8.00" +
	 * System.currentTimeMillis()); AttrDef attrDef = new AttributeDef(66L);
	 * 
	 * Attr attr = enServ.createAttribute(attrDef, attrMap);
	 * 
	 * List<Attr> attributes = newEntityODR.getAttributes();
	 * attributes.add(attr); newEntityODR.setAttributes(attributes);
	 * 
	 * enServ.updateEntity(newEntityODR);
	 * 
	 * Entity updatedEntity = enServ.readEntity(id); assertEquals(5,
	 * updatedEntity.getAttributes().size()); //--------Entity Update Test
	 * end--------- //--------Value Update Test start-------- Float
	 * testNewValue = 0.0f; ValueODR val = new ValueODR(null, null,
	 * testNewValue);
	 * 
	 * val.setValue(testNewValue);
	 * 
	 * for (Attribute atr : newAttrs) {
	 * 
	 * if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
	 * AttributeODR attrODR = new AttributeODR(atr); // ValueODR val =
	 * (ValueODR) attrODR.getValues().strs(0); // val.setValue(value);
	 * enServ.updateAttributeValue(newEntityODR, attrODR, val); } }
	 * 
	 * EntityODR entityUpdValue = (EntityODR) enServ.readEntity(id);
	 * 
	 * List<Attribute> updAttributes = entityUpdValue.getAttributes();
	 * 
	 * for (Attribute atr : updAttributes) { if
	 * (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
	 * assertEquals(testNewValue, atr.getValues().get(0).getValue()); } }
	 * 
	 * enServ.deleteEntity(id);
	 */
    }

    @Test
    public void testSwebCreateMinimalEntity() {

	it.unitn.disi.sweb.webapi.model.eb.Entity ent = new it.unitn.disi.sweb.webapi.model.eb.Entity();
	EntityType swebEtype = ((EtypeService) ets).readSwebEntityType(ROOT_ENTITY_ID);
	Long conceptId = swebEtype.getConceptId();
	it.unitn.disi.sweb.webapi.model.kb.concepts.Concept concept = ((KnowledgeService) ekb.getKnowledgeService())
		.readConceptById(conceptId);
	ent.setTypeId(ROOT_ENTITY_ID);
	LOG.warn("TODO - USING HARD-CODED ENTITYBASE ID SET TO 1");
	ent.setEntityBaseId(1L);
	Value val = new Value();
	val.setValue(concept);
	Attribute attr = new Attribute();
	attr.setDefinitionId(ATTR_DEF_CLASS_ID);
	attr.setValues(Lists.newArrayList(val));
	ent.setAttributes(Lists.newArrayList(attr));
	InstanceClient instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
	instanceClient.create(ent);
    }

    /**
     * Creates just one entity and then deletes it.
     */
    @Test
    public void testCreateEntity() {

	Etype etype = ekb.getEtypeService().readEtype(FACILITY_URL);

	Entity.Builder enb = Entity.builder();

	enb.setNameAttr(Dict.of("TestName"), etype.getId(), ekb.getEtypeService());

	Concept concept = Concept.builder()
		.setId(um.conceptIdToUrl(123L)).build();
	enb.putObj(etype.attrDefByName("Class"), concept);
	enb.putObj(etype.attrDefByName("Latitude"), 12.123F);
	enb.putObj(etype.attrDefByName("Longitude"), 56.567F);

	Struct.Builder strub = Struct.builder();

	Etype opHoursEt = ets.readEtype(OPENING_HOURS_URL);

	AttrDef openHourAD = opHoursEt.attrDefById(ATTR_DEF_HOURS_OPENING_HOUR_URL);
	AttrDef closeHourAD = opHoursEt.attrDefById(ATTR_DEF_HOURS_CLOSING_HOUR_URL);

	Map<AttrDef, Object> attrMap = new HashMap();
	strub.putAttrs(ATTR_DEF_HOURS_OPENING_HOUR_URL, Attr.ofObject(openHourAD, "8:00"));
	strub.putAttrs(ATTR_DEF_HOURS_CLOSING_HOUR_URL, Attr.ofObject(closeHourAD, "18:00"));
	strub.setEtypeId(opHoursEt.getId());

	enb.putObj(etype.attrDefById(ATTR_DEF_FACILITY_OPENING_HOURS_URL), strub.build());

	enb.setEtypeId(FACILITY_URL);

	Entity newEntity = enServ.createEntity(enb.build());
	checker.checkEntity(newEntity);
	LOG.info("Entity id:" + newEntity.getId());

	enServ.deleteEntity(newEntity.getId());
    }

    @Test
    public void testSearchAllEntities() {

	List<SearchResult> res = enServ.searchEntities(EntityQuery.of());
	LOG.info("Found ", res.size(), " entities");
	assertTrue(res.size() > 0);
    }

    /**
     * Creates one entity for each etype and then deletes it.
     */
    @Test
    public void testCreateOneEntityPerEtype() {
	List<Etype> etypes = ets.readAllEtypes();

	LOG.info("Read " + etypes.size() + " etypes. Going to create one entity per etype...");

	List<String> enUrls = new ArrayList();

	for (Etype etype : etypes) {

	    EntityQuery query = EntityQuery.builder()		    
		    .setEtypeId(etype.getId())		    
		    .build();

	    List<SearchResult> results = enServ.searchEntities(query);
	    if (!results.isEmpty()) {
		enUrls.add(results.get(0).getId());
	    }
	}

	List<Entity> entities = enServ.readEntities(enUrls);

	for (Entity en : entities) {
	    Entity newEntity = enServ.createEntity(en);
	    checker.checkEntity(newEntity);
	    enServ.deleteEntity(newEntity.getId());
	}

    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Etype readEtype(Entity en) {
	return ekb.getEtypeService().readEtype(en.getEtypeId());
    }

    /**
     * Andalo is nasty as it has a name type "Place Name" with ID 23, instead of
     * the usual one with ID 10 . Screw Andalo.
     */
    @Test
    public void testReadAndalo() {

	Entity en = enServ.readEntity(ANDALO_URL);

	checker.checkEntity(en);

	Etype etype = readEtype(en);

	AttrDef nameAttrDef = etype.nameAttrDef();

	LOG.info("nameAttrDefURL = " + nameAttrDef.getId());

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
    public void testSearchAndalo() {
	String enName = "Andalo";
	EntityQuery query = EntityQuery.builder()
		.setPartialName(enName)
		.setEtypeId(LOCATION_URL)
		.setLocale(Locale.ENGLISH)
		.build();

	List<SearchResult> sResults = enServ.searchEntities(query);
	assertTrue(sResults.size() > 0);

	assertEquals(enName, sResults.get(0).getName().str(Locale.ENGLISH));
    }

    @Test
    public void testSearchEntities() {

	EntityQuery query = EntityQuery.builder()
		.setPartialName("Povo")
		.setEtypeId(LOCATION_URL)
		.setLocale(Locale.ENGLISH)
		.build();

	List<SearchResult> sResults = enServ.searchEntities(query);
	for (SearchResult sr : sResults) {
	    assertNotNull(sr.getId());
	    assertNotNull(sr.getName());
	}
    }

    @Test
    public void testSearchIncompleteEntity() {

	EntityQuery query = EntityQuery.builder()
		.setPartialName("roveret")
		.setLocale(Locale.ITALIAN)
		.build();

	List<SearchResult> res = enServ.searchEntities(query);
	assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchMultiWordEntity() {

	EntityQuery query = EntityQuery.builder()
		.setPartialName("borgo valsugana")
		.setLocale(Locale.ITALIAN)
		.build();

	List<SearchResult> res = enServ.searchEntities(query);
	assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteMultiWordEntity() {

	EntityQuery query = EntityQuery.builder()
		.setPartialName("borgo valsu")
		.setLocale(Locale.ITALIAN)
		.build();

	List<SearchResult> res = enServ.searchEntities(query);
	assertTrue(res.size() > 0);

    }

}
