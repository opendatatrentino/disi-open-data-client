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

import eu.trentorise.opendatarise.semantics.model.facade.ImpiantoDiRisalitaFacade;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 28 Mar 2014
 * 
 */
public class TestEntity {


	private final long latitudeAtDef=69;
	private final long longitudeAtDef=68;

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


//	@Test
	public void testCreationImpianti(){
		ImpiantoDiRisalitaFacade idrf = new ImpiantoDiRisalitaFacade(getClientProtocol());
		long id =idrf.createEntity("Ivan", "Cabinovia", 12.356f, 20.9087f, "8:00", "17:00");
		System.out.println("ID of entity: "+ id);

	}

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

		Entity en = new Entity();
		AttributeODR attr = new AttributeODR(getClientProtocol());
		AttributeDef adLat = new AttributeDef(latitudeAtDef);
		//AttributeDef adLon = new AttributeDef(longitudeAtDef);
		attr.setAttributeDefinition(adLat);
		//		ValueODR val = new ValueODR();
		//		attr.addValue(value);
		//		
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
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
