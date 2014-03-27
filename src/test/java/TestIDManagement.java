import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
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
		EntityODR entity1 = (EntityODR)enServ.readEntity(62841L);
	//	EntityODR entity2 = (EntityODR)enServ.readEntity(15008L);
	//	EntityODR entity3 = (EntityODR)enServ.readEntity(15009L);

		entity1.getEntityAttributes();
	//	entity2.getEntityAttributes();
	//	entity3.getEntityAttributes();
		
		entity1.getEtype();
	//	entity2.getEtype();
	//	entity3.getEtype();
		
		//entity1.getNames();
		
		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(entity1);
	//	entities.add(entity2);
	//	entities.add(entity3);

		List<IDResult> results=  idServ.assignID(entities);
		for (IDResult res: results){
			System.out.println(res.getResult());
		}
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

}