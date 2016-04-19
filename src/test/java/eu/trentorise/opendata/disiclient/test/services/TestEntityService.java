package eu.trentorise.opendata.disiclient.test.services;

import com.google.common.collect.ImmutableList;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;

import com.google.common.collect.Lists;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.attrDefIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.conceptIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.entityIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.etypeIDToURL;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.EbClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.EntityBase;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import eu.trentorise.opendata.disiclient.test.DisiClientTestRunner;
 



/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
 @RunWith(DisiClientTestRunner.class)
public class TestEntityService {

    public static final long OPENING_HOURS = 7L;
    public static String OPENING_HOURS_URL;

    public static final long ATTR_DEF_FACILITY_OPENING_HOURS = 66L;
    public static String ATTR_DEF_FACILITY_OPENING_HOURS_URL;

    public static final long ATTR_DEF_HOURS_OPENING_HOUR = 31L;
    public static String ATTR_DEF_HOURS_OPENING_HOUR_URL;
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR = 30L;
    public static String ATTR_DEF_HOURS_CLOSING_HOUR_URL;

    public static final long PALAZZETTO_ID = 64000L;

    public static final long BORGO_VALSUGANA_ID = 64000L;
    /**
     * Palazzetto is a Facility. It doesn't have description. Its concept is
     * gymnasium.
     */

    public static String PALAZZETTO_URL;
    public static final String PALAZZETTO_NAME_IT = "PALAZZETTO DELLO SPORT";
    public static final long GYMNASIUM_CONCEPT_ID = 18565L;
    public static String GYMNASIUM_CONCEPT_URL;

    /**
     * Ravazzone is a cool district of Mori.
     */
    public static final long RAVAZZONE_ID = 15001L;
    public static String RAVAZZONE_URL;
    public static final String RAVAZZONE_NAME_IT = "Ravazzone";
    public static final String RAVAZZONE_NAME_EN = "Ravazzone";
    public static final long ADMINISTRATIVE_DISTRICT_CONCEPT_ID = 10001L;
    public static String ADMIN_DISTRICT_CONCEPT_URL;

    public static final long RESIDENCE_DES_ALPES_ID = 66206L;
    public static String RESIDENCE_DES_ALPES_URL;

    public static final long COMANO_ID = 15007L;
    public static String COMANO_URL;
    
    
    /**
     * Rovereto URl
     */
    public static final long POVO_ID = 1024L;
    public static String POVO_URL;

    /**
     * "Campanil partenza" is a Facility. Entity concept is Detachable
     * chairlift. Has attributes orari. Has descriptions both in Italian and
     * English. Name is only in Italian.
     */
    public static final long CAMPANIL_PARTENZA_ID = 64235L;
    public static String CAMPANIL_PARTENZA_URL;
    public static final long DETACHABLE_CHAIRLIFT_CONCEPT_ID = 111009L;
    public static String DETACHABLE_CHAIRLIFT_CONCEPT_URL;
    public static final String CAMPANIL_PARTENZA_NAME_IT = "Campanil partenza";

    /**
     * Andalo is one of those nasty locations with "Place Name" as Name type
     */
    public static final long ANDALO_ID = 2089L;
    public static String ANDALO_URL;

    /**
     * See bug
     * https://github.com/opendatatrentino/disi-open-data-client/issues/34
     */
    public static final long FARMACIA_SILVESTRI_ID = 78078L;
    public static String FARMACIA_SILVESTRI_URL;

    public static final long CLASS_CONCEPT_ID = 21987L;
    public static String CLASS_CONCEPT_ID_URL;

    public static final long ROOT_ENTITY_ID = 21L;
    public static String ROOT_ENTITY_URL;

    public static final Long LOCATION_ID = 18L;
    public static String LOCATION_URL;

    // Facility
    public static final long FACILITY_ID = 12L;
    public static String FACILITY_URL;

    public static final long ATTR_DEF_LATITUDE_ID = 69L;
    public static String ATTR_DEF_LATITUDE_URL;
    public static final long ATTR_DEF_LONGITUDE_ID = 68L;
    public static String ATTR_DEF_LONGITUDE_URL;
    public static final long ATTR_DEF_CLASS = 58L;
    public static String ATTR_DEF_CLASS_URL;
    public static final long ATTR_DEF_DESCRIPTION = 62L;
    public static String ATTR_DEF_DESCRIPTION_URL;

