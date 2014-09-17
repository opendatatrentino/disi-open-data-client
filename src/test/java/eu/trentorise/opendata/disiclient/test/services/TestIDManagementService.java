package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_NAME_IT;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.IdentityService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Mar 2014
 * 
 */
public class TestIDManagementService {
	public static final long GYMNASIUM_CONCEPT_ID = 18565L;

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

	@Test 
	public void idServiceEntityNew(){

		IdentityService idServ = new IdentityService();
		EntityService enServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR)enServ.readEntity(PALAZZETTO_ID);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();
		for (Attribute atr : attrs){
			if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")){
				//	System.out.println(atr.getName());
				IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
				AttributeODR attr = enServ.createAttribute(atDef, "50f6e6f516488f6cc81a42fc");
				Attribute a=attr.convertToAttribute();
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

		List<IIDResult> results=  idServ.assignURL(entities, 3);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();

			System.out.println("result "+res.getAssignmentResult());
			System.out.println("Global id: "+res.getGUID());
			System.out.println("Local id: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
		}


	}


	/**
         * 
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
		String name = PALAZZETTO_NAME_IT;
		//String name = "my entity name";
		//		Search search = new Search(WebServiceURLs.getClientProtocol());
		//		List<Name> names = search.nameSearch(name);

		//		for (Name n: names ){
		//			System.out.println("Names:"+n);
		//		}


		EntityODR entity = (EntityODR)enServ.readEntity(PALAZZETTO_ID);
		List<Attribute> attrs=entity.getAttributes();
		List<Attribute> attrs1=new ArrayList<Attribute>();
		List<IAttribute> iattr=entity.getStructureAttributes();
//
//		for (IAttribute atr : iattr){
//
//			if (atr.getAttributeDefinition().getName().getString(Locale.ENGLISH).equalsIgnoreCase("Name")){
//				System.out.println(atr.getValues().get(0).getValue());
//				Attribute a =createAttributeNameEntity(name);
//				attrs1.add(a);
//			} 
//		}

		for (Attribute atr : attrs){
			if (atr.getName().get("en").equalsIgnoreCase("Name")){
				Attribute a =createAttributeNameEntity(name);
				attrs1.add(a);
			} 
			else 
//				if (atr.getName().get("en").equalsIgnoreCase("Description")){
//					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
//					//Value v = atr.getValues().get(0);
//				//	AttributeODR attr = enServ.createAttribute(atDef, "my description");
//					//Attribute a=attr.convertToAttribute();
//					attrs1.add(atr);
//				} 
				if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = enServ.createAttribute(atDef, 11.466894f);
					Attribute a=attr.convertToAttribute();
					attrs1.add(a);
				} 
				else if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = enServ.createAttribute(atDef, 46.289413f);
					Attribute a=attr.convertToAttribute();
					attrs1.add(a);
					//					
				}
				else if (atr.getName().get("en").equalsIgnoreCase("Class")){
					ConceptODR concept = new ConceptODR();
					concept = concept.readConcept(GYMNASIUM_CONCEPT_ID);
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr = enServ.createAttribute(atDef, concept);
					Attribute a=attr.convertToAttribute();
					attrs1.add(a);
				} 
			//				else
			//		    if (atr.getName().get("en").equalsIgnoreCase("Opening hours")) {
			//                System.out.println(atr.getName());
			//                AttributeDef openHourAD = new AttributeDef(31L);
			//                AttributeDef closeHourAD = new AttributeDef(30L);
			//
			//                HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef, Object>();
			//                attrMap.put(openHourAD, "8.00");
			//                attrMap.put(closeHourAD, "18.00");
			//                AttributeDef atDef = new AttributeDef(66L);
			//                
			//
			//                AttributeODR attr = enServ.createAttribute(atDef, attrMap);
			//                Attribute a = attr.convertToAttribute();
			//                attrs1.add(a);
			//   }

		}

		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(FACILITY_ID);
		en.setAttributes(attrs1);
		//en.setGlobalId(10002538L);
		EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		System.out.println("Name:" +ent.getName());
		System.out.println("Name:" +ent.getDescription());

		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(ent);

		List<IIDResult> results=  idServ.assignURL(entities, 3);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			System.out.println("result "+res.getAssignmentResult());
			System.out.println("Global ID: "+res.getGUID());
			System.out.println("Local ID: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.REUSE, res.getAssignmentResult());

		}
		//ent.setGlobalId(10002538L);
		Long id = enServ.createEntity(ent);


		System.out.println(id);

		//assertEquals(AssignmentResult.REUSE, results.get(0).getAssignmentResult());

	}
        
        @Test
        public void testRelationalAttribute(){
            EntityService enServ =new EntityService(WebServiceURLs.getClientProtocol());
            EntityTypeService etypeServ =new  EntityTypeService();
            IdentityService idServ= new IdentityService();
            
            
            
            final EntityODR enodr = new EntityODR();
                        
            IEntityType facility = etypeServ.readEntityType(FACILITY_URL);
            enodr.setEtype(facility); 
            enodr.setEntityBaseId(1L); // todo fixed ID !            
           
            IEntity palazzetto = enServ.readEntity(PALAZZETTO_URL);
            
            List<IAttribute> attrs = new ArrayList();
            attrs.add(enServ.createAttribute(facility.getAttrDef(facility.getNameAttrDef().getURL()),
                                    "test entity")); // so doesn't complain about missing name...
           EntityODR  palazzetto1 = (EntityODR) palazzetto;
          
           attrs.add(enServ.createAttribute(facility.getAttrDef(TestEntityService.ATTR_DEF_PART_OF_URL),palazzetto1  ));            
            
            enodr.setStructureAttributes(attrs);

            // todo this call fails because tries to serialize the whole palazzetto as EntityODR
            idServ.assignURL(new ArrayList(){{add(enodr);}}, 3);
        }

	@Test 
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
				attrs1.add(atr);
			} 
			//			else if (atr.getName().get("en").equalsIgnoreCase("Class")){
			//				attrs1.add(atr);
			//			}
			else if (atr.getName().get("en").equalsIgnoreCase("Class")){
				ConceptODR concept = new ConceptODR();
				concept = concept.readConcept(GYMNASIUM_CONCEPT_ID);
				IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
				AttributeODR attr = enServ.createAttribute(atDef, concept);
				Attribute a=attr.convertToAttribute();
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

		List<IIDResult> results=  idServ.assignURL(entities, 3);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			//	System.out.println("result "+res.getAssignmentResult());
			//	System.out.println("Global id: "+res.getGUID());
			//	System.out.println("Local id: "+entityODR.getLocalID());
			assertEquals(AssignmentResult.INVALID, res.getAssignmentResult());
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
			//				if (atd.getDataType().equals(DataTypes.STRUCTURE)){
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

			//						if (atd.getName().getString(Locale.ENGLISH).equals("Foursquare ID")){
			//							AttributeODR attr = es.createAttribute(atd, (String)value);
			//							 a = attr.convertToAttribute();
			//							attrs.add(a);
			//						}

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

			//									if (atd.getName().getString(Locale.ENGLISH).equals("Opening hours")){
			//										System.out.println(atd.getName());
			//										AttributeDef openHourAtDef = new AttributeDef(31L);
			//										AttributeDef closeHourAtDef = new AttributeDef(30L);
			//										
			//										HashMap<AttributeDef, Object> attrMap = new HashMap<AttributeDef,Object>();
			//										attrMap.put(openHourAtDef, openTime);
			//										attrMap.put(closeHourAtDef, closeTime);
			//										
			//										AttributeODR attr = es.createAttribute(atd,attrMap);
			//										Attribute a=attr.convertToAttribute();
			//										attrs.add(a);
			//								}
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
