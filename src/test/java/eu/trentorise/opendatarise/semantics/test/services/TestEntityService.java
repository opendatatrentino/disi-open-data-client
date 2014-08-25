package eu.trentorise.opendatarise.semantics.test.services;

import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.DisiClientException;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.entity.ValueODR;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.services.Ekb;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.SemanticTextFactory;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 * * TODO REVIEW THIS CLASS IS A DUPLICATE OF TestEntityService2, although it seems more recent
 * @date 05 June 2014
 */
public class TestEntityService {

	public static final long ATTR_TYPE_OPENING_HOUR = 31L;
	public static final long ATTR_TYPE_CLOSING_HOUR = 30L;

	public static final long PALAZZETTO_ID = 64000L;
	public static final long RAVAZZONE_ID = 15001L;
	public static final long GYMNASIUM_CONCEPT_ID = 18565L;
	public static final String GYMNASIUM_CONCEPT_URL = WebServiceURLs.getURL() + "/concepts/" + GYMNASIUM_CONCEPT_ID;
	public static final String PALAZZETTO_URL = WebServiceURLs.getURL() + "/instances/" + PALAZZETTO_ID;
	public static final String RAVAZZONE_URL = WebServiceURLs.getURL() + "/instances/" + RAVAZZONE_ID;


	public static final long ATTR_DEF_LATTITUDE = 69L;
	public static final long ATTR_DEF_LONGITUDE = 68L;
	public static final long ATTR_DEF_CLASS = 58L;

	public static final long CLASS_CONCEPT_ID = 21987L;
	public static final long FACILITY_ID = 12L;

	public static final String ATTR_DEF_LATTITUDE_URL = WebServiceURLs.getURL() + "/attributedefinitions/" + ATTR_DEF_LATTITUDE;
	public static final String ATTR_DEF_LONGITUDE_URL = WebServiceURLs.getURL() + "/attributedefinitions/" + ATTR_DEF_LONGITUDE;
	public static final String ATTR_DEF_CLASS_URL = WebServiceURLs.getURL() + "/attributedefinitions/" + ATTR_DEF_CLASS;
	public static final String CLASS_CONCEPT_ID_URL = WebServiceURLs.getURL() + "/concepts/" + CLASS_CONCEPT_ID;
	public static final String FACILITY_URL = WebServiceURLs.getURL() + "/types/" + FACILITY_ID;


	private IProtocolClient api;	

	Logger logger = LoggerFactory.getLogger(this.getClass());



	@Before
	public void getClientProtocol() {
		this.api = WebServiceURLs.getClientProtocol();

	}


	@Test
	public void testPalazzettoRead() {
		IEkb disiEkb = new Ekb();

		EntityODR entity = (EntityODR) disiEkb.getEntityService().readEntity(PALAZZETTO_URL);
		logger.info("\n\n *************   entity Palazzetto (" + PALAZZETTO_URL + ") ***************** \n\n" + entity);
		IntegrityChecker.checkEntity(entity);
		assertTrue(entity.getName().getString(Locale.ITALIAN).length() > 0);
		// assertTrue(entity.getDescription().getString(Locale.ITALIAN).length() > 0);

	}

	@Test
	public void testReadNonExistingEntity(){
		IEkb disiEkb = new Ekb();
		assertEquals(disiEkb.getEntityService().readEntity("http://blabla.com"), null);
	}

	@Test
	public void testReadNonExistingEntities() {
		EntityService es = new EntityService(api);
		List<String> entitieURLs = new ArrayList();
		entitieURLs.add("non-existing-url");
		entitieURLs.add(SemanticTextFactory.entitypediaEntityIDToURL(RAVAZZONE_ID));
		thrown.expect(DisiClientException.class);
		List<IEntity> entities =  es.readEntities(entitieURLs);
		assertEquals(entities.get(0),null);
		logger.info(entities.get(1).getEtype().getName().getStrings(Locale.ITALIAN).get(0));
		assertEquals(entities.get(1).getName().getStrings(Locale.ITALIAN).get(0),"Ravazzone");
	}

	@Test
	public void testUpdateNonExistingEntity(){
		EntityODR entity = new EntityODR();
		IEkb ekb = new Ekb();
		IEntityService es = ekb.getEntityService();
		entity.setEntityAttributes(new ArrayList());
		entity.setEtype(ekb.getEntityTypeService().getEntityType(FACILITY_URL));
		entity.setEntityBaseId(1L);
		entity.setURL("http://blabla.org");
		try {
			es.updateEntity(entity);
			fail("Should have failed while updating non existing entity!");
		} catch(NotFoundException ex){

		}
	}


