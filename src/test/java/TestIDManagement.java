import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.facade.ImpiantoDiRisalitaFacade;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.IdentityService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import it.unitn.disi.sweb.webapi.model.eb.Value;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Mar 2014
 * 
 */
public class TestIDManagement {

	@Test
	public void testIdService(){
		IdentityService idServ = new IdentityService();
		EntityService enServ = new EntityService(getClientProtocol());
		EntityODR entity1 = (EntityODR)enServ.readEntity(64000L);
	//	EntityODR entity2 = (EntityODR)enServ.readEntity(117516L);

		List<Attribute> attrs = entity1.getAttributes();
		for (Attribute atr : attrs){
			System.out.println(atr.getName().get("en"));
			
			if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")){
				System.out.println(atr.getName());
				attrs.remove(atr);
				}
			
		}
		entity1.setAttributes(attrs);
		
		List<Attribute> attrs1 = entity1.getAttributes();
		for (Attribute atr : attrs1){
			System.out.println(atr.getName().get("en"));
			
			if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")){
				System.out.println(atr.getName());
				//attrs.remove(atr);
				}
			
		}
		entity1.setAttributes(attrs);		
		//EntityODR entity2 = (EntityODR)enServ.readEntity(64005L);

		//ImpiantoDiRisalitaFacade idrf = new ImpiantoDiRisalitaFacade(WebServiceURLs.getClientProtocol());
		//EntityODR entity3 =idrf.createEmptyEntity("Ivan", "Cabinovia", 12.356f, 20.9087f, "8:00", "17:00");
		//Long entity4 =idrf.createEntity("Ivan", "Cabinovia", 12.356f, 20.9087f, "8:00", "17:00");
		//System.out.println(entity4);

		//	entity1.getEntityAttributes();
		//	entity2.getEntityAttributes();
		//	entity3.getEntityAttributes();

		//	entity1.getEtype();
		//	entity2.getEtype();
		//	entity3.getEtype();

		//		EntityODR entity1 = (EntityODR)enServ.readEntity(64010L);
		//		EntityODR entity2 = (EntityODR)enServ.readEntity(64015L);
		//	EntityODR entity3 = (EntityODR)enServ.readEntity(15009L);

		//	entity1.getEntityAttributes();
		//	entity2.getEntityAttributes();
		//	entity3.getEntityAttributes();

		//	entity1.getEtype();
		//	entity2.getEtype();
		//	entity3.getEtype();

		//entity1.getNames();

		List<IEntity> entities = new ArrayList<IEntity>();
		//	entities.add(entity1);
		//	entities.add(entity2);
		entities.add(entity1);


		//System.out.println("Will try to asign IDs to:");
		//for(IEntity entityInList: entities){
		//	System.out.println(entityInList);
		//	}
		//	entities.add(entity2);
		//	entities.add(entity3);

		System.out.println("The result is:");
		//IProtocolClient clientApi = getClientProtocol();
		List<IIDResult> results=  idServ.assignGUID(entities);
		for (IIDResult res: results){
			EntityODR entityODR =  (EntityODR) res.getResultEntity();
			System.out.println("result "+res.getAssignmentResult());
			System.out.println("result "+res.getGUID());
			System.out.println("result "+entityODR.getLocalID());
			//System.out.println("new sweb id "+res.getSwebID());
			//System.out.println("for entity(webAPI): "+entityToString(res.getEntity()));
			System.out.println("for entity(ODR): "+entityODR);
		}
	}

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
	public void testIdManServiceDISIClient(){
		AttributeClient attrClient = new AttributeClient(getClientProtocol());
		InstanceClient instanceCl= new  InstanceClient(getClientProtocol());
		Entity entity1 = (Entity) instanceCl.readInstance(62841L, null);
		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes = attrClient.readAttributes(62841L, null,null);

		entity1.setAttributes(attributes);

		IDManagementClient idManCl = new IDManagementClient(getClientProtocol());
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(entity1);
		List<IDResult> results =idManCl.assignIdentifier(entities, 0);
		for (IDResult res: results){
			System.out.println(res.getResult());
			//System.out.println(res.);
		}
	}
	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}



}
