package eu.trentorise.opendatarise.semantics.test.services;

import static org.junit.Assert.*;

import eu.trentorise.opendatarise.semantics.services.*;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import org.junit.Test;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Mar 2014
 * 
 */
public class TestIDManagementService {

	private String entityToString(Entity e){
		String str = "id:"+e.getId()+
				", gID:"+e.getGlobalId()+
				", names:"+e.getNames()+
				", attributes:"+attributesToString(e.getAttributes());
		return str;
	}

	private String attributesToString(List<Attribute> attributes){
		String str = "[";
		for(Attribute attr:attributes){
			str+=attributeToString(attr)+"\n";
		}
		return str+"]";
	}

	private String attributeToString(Attribute attr) {
		String str = "attr concept_id:"+attr.getConceptId()+
				", datatype:"+attr.getDataType()+" values[";
		for(Value v:attr.getValues()){
			str+=v.getValue()+", ";
		}
		return str+"]";
	}

	//@Test 
	public void idServiceEntityNew(){

		IdentityService idServ = new IdentityService();
		EntityService enServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();
		for (Attribute atr : attrs){
				if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")){
					System.out.println(atr.getName());
					Attribute a = createAttributeEntity("50f6e6f516f88f6cc81a42fc");
					attrs1.add(a);
				}
				
		}
		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);

		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(ent);

		List<IIDResult> results=  idServ.assignGUID(entities);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			System.out.println("result "+res.getAssignmentResult());
			System.out.println("Global id: "+res.getGUID());
			System.out.println("Local id: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
		}
		

	}


    /**
     * Don't want errors on empty array
     */
    @Test
    public void testIdManagementEmptyArray(){
        IdentityService idServ= new IdentityService();
        List res = idServ.assignURL(new ArrayList(),3);
        assertTrue(res.isEmpty());
    }


	@Test
	public void testIdManagementReuse(){
		EntityService enServ =new EntityService(WebServiceURLs.getClientProtocol());
		IdentityService idServ= new IdentityService();
		String name = "PALAZZETTO DELLO SPORT";

		
		
		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs=entity.getAttributes();
		List<Attribute> attrs1=new ArrayList<Attribute>();

		for (Attribute atr : attrs){
			if (atr.getName().get("en").equalsIgnoreCase("Name")){
				Attribute a =createAttributeNameEntity(name);
				attrs1.add(a);
			} else 
				if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
					attrs1.add(atr);
				} else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
					EntityService es = new EntityService(getClientProtocol());
					attrs1.add(atr);
				}
				else if (atr.getName().get("en").equalsIgnoreCase("Class")){
					attrs1.add(atr);
				}
		}

		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		
		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(ent);
		
		List<IIDResult> results=  idServ.assignGUID(entities);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			System.out.println("result "+res.getAssignmentResult());
			System.out.println("Global ID: "+res.getGUID());
			System.out.println("Local ID: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.REUSE, res.getAssignmentResult());

		}
		
	}

	//@Test 
	public void idServiceEntityMissing(){

		IdentityService idServ = new IdentityService();
		EntityService enServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();

		for (Attribute atr : attrs){

				
			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
				attrs1.add(atr);
			}
			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				EntityService es = new EntityService(getClientProtocol());
				attrs1.add(atr);
			} 
		}

		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);

		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(ent);

		List<IIDResult> results=  idServ.assignGUID(entities);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			System.out.println("result "+res.getAssignmentResult());
			System.out.println("Global id: "+res.getGUID());
			System.out.println("Local id: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.MISSING, res.getAssignmentResult());
		}
	}

	
	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}

	public Attribute createAttributeNameEntity(String value){
		EntityService es = new EntityService(getClientProtocol());
		EntityTypeService ets = new EntityTypeService();
		EntityType etype = ets.getEntityType(12L);
		
		List<IAttributeDef>attrDefList=etype.getAttributeDefs();
		List<Attribute> attrs = new ArrayList<Attribute>();
		
		Attribute a = null;
		for (IAttributeDef atd: attrDefList){
			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
				System.out.println(atd.getName());
				AttributeODR attr = es.createNameAttributeODR(atd,value);
				a=attr.convertToAttribute();
				return a;
			}
		}
		return a;
	}

	public Attribute createAttributeEntity(Object value){
		EntityService es = new EntityService(getClientProtocol());
		EntityTypeService ets = new EntityTypeService();
		EntityType etype = ets.getEntityType(12L);
		
		List<IAttributeDef>attrDefList=etype.getAttributeDefs();
		List<Attribute> attrs = new ArrayList<Attribute>();
		
		Attribute a = null;
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

//			if (atd.getName().getString(Locale.ENGLISH).equals("Name")){
//				System.out.println(atd.getName());
//				AttributeODR attr = es.createNameAttribute(atd,(Name)value);
//				a=attr.convertToAttribute();
//				return a;
//			}
			//
			//			if (atd.getName().getString(Locale.ENGLISH).equals("Class")){
			//				System.out.println(atd.getName());
			//				AttributeODR attr = es.createAttribute(atd,clazz);
			//				Attribute a=attr.convertToAttribute();
			//				attrs.add(a);
			//			}

						if (atd.getName().getString(Locale.ENGLISH).equals("Foursquare ID")){
							AttributeODR attr = es.createAttribute(atd, (String)value);
							 a = attr.convertToAttribute();
							attrs.add(a);
						}

			//			if (atd.getName().getString(Locale.ENGLISH).equals("Latitude")){
			//				System.out.println(atd.getName());
			//				AttributeODR attr = es.createAttribute(atd,latitude);
			//				Attribute a=attr.convertToAttribute();
			//				attrs.add(a);
			//			}
			//			if (atd.getName().getString(Locale.ENGLISH).equals("Longitude")){
			//				System.out.println(atd.getName());
			//				AttributeODR attr = es.createAttribute(atd,longitude);
			//				 a=attr.convertToAttribute();
			//			}

			//						if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")){
			//							System.out.println(atd.getName());
			//							AttributeDef openHourAtDef = new AttributeDef(31L);
			//							AttributeDef closeHourAtDef = new AttributeDef(30L);
			//							
			//							HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef,Object>();
			//							attrMap.put(openHourAtDef, openTime);
			//							attrMap.put(closeHourAtDef, closeTime);
			//							
			//							AttributeODR attr = es.createAttribute(atd,attrMap);
			//							Attribute a=attr.convertToAttribute();
			//							attrs.add(a);
			//						}
		}
		//		EntityODR e = new EntityODR();
		//		e.setEntityBaseId(1L);
		//		e.setTypeId(12L);
		//		e.setAttributes(attrs);

		return a;

		//		long id = es.createEntity(e);
		//		System.out.println("Entity id:"+id);
	}

}
