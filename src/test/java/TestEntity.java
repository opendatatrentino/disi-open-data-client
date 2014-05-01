import static org.junit.Assert.assertEquals;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendatarise.semantics.model.facade.ImpiantoDiRisalitaFacade;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.entity.ValueODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 28 Mar 2014
 * 
 */
public class TestEntity {

	public static final Long ATTR_TYPE_LATITUDE = 69L;
	public static final Long ATTR_TYPE_LONGITUDE = 68L;
	public static final Long ATTR_TYPE_OPENING_HOUR = 30L;
	public static final Long ATTR_TYPE_CLOSING_HOUR = 31L;
	public static final Long ATTR_TYPE_OPENING_HOURS = 66L;
	public static final Long CLASS = 58L;
	public static final Long NAME = 55L;

	//
	//	@Test
	//	public void testCType(){
	//
	//		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
	//		List<ComplexType> cType = ctc.readComplexTypes(1L, 111001L, null, null);
	//		System.out.println("Name ID:"+cType.size());
	//
	//
	//	}

	//@Test
	public void testGetEntityName(){

		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		assertEquals("Comano",entity.getName(Locale.ITALIAN));
	}

	//@Test
	public void test(){
		InstanceClient  ic = new InstanceClient(getClientProtocol());
		Name nameStructure = new Name();
		List<Attribute> nameAttributes = new ArrayList<Attribute>();
		nameStructure.setEntityBaseId(1L);
		Attribute nameAttribute = new Attribute();
		nameAttribute.setDefinitionId(55L);
		nameAttributes.add(nameAttribute);
		List<Value>nameValues=new ArrayList<Value>();
		nameValues.add(new Value("Your name", 1L));
		nameAttribute.setValues(nameValues);
		nameStructure.setAttributes(nameAttributes);
		long id  =ic.create(nameStructure);
		System.out.println("Name ID:"+id);

		Entity entity = new Entity();
		entity.setEntityBaseId(1L);
		entity.setTypeId(12L);
		List<Attribute> entityNameattributes = new ArrayList<Attribute>();
		Attribute entityNameAttribute = new Attribute();
		entityNameAttribute.setDefinitionId(162L);
		entityNameattributes.add(nameAttribute);
		Attribute classAttribute = new Attribute();
		classAttribute.setDefinitionId(126L);
		List<Value>entityNameValues=new ArrayList<Value>();
		entityNameValues.add(new Value(id)); // here is your link to the name structure, if you want you can put the id of the name instance (if you created it before) but make sure the data type is COMPLEX_TYPE
		entityNameAttribute.setValues(entityNameValues);
		List<Value>entityClassValues=new ArrayList<Value>();
		entityClassValues.add(new Value(42806L)); // here is the link to the class of the entity you have to put the concept instance or the id of the concept if you have it
		classAttribute.setValues(entityClassValues);
		EntityService entServ = new EntityService(getClientProtocol());
		id  =ic.create(entity);
		System.out.println("INSTANCE ID: "+id);
	}




	// @Test broken...
	public void testCreationImpianti(){
		ImpiantoDiRisalitaFacade idrf = new ImpiantoDiRisalitaFacade(getClientProtocol());
		long id =idrf.createEntity("Ivan", "Cabinovia", 12.356f, 20.9087f, "8:00", "17:00");
		System.out.println("ID of entity: "+ id);
	}