    public static final long ATTR_DEF_PART_OF = 60L;
    /** Part-of has {@link #ROOT_ENTITY_URL} as range */
    public static String ATTR_DEF_PART_OF_URL;

    public static final long NAME_ID = 10L;
    public static String NAME_URL;

    // Shopping facility
    public static final long SHOPPING_FACILITY_ID = 1L;
    public static String SHOPPING_FACILITY_URL;

    // Certified product stuff
    public static final long CERTIFIED_PRODUCT_ID = 17L;
    public static String CERTIFIED_PRODUCT_URL;

    public static final long ATTR_TYPE_OF_CERTIFICATE = 110L;
    public static String ATTR_TYPE_OF_CERTIFICATE_URL;

    /**
     * It is of type 'Certified product' NOTE: CREATED WITH ODR, WILL DISAPPEAR
     * FROM SERVER ONCE IT IS REGENERATED
     */
    public static final long MELA_VAL_DI_NON = 75167L;
    /**
     * NOTE: CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS REGENERATED
     */
    public static String MELA_VAL_DI_NON_URL;

    private IProtocolClient api;

    private static final Logger logger = LoggerFactory.getLogger(TestEntityService.class);

    IEkb ekb;

    @Before
    public void getClientProtocol() {
	ekb = ConfigLoader.init();
	this.api = WebServiceURLs.getClientProtocol();
    }

    @Test
    public void testPalazzettoReadNameEtype() {

	EntityODR entity = (EntityODR) ekb.getEntityService().readEntity(PALAZZETTO_URL);
	logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
	IAttributeDef nameAttrDef = entity.getEtype().getNameAttrDef();
	IStructure nameValue = (IStructure) entity.getAttribute(nameAttrDef.getURL()).getValues().get(0).getValue();

	assertTrue(nameValue.getEtypeURL() != null);

	assertTrue(entity.getName().getString(Locale.ITALIAN).length() > 0);
	// assertTrue(entity.getDescription().getString(Locale.ITALIAN).length()
	// > 0);

    }

    @Test
    public void testPalazzettoRead() {

	EntityODR entity = (EntityODR) ekb.getEntityService().readEntity(PALAZZETTO_URL);
	logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
	/*
	 * This stuff should be caught by the integrity checker IAttributeDef
	 * nameAttrDef = entity.getEtype().getNameAttrDef(); IStructure
	 * nameValue = (IStructure)
	 * entity.getAttribute(nameAttrDef.getURL()).getValues().get(0).getValue
	 * (); assertTrue(nameValue.getEtype() != null);
	 */
	IntegrityChecker.checkEntity(entity);

	assertTrue(entity.getName().getString(Locale.ITALIAN).length() > 0);
	// assertTrue(entity.getDescription().getString(Locale.ITALIAN).length()
	// > 0);

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
	EntityService es = new EntityService(api);
	List<String> entitieURLs = new ArrayList<String>();
	entitieURLs.add("non-existing-url");
	entitieURLs.add(WebServiceURLs.entityIDToURL(RAVAZZONE_ID));
	thrown.expect(IllegalArgumentException.class);
	List<IEntity> entities = es.readEntities(entitieURLs);
	assertEquals(entities.get(0), null);
	logger.info(entities.get(1).getEtype().getName().getStrings(Locale.ITALIAN).get(0));
	assertEquals(entities.get(1).getName().getStrings(Locale.ITALIAN).get(0), "Ravazzone");
    }

