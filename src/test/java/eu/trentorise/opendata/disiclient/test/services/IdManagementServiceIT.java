package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.EntityService;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.ATTR_DEF_PART_OF_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.CERTIFIED_PRODUCT_ID;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.CERTIFIED_PRODUCT_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.GYMNASIUM_CONCEPT_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.LOCATION_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.PALAZZETTO_NAME_IT;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.PALAZZETTO_URL;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entities;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IdResult;
import eu.trentorise.opendata.semantics.services.mock.MockEntityService;
import eu.trentorise.opendata.traceprov.types.Concept;
import eu.trentorise.opendata.semantics.services.AssignmentResult;

import it.unitn.disi.sweb.webapi.model.eb.Attribute;

import it.unitn.disi.sweb.webapi.model.eb.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import eu.trentorise.opendata.commons.validation.Preconditions;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 *
 */
public class IdManagementServiceIT extends DisiTest {

    private static final Logger LOG = LoggerFactory.getLogger(IdManagementServiceIT.class);

    IEntityService enServ;
    IEtypeService ets;
    IIdentityService idServ;
    MockEntityService mockEs;

    @Before
    public void before() {
	enServ = ekb.getEntityService();
	idServ = ekb.getIdentityService();
	ets = ekb.getEtypeService();
	mockEs = new MockEntityService(ekb);
    }

    @After
    public void after() {
	enServ = null;
	idServ = null;
	ets = null;
	mockEs = null;
    }



    private String attributesToString(List<Attribute> attributes) {
	String str = "[";
	for (Attribute attr : attributes) {
	    str += attributeToString(attr) + "\n";
	}
	return str + "]";
    }

    private String attributeToString(Attribute attr) {
	String str = "attr concept_id:" + attr.getConceptId() + ", datatype:" + attr.getDataType() + " values[";
	for (Value v : attr.getValues()) {
	    str += v.getValue() + ", ";
	}
	return str + "]";
    }

    public Entity assignNewURL() {

	Entity entity = enServ.readEntity(PALAZZETTO_URL);
	Entity.Builder enb = Entity.builder();

	Etype etype = ets.readEtype(entity.getEtypeId());

	for (AttrDef attrDef : etype.getAttrDefs().values()) {
	    String adName = attrDef.getName().str(Locale.ENGLISH);
	    if (adName.equalsIgnoreCase("Foursquare ID")) {
		// System.out.println(atr.getName());
		Attr attr = Attr.ofObject(attrDef, "50f6e6f516488f6cc81a42fc");

		enb.putAttrs(attrDef.getId(), attr);
	    }

	}

	enb.setEtypeId(entity.getEtypeId());

	List<Entity> entities = new ArrayList();
	entities.add(enb.build());

	List<IdResult> results = idServ.assignURL(entities, 3);
	assertEquals(1, results.size());
	assertEquals(AssignmentResult.NEW, results.get(0).getAssignmentResult());
	assertNotNull(results.get(0).getResultEntity());

	return results.get(0).getResultEntity();
    }

    @Before
    public void beforeMethod() {
	ConfigLoader.init();
    }

    @Test
    public void idServiceEntityNew() {
	assignNewURL();
    }

    /**
     *
     * Don't want errors on empty array
     */
    @Test
    public void testIdManagementEmptyArray() {

	List res = idServ.assignURL(new ArrayList(), 3);
	assertTrue(res.isEmpty());
    }

