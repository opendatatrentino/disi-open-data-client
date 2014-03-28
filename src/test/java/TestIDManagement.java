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
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.IdentityService;


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
	//	EntityODR entity2 = (EntityODR)enServ.readEntity(15008L);
	//	EntityODR entity3 = (EntityODR)enServ.readEntity(15009L);

	//	entity1.getEntityAttributes();
	//	entity2.getEntityAttributes();
	//	entity3.getEntityAttributes();
		
	//	entity1.getEtype();
	//	entity2.getEtype();
	//	entity3.getEtype();
		
		//entity1.getNames();
		
		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(entity1);
                System.out.println("Will try to asign IDs to:");
                for(IEntity entityInList: entities){
                    System.out.println(entityInList);
                }
	//	entities.add(entity2);
	//	entities.add(entity3);

                System.out.println("The result is:");
		List<IDResult> results=  idServ.assignID(entities);
		for (IDResult res: results){
			System.out.println("result "+res.getResult());
                        System.out.println("new sweb id "+res.getSwebID());
                        System.out.println("for entity: "+res.getEntity());
		}
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
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

}