	@Test
	public void testEntityReadByGlobalID() {
		EntityService es = new EntityService(api);
		EntityODR entity = (EntityODR) es.readEntityByGUID(10000466L);
		logger.info(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
		assertEquals(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0),"Infrastruttura");
	}


	@Test
	public void testCreateDeleteEntity() {

		//initialising variables
		EntityService es = new EntityService(api);
		InstanceClient instanceClient = new InstanceClient(api);
		AttributeClient attrClient = new AttributeClient(api);
		ComplexTypeClient ctypecl = new ComplexTypeClient(api);

		Instance inst = instanceClient.readInstance(15007L, null);

		EntityODR entity = new EntityODR();
		List<Attribute> attributes = new ArrayList<Attribute>();
		ComplexType cType = ctypecl.readComplexType(inst.getTypeId(), null);
		EntityType etype = new EntityType(cType);
		//List<Name> names = new ArrayList<Name>();

		attributes = attrClient.readAttributes(15007L, null, null);
		//instantiation of variables

		attributes = attrClient.readAttributes(15007L, null, null);
		//logger.info("Etype id: "+inst.getTypeId());
		//assigning variables
		entity.setAttributes(attributes);
		entity.setEtype(etype);
		entity.setEntityBaseId(101L);
		//  logger.info("entity: " + entity.toString());
		//es.createEntity(entity);

		EbClient ebc = new EbClient(api);
		EntityBase eb = ebc.readEntityBase(101L, null);
		int instanceNum = eb.getInstancesNumber();

		String entityURL = es.createEntityURL(entity);
		//        es.ge
		//        inst = instanceClient.readInstance(id, null);
		EntityBase ebafter = ebc.readEntityBase(101L, null);
		int instanceNumAfter = ebafter.getInstancesNumber();
		assertEquals(instanceNum + 1, instanceNumAfter);

		es.deleteEntity(entityURL);
		EntityBase ebafterDel = ebc.readEntityBase(101L, null);
		int instanceNumAfterDel = ebafterDel.getInstancesNumber();
		assertEquals(instanceNumAfterDel, instanceNumAfterDel);

	}

	@Test
	public void testReadEntity() {
		EntityService es = new EntityService(api);
		EntityODR entity = (EntityODR) es.readEntity(SemanticTextFactory.entitypediaEntityIDToURL(15001L));
		logger.info(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
		assertEquals(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0),"Località");
	}

	@Test
	public void testReadEntities() {
		EntityService es = new EntityService(api);
		List<String> entitieURLs = new ArrayList();
		entitieURLs.add(PALAZZETTO_URL);
		entitieURLs.add(SemanticTextFactory.entitypediaEntityIDToURL(RAVAZZONE_ID));
		List<IEntity> entities =  es.readEntities(entitieURLs);
		assertEquals(entities.get(0).getName().getStrings(Locale.ITALIAN).get(0),"PALAZZETTO DELLO SPORT");
		logger.info(entities.get(1).getEtype().getName().getStrings(Locale.ITALIAN).get(0));
		assertEquals(entities.get(1).getName().getStrings(Locale.ITALIAN).get(0),"Ravazzone");
	}

	@Test
	public void testReadZeroEntities() {
		EntityService es = new EntityService(api);
		assertEquals(es.readEntities(new ArrayList()).size(), 0);
	}        

	