	//@Test 
	public void testCreationEntity(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		EntityODR entity = new EntityODR();
		entity.setEntityBaseId(1L);
		EntityTypeService ets = new EntityTypeService();
		//set facility etype - 12L
		EntityType etype = ets.getEntityType(12L);
		entity.setEtype(etype);
		entity.setName(Locale.ENGLISH, "My name");
		//entity.setDescription(language, description);
		//entity.setClassConceptId(CLASS);
		List<AttributeODR> attributes = new ArrayList<AttributeODR>();
		//Attribute
		AttributeODR latitudeAttr = new AttributeODR(api);
		AttributeODR longitudeAttr = new AttributeODR(api);
		AttributeODR structureOpeningHour = new AttributeODR(api);
		AttributeDef lattitudeAtDef = new AttributeDef(ATTR_TYPE_LATITUDE);
		AttributeDef longitudeAtDef = new AttributeDef(ATTR_TYPE_LONGITUDE);
		AttributeDef structureOpeningHourAtDef = new AttributeDef(ATTR_TYPE_OPENING_HOURS);
		AttributeDef openHourAtDef = new AttributeDef(ATTR_TYPE_OPENING_HOUR);
		AttributeDef closeHourAtDef = new AttributeDef(ATTR_TYPE_CLOSING_HOUR);
		//TODO class is strictly mandatory, where to take this information?
		AttributeDef classConceptIdAtDef = new AttributeDef(CLASS);
		latitudeAttr.setAttributeDefinition(lattitudeAtDef);
		longitudeAttr.setAttributeDefinition(longitudeAtDef);
		structureOpeningHour.setAttributeDefinition(structureOpeningHourAtDef);
		List<ValueODR> latValues = new ArrayList<ValueODR>();
		List<ValueODR> lonValues = new ArrayList<ValueODR>();
		List<ValueODR> OpenHourValues = new ArrayList<ValueODR>();

		ValueODR latitudeVal = new ValueODR();
		ValueODR longitudeVal = new ValueODR();
		//		ValueODR structureOpeningHourVal = new ValueODR();

		latValues.add(latitudeVal);
		lonValues.add(longitudeVal);
	}

	//@Test
	public void testCreateAttributeEntity(){
		EntityODR entity = new EntityODR();
		entity.setEntityBaseId(1L);
		EntityTypeService ets = new EntityTypeService();
		EntityService es = new EntityService(getClientProtocol());
		//set facility etype - 12L
		EntityType etype = ets.getEntityType(12L);
		entity.setEtype(etype);
		AttributeDef lattitudeAtDef = new AttributeDef(ATTR_TYPE_LATITUDE);
		AttributeDef longitudeAtDef = new AttributeDef(ATTR_TYPE_LONGITUDE);
		AttributeDef classConceptIdAtDef = new AttributeDef(CLASS);
		AttributeDef nameAtDef = new AttributeDef(NAME);
		List<IAttribute> ats = new ArrayList<IAttribute>();

		AttributeODR atName = (AttributeODR) es.createAttribute(nameAtDef, "First Name");
		AttributeODR atClass = (AttributeODR) es.createAttribute(classConceptIdAtDef, 123L);
		AttributeODR atLat = (AttributeODR) es.createAttribute(lattitudeAtDef,  12.356f);
		AttributeODR atLon = (AttributeODR) es.createAttribute(longitudeAtDef, 20.9087f);

		ats.add(atName);
		ats.add(atClass);
		ats.add(atLat);
		ats.add(atLon);

		entity.setStructureAttributes(ats);

		long id = es.createEntity(entity);
		System.out.println(id);

	}

	//@Test
	public Attribute addAttribute(Name name, long id){
		Attribute atr = new Attribute();
		atr.setDefinitionId(id);
		Value value = new Value();
		value.setValue(name);
		value.setAttributeId(id);

		List<Value> vals = new ArrayList<Value>();
		vals.add(value);
		atr.setValues(vals);
		return atr;
	}

	//@Test
	public void testSetEntityName(){

		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		entity.setName(Locale.CANADA, "Coman");
		entServ.updateEntity(entity);
		EntityODR entityUpd = (EntityODR) entServ.readEntity(15007L);

		System.out.println(entityUpd.getName(Locale.CANADA));
		//assertEquals("Comano",entity.getName(Locale.ITALIAN));
	}


	//	@Test
	public void testEntityAttributeCreate(){

		//		Entity en = new Entity();
		//		AttributeODR attr = new AttributeODR(getClientProtocol());
		//		AttributeDef adLat = new AttributeDef(latitudeAtDef);
		//		//AttributeDef adLon = new AttributeDef(longitudeAtDef);
		//		attr.setAttributeDefinition(adLat);
		//		ValueODR val = new ValueODR();
		//		attr.addValue(value);
		//		
	}

	private IProtocolClient getClientProtocol(){
		return WebServiceURLs.getClientProtocol();
	}

	//	private <T> void createAttribute(Long attributeTypeId, Object value, Class<T> clazz) {
	//		IAttribute attribute = new AttributeODR(api);
	//		attribute.setAttributeDefinition(new AttributeDef(attributeTypeId));
	//		IValue attributeValue = new ValueODR();
	//		attributeValue.setValue(clazz.cast(value));
	//		attribute.addValue(attributeValue);
	//		//adding attribute to the entity
	//		this.getEntityAttributes().add(attribute);
	//	}


}