    @Test
    @Ignore
    public void testUpdateNonExistingEntity() {
	EntityODR entity = new EntityODR();
	IEntityService es = ekb.getEntityService();
	entity.setEntityAttributes(new ArrayList<IAttribute>());
	entity.setEtype(ekb.getEntityTypeService().readEntityType(FACILITY_URL));
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
    @Parameters({"false", 
    "true" })
    //@TestCaseName("[{index}] {method}: {params}")
    public void testCreateDeleteEntity(
	    boolean omitClass) {	 
	
	// initialising variables
	EntityService es = new EntityService(api);
	InstanceClient instanceClient = new InstanceClient(api);
	AttributeClient attrClient = new AttributeClient(api);
	ComplexTypeClient ctypecl = new ComplexTypeClient(api);

	Instance inst = instanceClient.readInstance(COMANO_ID, null);

	EntityODR entityToCreate = new EntityODR();
	List<Attribute> attributes = new ArrayList<Attribute>();
	ComplexType cType = ctypecl.readComplexType(inst.getTypeId(), null);
	IEntityType etype = new EntityTypeService().readEntityType(WebServiceURLs.etypeIDToURL(cType.getId()));
	// List<Name> names = new ArrayList<Name>();

	// instantiation of variables
	attributes = attrClient.readAttributes(COMANO_ID, null, null);
	// EntityTypeService es = new EntityTypeService();
	// EntityType etype= es.getEntityType(e.getTypeId());

	List<IAttributeDef> attrDefs = etype.getAttributeDefs();
	Long attrDefClassAtrID = null;
	String classAttrDefUrl = null;
	
	for (IAttributeDef adef : attrDefs) {

	    if (adef.getName().getString(Locale.ENGLISH).equalsIgnoreCase("class")) {
		attrDefClassAtrID = adef.getGUID();
		classAttrDefUrl = adef.getURL();
		break;
	    }
	}

	// boolean isExistAttrClass=false;
	ArrayList<Attribute> attrsEntityToCreate = new ArrayList<Attribute>();

	for (Attribute a : attributes) {

	    if (!omitClass || a.getDefinitionId() != attrDefClassAtrID) {
		  
		// System.out.println(a.getName().get("en"));
		a.setInstanceId(null);
		a.setId(null);
		a.getValues().get(0).setId(null);
		a.getValues().get(0).setAttributeId(null);
		System.out.println(a.getValues());
		// a.setValues(null);
		attrsEntityToCreate.add(a);
	    }	    
	}
	// logger.info("Etype id: "+inst.getTypeId());
	// assigning variables
	entityToCreate.setAttributes(attrsEntityToCreate);
	entityToCreate.setEtype(etype);
	entityToCreate.setEntityBaseId(1L);
	// logger.info("entity: " + entity.toString());
	// es.createEntity(entity);

	EbClient ebc = new EbClient(api);
	EntityBase eb = ebc.readEntityBase(1L, null);
	int instanceNum = eb.getInstancesNumber();
	String entityURL = null;
	// entityURL = es.createEntityURL(entityToCreate);
	// es.deleteEntity(entityURL);
	try {
	    entityURL = es.createEntityURL(entityToCreate);
	    // es.ge
	    // inst = instanceClient.readInstance(id, null);
	    System.out.println(entityURL);
	    EntityBase ebafter = ebc.readEntityBase(1L, null);
	    IEntity en = es.readEntity(entityURL);
	    IAttribute attr = en.getAttribute(classAttrDefUrl);
	    // independently of omitClass, we have one.
	    assertEquals(1, attr.getValues().size());
	    
	    int instanceNumAfter = ebafter.getInstancesNumber();
	    assertEquals(instanceNum + 1, instanceNumAfter);
	} finally {
	    if (entityURL != null) {
		es.deleteEntity(entityURL);
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
	assertNotNull(((ISemanticText) entity.getAttribute(ATTR_DEF_DESCRIPTION_URL).getValues().get(0).getValue())
		.getLocale());
    }

    @Test
    public void testReadStructure() {
	EntityService es = new EntityService(api);
	StructureODR structure = (StructureODR) es.readStructure(64001L);
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
	// entitieURLs.add(POVO_URL);
	List<IEntity> entities = es.readEntities(entitieURLs);
	for (IEntity entity : entities) {
	    // IntegrityChecker.checkEntity(entity);
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
    @Ignore
    public void testUpdateEntity() {
	EntityService es = new EntityService(api);
	EntityODR entity = (EntityODR) es.readEntity(64000L);
	List<Attribute> attrs = entity.getAttributes();
	List<Attribute> attrs1 = new ArrayList<Attribute>();

	for (Attribute atr : attrs) {
	    if (atr.getName().get("en").equalsIgnoreCase("Name")) {
		attrs1.add(atr);
	    } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
		// IAttributeDef atDef = new
		// AttributeDef(atr.getDefinitionId());
		// AttributeODR attr = es.createAttribute(atDef, 11.466f);
		// Attribute a = attr.convertToAttribute();
		attrs1.add(atr);
	    } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
		// IAttributeDef atDef = new
		// AttributeDef(atr.getDefinitionId());
		// AttributeODR attr = es.createAttribute(atDef, 46.289f);
		// Attribute a = attr.convertToAttribute();
		attrs1.add(atr);

	    } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {

		ConceptODR concept = new KnowledgeService().readConcept(GYMNASIUM_CONCEPT_ID);
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
	// --------Entity Update Test start

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
	// --------Entity Update Test end---------
	// --------Value Update Test start--------
	ValueODR val = new ValueODR();
	Float testNewValue = 0.0f;
	val.setValue(testNewValue);

	for (Attribute atr : newAttrs) {

	    if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
		AttributeODR attrODR = new AttributeODR(api, atr);
		// ValueODR val = (ValueODR) attrODR.getValues().get(0);
		// val.setValue(value);
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
	    // if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
	    // logger.info(atd.getName());
	    // logger.info(atd.getGUID());
	    // logger.info(atd.getDataType());
	    // if (atd.getDataType().equals(DataTypes.STRUCTURE)){
	    // logger.info(atd.getRangeEType().getURL());
	    // EntityType etpe =
	    // ets.getEntityType(atd.getRangeEType().getURL());
	    // List<IAttributeDef>atsd = etpe.getAttributeDefs();
	    // for (IAttributeDef a:atsd){
	    // logger.info(a.getGUID());
	    // }
	    //
	    // }

	    if (atd.getName().getString(Locale.ENGLISH).equals("Name")) {
		// logger.info(atd.getName());
		AttributeODR attr = es.createAttribute(atd, "TestName");
		Attribute a = attr.convertToAttribute();
		attrs.add(a);
	    } else if (atd.getName().getString(Locale.ENGLISH).equals("Class")) {
		// logger.info(atd.getName());
		AttributeODR attr = es.createAttribute(atd, 123L);
		Attribute a = attr.convertToAttribute();
		attrs.add(a);
	    } else if (atd.getName().getString(Locale.ENGLISH).equals("Latitude")) {
		// logger.info(atd.getName());
		AttributeODR attr = es.createAttribute(atd, 12.123F);
		Attribute a = attr.convertToAttribute();
		attrs.add(a);
	    } else if (atd.getName().getString(Locale.ENGLISH).equals("Longitude")) {
		// logger.info(atd.getName());
		AttributeODR attr = es.createAttribute(atd, 56.567F);
		Attribute a = attr.convertToAttribute();
		attrs.add(a);
	    } else if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")) {
		// logger.info(atd.getName());
		// logger.info(atd.getURL());

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
	// AttributeDef atrDef = new AttributeDef(169);
	// AttributeODR nameAtr = es.createNameAttributeODR(atrDef, name);

	for (IAttribute a : ((IStructure) en).getStructureAttributes()) {

	    // System.out.println(a.getAttrDef().getName().getString(Locale.ENGLISH));
	    // //System.out.println(a.getAttrDef().getRangeEtypeURL());
	    // //System.out.println(a.getAttrDef().getEType());
	    // System.out.println(a.getAttrDef().getGUID());
	    // System.out.println(a.getAttrDef().getConcept().getGUID());
	}
    }

    @Test
    public void testReadEntity_2() {
	EntityService es = new EntityService();
	es.readEntity(WebServiceURLs.entityIDToURL(41950));
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

    /**
     * See bug
     * https://github.com/opendatatrentino/disi-open-data-client/issues/34
     * 
     * todo this fails!
     */    
    @Test
    @Ignore
    public void testReadFarmaciaSilvestri() {
	EntityService es = new EntityService();
	IEntity en = es.readEntity(FARMACIA_SILVESTRI_URL);

	IntegrityChecker.checkEntity(en);

	IAttributeDef nameAttrDef = en.getEtype().getNameAttrDef();
	String nameAttrDefURL = nameAttrDef.getURL();
	logger.info("nameAttrDefURL = " + nameAttrDefURL);

	IAttribute nameAttr = en.getAttribute(nameAttrDefURL);

	assertEquals(nameAttrDefURL, nameAttr.getAttrDef().getURL());

	IStructure nameStruct = (IStructure) nameAttr.getValues().get(0).getValue();

	IDict name = en.getName();
	assertNotNull(name);
	assertNotNull(name.getString(Locale.ENGLISH)); // todo this fails!
	assertTrue(name.getString(Locale.ENGLISH).toLowerCase().contains("silvestri"));
	
	
	assertEquals(nameAttrDef.getRangeEtypeURL(), nameStruct.getEtypeURL());
    }

    @Test
    public void testDisify() {
	EntityService es = new EntityService();
	IEntity en = es.readEntity(POVO_URL);
	EntityODR e = (EntityODR) en;

	AttributeODR a = (AttributeODR) en.getStructureAttributes().get(2);
	IValue val = new ValueODR();
	val.setValue(15.2f);
	a.addValue(val);

	IEntity ent = EntityODR.disify(e, true);
	assertNotNull(ent);
	String URL = null;
	try {
	    URL = es.createEntityURL(ent);
	} finally {
	    if (URL != null) {
		es.deleteEntity(URL);
	    }
	}
    }

    @Test
    public void testEntitySearch() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

	String etypeURL = WebServiceURLs.etypeIDToURL(18L);
	Locale locale = TraceProvUtils.languageTagToLocale("it");
	List<ISearchResult> sResults = enServ.searchEntities("Povo", etypeURL, locale);
	for (ISearchResult sr : sResults) {
	    assertNotNull(sr.getURL());
	    assertNotNull(sr.getName());

	}
    }

    @Test
    public void testEntitySearchAndalo() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

	String etypeURL = WebServiceURLs.etypeIDToURL(18L);
	Locale locale = TraceProvUtils.languageTagToLocale("it");
	List<ISearchResult> sResults = enServ.searchEntities("Andalo", etypeURL, locale);
	assertTrue(sResults.size() > 0);

	assertEquals("Andalo", sResults.get(0).getName().getString(Locale.ENGLISH));
    }

    @Test
    public void TestResidenceDesAlpes() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
	IEntity en = enServ.readEntity(RESIDENCE_DES_ALPES_URL);
	IntegrityChecker.checkEntity(en);
    }

    @Test
    public void testSearchIncompleteEntity() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
	List<ISearchResult> res = enServ.searchEntities("roveret", null, Locale.ITALIAN);
	assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchMultiWordEntity() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
	List<ISearchResult> res = enServ.searchEntities("borgo valsugana", null, Locale.ITALIAN);
	assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteMultiWordEntity() {
	EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
	List<ISearchResult> res = enServ.searchEntities("borgo valsu", null, Locale.ITALIAN);
	assertTrue(res.size() > 0);
    }
    
    
    /**
     * Creates instance only using sweb InstanceClient.
     * 
     * Shows in sweb you can't put SemanticString as an object inside a Value, 
     * you need to put a string and call Value.setSemanticValue(ss) 
     * 
     * Three attributes are created: name, publication date and class to fulfill an identifying set and
     * adds also the attribute 'Abstract', which is a SemanticString. The latter was causing the server 
     * to throw a 
     * <pre>
     *       java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
     *       at it.unitn.disi.sweb.webapi.server.translators.impl.ValueTranslator.syncValue(ValueTranslator.java:297)
     * </pre>
     *
     * @since 0.11.1
     */
    @Ignore  // only for digiuni, and fixed by #37
    @Test
    public void testCreateSemanticStringInDigiUni(){

	InstanceClient ic = new InstanceClient(WebServiceURLs.getClientProtocol());
	
	Entity ent = new Entity();	
	ent.setEntityBaseId(1L);
	ent.setTypeId(208L); // thesis
	
	Name name = new Name();
	name.setTypeId(8L);	
	name.setEntityBaseId(1L);
	addSwebValue(name, 65L, "zeName" ,"en");
	
	addSwebValue(ent, 407L, name, null);
	addSwebValue(ent, 399L, 5, null); // publication date
	Concept concept = new Concept();
	concept.setId(33910L); // class
	addSwebValue(ent, 408L, concept, null); 
	
	// this was giving the problem:
	SemanticString ss = new SemanticString("bla");
	Value val = addSwebValue(ent, 400L, ss.getText(), null);  // Abstract
	val.setSemanticValue(ss);
	
	long res = ic.create(ent);
	
	logger.info("Done creating entity " + WebServiceURLs.entityIDToURL(res));
    }

    /**
     * Creates Attribute and fills it with obj as value's value, then returns the added Value.
     * @since 0.11.1
     */
    private static Value addSwebValue(Instance inst, Long attrDefId, Object obj, String languageCode){
		
	if (inst.getAttributes() == null){
	    inst.setAttributes(new ArrayList());
	}
	
	Attribute attr = new Attribute();	
	attr.setDefinitionId(attrDefId);	
	List<Value> values = new ArrayList();	
	Value val = new Value();	
	val.setValue(obj);		
	val.setLanguageCode(languageCode);
	
	values.add(val);
	attr.setValues(values);
	inst.getAttributes().add(attr);
	return val;
    }
}
