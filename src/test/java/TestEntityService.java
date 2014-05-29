import static org.junit.Assert.assertEquals;
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
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Mar 2014
 * 
 */
public class TestEntityService {

	private IProtocolClient api;
	private Long entityID;
	static final Long ATTR_TYPE_OPENING_HOUR = 31L;
	public static final Long ATTR_TYPE_CLOSING_HOUR = 30L;

	@Before
	public void getClientProtocol(){
		this.api = WebServiceURLs.getClientProtocol();
	}

	//@Test
	public void testEntityRead(){
		EntityService es= new EntityService(api);
		EntityODR entity = (EntityODR) es.readEntity(15001L);
		//System.out.println(entity.getEtype().getName().getStrings(Locale.ITALIAN).get(0));
		//	assertEquals(entity.getEtype().getName(Locale.ENGLISH),"Location");
	}

	//@Test 
	public void testCreateDeleteEntity(){

		//initialising variables
		EntityService es= new EntityService(api);
		InstanceClient instanceClient = new InstanceClient(api);
		AttributeClient attrClient = new AttributeClient(api);
		ComplexTypeClient ctypecl = new ComplexTypeClient(api);

		Instance inst = instanceClient.readInstance(15007L, null);

		EntityODR entity = new EntityODR();
		List<Attribute> attributes = new ArrayList<Attribute>();
		ComplexType cType = ctypecl.readComplexType(inst.getTypeId(), null);
		EntityType etype = new EntityType(cType);
		//List<Name> names = new ArrayList<Name>();

		attributes = attrClient.readAttributes(15007L, null,null);
		//instantiation of variables

		attributes = attrClient.readAttributes(15007L, null,null);
		//System.out.println("Etype id: "+inst.getTypeId());
		//assigning variables
		entity.setAttributes(attributes);
		entity.setEtype(etype);
		entity.setEntityBaseId(101L);
		System.out.println("entity: "+entity.toString());
		//es.createEntity(entity);

		EbClient ebc = new EbClient(api); 
		EntityBase eb = ebc.readEntityBase(101L, null);
		int instanceNum = eb.getInstancesNumber();

		long id=es.createEntity(entity);
		inst = instanceClient.readInstance(id, null);
		EntityBase ebafter = ebc.readEntityBase(101L, null);
		int instanceNumAfter = ebafter.getInstancesNumber();
		assertEquals(instanceNum+1, instanceNumAfter);

		es.deleteEntity(id);
		EntityBase ebafterDel = ebc.readEntityBase(101L, null);
		int instanceNumAfterDel = ebafterDel.getInstancesNumber();
		assertEquals(instanceNumAfterDel, instanceNumAfterDel);

	}

	//@Test 
	public void testCreateEntityODR(){
		String name = "Test name";
		InstanceClient  ic = new InstanceClient(api);
		Name nameStructure = new Name();
		List<Attribute> nameAttributes = new ArrayList<Attribute>();
		nameStructure.setEntityBaseId(1L);
		Attribute nameAttribute = new Attribute();
		nameAttribute.setDefinitionId(55L);
		nameAttributes.add(nameAttribute);
		List<Value>nameValues=new ArrayList<Value>();
		nameValues.add(new Value(name, 1L));
		//BE CAREFULL WITH VOCABULARY
		nameAttribute.setValues(nameValues);
		//this.getAttributes().add(nameAttribute);
		nameStructure.setAttributes(nameAttributes);
		long id  =ic.create(nameStructure);
		System.out.println("Name ID:"+id);
	}


	//@Test
	public void testUpdateEntity(){
		InstanceClient instanceClient = new InstanceClient(api);
		//Instance inst1 = instanceClient.readInstance(15007L, null);
		Instance instPreModif = instanceClient.readInstance(189701L, null);
		Entity ent = (Entity)instPreModif;
		EntityODR ePreMod = new EntityODR(api,ent);
		List<IAttribute> attrs = ePreMod.getEntityAttributes();
		System.out.println(attrs.size());
		EntityService es = new EntityService(api);
		//Instance inst2 = instanceClient.readInstance(189701L, null);

		List<IAttribute> newListAttrs = new ArrayList<IAttribute>();
		newListAttrs.add(attrs.get(0));
		newListAttrs.add(attrs.get(1));

		EntityODR ie = new EntityODR(api, ent);
		ie.setEntityAttributes(newListAttrs);
		es.updateEntity(ie);
		Instance instPostModif = instanceClient.readInstance(189701L, null);
		Entity entPost = (Entity) instPostModif;

		EntityODR ePostMod = new EntityODR(api,entPost);
		List<IAttribute> attrsPost = ePostMod.getEntityAttributes();
		System.out.println(attrsPost.size());
	}

	@Test 
	public void testCreateAttributeEntity(){
		EntityService es = new EntityService(api);
		EntityTypeService ets = new EntityTypeService();
		EntityType etype = ets.getEntityType(12L);
		List<IAttributeDef>attrDefList=etype.getAttributeDefs();
		List<Attribute> attrs = new ArrayList<Attribute>();

		for (IAttributeDef atd: attrDefList){
			//			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
			//				System.out.println(atd.getName());
			//				System.out.println(atd.getGUID());
			//				System.out.println(atd.getDataType());
			//				if (atd.getDataType().equals("oe:structure")){
			//					System.out.println(atd.getRangeEType().getURL());
			//					EntityType etpe =	ets.getEntityType(atd.getRangeEType().getURL());
			//					List<IAttributeDef>atsd = etpe.getAttributeDefs();
			//					for (IAttributeDef a:atsd){
			//						System.out.println(a.getGUID());
			//					}
			//
			//				}

			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
				System.out.println(atd.getName());
				AttributeODR attr = es.createAttribute(atd,"My test name");
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
			}

			if (atd.getName().getString(Locale.ENGLISH).equals("Class")){
				System.out.println(atd.getName());
				AttributeODR attr = es.createAttribute(atd,123L);
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
			}

			if (atd.getName().getString(Locale.ENGLISH).equals("Latitude")){
				System.out.println(atd.getName());
				AttributeODR attr = es.createAttribute(atd,12.123F);
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
			}
			if (atd.getName().getString(Locale.ENGLISH).equals("Longitude")){
				System.out.println(atd.getName());
				AttributeODR attr = es.createAttribute(atd,56.567F);
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
			}
			if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")){
				System.out.println(atd.getName());
				AttributeDef openHourAtDef = new AttributeDef(ATTR_TYPE_OPENING_HOUR);
				AttributeDef closeHourAtDef = new AttributeDef(ATTR_TYPE_CLOSING_HOUR);
				
				HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef,Object>();
				attrMap.put(openHourAtDef, "8:00");
				attrMap.put(closeHourAtDef, "18:00");
				
				AttributeODR attr = es.createAttribute(atd,attrMap);
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
			}
			
			

		}
		EntityODR e = new EntityODR();
		e.setEntityBaseId(1L);
		e.setTypeId(18L);
		e.setAttributes(attrs);
		long id = es.createEntity(e);
		System.out.println("Entity id:"+id);
	}
}