	@Test
	public void testUpdateEntity() {
		EntityService es = new EntityService(api);
		EntityODR entity = (EntityODR)es.readEntity(64000L);
		List<Attribute> attrs=entity.getAttributes();
		List<Attribute> attrs1=new ArrayList<Attribute>();

		for (Attribute atr : attrs){
			if (atr.getName().get("en").equalsIgnoreCase("Name")){
				attrs1.add(atr);
			} 
			else 
				if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = es.createAttribute(atDef, 11.466f);
					Attribute a=attr.convertToAttribute();
					attrs1.add(atr);
				} else if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = es.createAttribute(atDef, 46.289f);
					Attribute a=attr.convertToAttribute();
					attrs1.add(atr);

				}
				else if (atr.getName().get("en").equalsIgnoreCase("Class")){
					ConceptODR concept = new ConceptODR();
					concept = concept.readConcept(GYMNASIUM_CONCEPT_ID);
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = es.createAttribute(atDef, concept);
					Attribute a=attr.convertToAttribute();

					attrs1.add(a);
				} 
		}
		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);
		EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		Long id = es.createEntity(ent);

		IEntity newEntity=  es.readEntity(id);
		EntityODR newEntityODR = (EntityODR) newEntity;
		List<Attribute>newAttrs = newEntityODR.getAttributes();
		//--------Entity Update Test start

		assertEquals(4, newEntity.getStructureAttributes().size());





		AttributeDef openHourAD = new AttributeDef(ATTR_TYPE_OPENING_HOUR);
		AttributeDef closeHourAD = new AttributeDef(ATTR_TYPE_CLOSING_HOUR);

		HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef, Object>();
		attrMap.put(openHourAD, "8:00");
		attrMap.put(closeHourAD, "8.00"+System.currentTimeMillis());
		IAttributeDef attrDef = new AttributeDef(66L);

		AttributeODR attr = es.createAttribute(attrDef, attrMap);

		List<IAttribute> attributes = newEntity.getStructureAttributes();
		attributes.add(attr);
		newEntity.setStructureAttributes(attributes);

		es.updateEntity(newEntity);


		IEntity updatedEntity=  es.readEntity(id);
		assertEquals(5, updatedEntity.getStructureAttributes().size());
		//--------Entity Update Test end---------
		//--------Value Update Test start--------
		ValueODR val = new ValueODR();
		Float testNewValue = 0.0f;
		val.setValue(testNewValue);

		for (Attribute atr : newAttrs){

			if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				AttributeODR attrODR = new AttributeODR (api, atr);
				//				ValueODR val = (ValueODR) attrODR.getValues().get(0);
				//				val.setValue(value);
				es.updateAttributeValue(newEntityODR, attrODR, val);
			}
		}

		EntityODR entityUpdValue = (EntityODR)es.readEntity(id);

		List<Attribute> updAttributes =entityUpdValue.getAttributes(); 

		for (Attribute atr : updAttributes){
			if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				assertEquals(testNewValue, atr.getValues().get(0).getValue());
			}
		}

		es.deleteEntity(id);

	}


	@Test
	public void testCreateAttributeEntity() {
		EntityService es = new EntityService(api);
		EntityTypeService ets = new EntityTypeService();
		EntityType etype = ets.getEntityType(12L);
		List<IAttributeDef> attrDefList = etype.getAttributeDefs();
		List<Attribute> attrs = new ArrayList<Attribute>();

		for (IAttributeDef atd : attrDefList) {
			//			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
			//				logger.info(atd.getName());
			//				logger.info(atd.getGUID());
			//				logger.info(atd.getDataType());
			//				if (atd.getDataType().equals("oe:structure")){
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
				AttributeODR attr = es.createAttribute(atd, "My test name");
				Attribute a = attr.convertToAttribute();
				attrs.add(a);
			}

			if (atd.getName().getString(Locale.ENGLISH).equals("Class")) {
				//  logger.info(atd.getName());
				AttributeODR attr = es.createAttribute(atd, 123L);
				Attribute a = attr.convertToAttribute();
				attrs.add(a);
			}

			if (atd.getName().getString(Locale.ENGLISH).equals("Latitude")) {
				//       logger.info(atd.getName());
				AttributeODR attr = es.createAttribute(atd, 12.123F);
				Attribute a = attr.convertToAttribute();
				attrs.add(a);
			}
			if (atd.getName().getString(Locale.ENGLISH).equals("Longitude")) {
				//     logger.info(atd.getName());
				AttributeODR attr = es.createAttribute(atd, 56.567F);
				Attribute a = attr.convertToAttribute();
				attrs.add(a);
			}
			if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")) {
				//     logger.info(atd.getName());
				//      logger.info(atd.getURL());

				AttributeDef openHourAD = new AttributeDef(ATTR_TYPE_OPENING_HOUR);
				AttributeDef closeHourAD = new AttributeDef(ATTR_TYPE_CLOSING_HOUR);

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
		assertTrue(id>0);
		es.deleteEntity(id);
	}
	@Rule
	public ExpectedException thrown= ExpectedException.none(); 

	@Test
	public void testEmptyExportToJsonLd(){
		EntityService es = new EntityService(api);
		thrown.expect(DisiClientException.class);
		es.exportToJsonLd(new ArrayList(), new PrintWriter(System.out));
	}

}