    @Test
    public void testIdManagementReuse() {

	String name = PALAZZETTO_NAME_IT;
	Entity entity = enServ.readEntity(PALAZZETTO_URL);
	Entity.Builder enb = Entity.builder();
	Etype etype = ets.readEtype(entity.getEtypeId());

	enb.setNameAttr(Dict.of("a"), etype.getId(), ets);
	for (AttrDef attrDef : etype.getAttrDefs().values()) {

	    String adName = attrDef.getName().str(Locale.ENGLISH);
	    if (adName.equalsIgnoreCase("Longitude")) {
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, 11.466894f));
	    } else if (adName.equalsIgnoreCase("Latitude")) {
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, 46.289413f));
	    } else if (adName.equalsIgnoreCase("Class")) {
		Concept concept = ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, concept));
	    }
	}
	enb.setEtypeId(FACILITY_URL);
	// en.setGlobalId(10002538L);
	Entity ent = enb.build();

	List<Entity> entities = new ArrayList();
	entities.add(ent);

	List<IdResult> results = idServ.assignURL(entities, 3);
	for (IdResult res : results) {
	    Entity newEntity = res.getResultEntity();
	    assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
	    Preconditions.checkNotDirtyUrl(newEntity.getId(), "Invalid new entity id!");
	}
    }

    @Test
    public void testFacilityIdMissingClass() {

	String name = PALAZZETTO_NAME_IT;
	Entity entity = enServ.readEntity(PALAZZETTO_URL);
	Entity.Builder enb = Entity.builder();
	Etype etype = ets.readEtype(entity.getEtypeId());

	enb.setNameAttr(Dict.of(name), entity.getEtypeId(), ets);

	for (AttrDef attrDef : etype.getAttrDefs().values()) {
	    String adName = attrDef.getName().str(Locale.ENGLISH);
	    if (adName.equalsIgnoreCase("Longitude")) {
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, 11.466894f));
	    } else if (adName.equalsIgnoreCase("Latitude")) {
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, 46.289413f));
	    }
	}

	enb.setEtypeId(FACILITY_URL);

	Entity ent = enb.build();

	List<Entity> entities = new ArrayList();
	entities.add(ent);

	List<IdResult> results = idServ.assignURL(entities, 3);
	for (IdResult res : results) {
	    assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
	    Preconditions.checkNotDirtyUrl(res.getResultEntity().getId(), "Invalid id result for new entity!");
	}
    }

    @Test
    public void testMissingClassCertifiedProduct() {

	Entity.Builder enb = Entity.builder();

	Etype et = ets.readEtype(CERTIFIED_PRODUCT_URL);

	AttrDef certificateTypeAttrDef = et.attrDefById(DisiTest.ATTR_TYPE_OF_CERTIFICATE_URL);

	assertNotNull(certificateTypeAttrDef);

	enb.putAttrs(DisiTest.ATTR_TYPE_OF_CERTIFICATE_URL, Attr.ofObject(certificateTypeAttrDef, "Please work"));
	enb.setEtypeId(CERTIFIED_PRODUCT_URL);

	List<Entity> entities = new ArrayList();
	entities.add(enb.build());

	List<IdResult> results = idServ.assignURL(entities, 3);
	for (IdResult res : results) {
	    assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
	}
    }

    @Test
    public void testRelationalAttribute() {

	Entity.Builder enb = Entity.builder();

	Etype etype = ets.readEtype(FACILITY_URL);
	enb.setEtypeId(FACILITY_URL);

	enb.setNameAttr(Dict.of("test entity"), etype.getId(), ets);

	Attr partOfAttr = Attr.ofObject(etype.attrDefById(DisiTest.ATTR_DEF_PART_OF_URL),
		mockEs.newEntity(PALAZZETTO_URL, etype, Dict.of(), Dict.of()));
	enb.putAttrs(DisiTest.ATTR_DEF_PART_OF_URL, partOfAttr);

	idServ.assignURL(Lists.newArrayList(enb.build()), 3);

    }

    @Test
    public void testNewEntityWithPartOfNewEntity() {

	// Entity entityPartOf = new MinimalEntity(RAVAZZONE_URL, new Dict(),
	// new Dict(), null);

	// assertNotNull(entityPartOf.getEtypeURL());

	Entity newEntity = assignNewURL();
	Etype etype = ets.readEtype(newEntity.getEtypeId());

	
	Entity.Builder enb = Entity.builder().from(newEntity);
	

	enb.setAttrs(OdtUtils.putKey(newEntity.getAttrs(),
		ATTR_DEF_PART_OF_URL,	
		Attr.ofObject(ets.readAttrDef(ATTR_DEF_PART_OF_URL),
			mockEs.newEntity("http://trial/instances/new/1234567", etype, Dict.of(), Dict.of()))));			

	List<IdResult> idRes = idServ.assignURL(Arrays.asList(enb.build()), 3);

    }

    @Test
    public void testIdServiceEntityMissing() {

	Entity palaz = enServ.readEntity(PALAZZETTO_URL);

	Entity.Builder enb = Entity.builder();

	for (String attrDefId : palaz.getAttrs().keySet()) {

	    AttrDef attrDef = ets.readAttrDef(attrDefId);
	    String adName = attrDef.getName().str(Locale.ENGLISH);

	    if (adName.equalsIgnoreCase("Latitude") || adName.equalsIgnoreCase("Longitude")) {
		enb.putAttrs(attrDef.getId(), palaz.attr(attrDef.getId()));
	    } else if (adName.equalsIgnoreCase("Class")) {
		Concept concept = ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
		enb.putAttrs(attrDef.getId(), Attr.ofObject(attrDef, concept));
	    }
	}

	List<IdResult> results = idServ.assignURL(Lists.newArrayList(enb.build()), 3);

	for (IdResult res : results) {
	    Entity entityODR =  res.getResultEntity();
	    assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
	}
    }

}
