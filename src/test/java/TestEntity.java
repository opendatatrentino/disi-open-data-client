import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.ListModel;

import org.junit.Test;

import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 28 Mar 2014
 * 
 */
public class TestEntity {

	@Test
	public void testGetEntityName(){
		
		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		entity.getStructureAttributes();
		List<Name> names = entity.getNames();
		Map<String, List<String>> nameMap = names.get(0).getNames();
		System.out.println("map entries: " + nameMap.toString() );
	}
	

	/** The method returns client protocol 
	 * @return returns an instance of ClientProtocol that contains information where to connect(Url adress and port) and locale
	 */
	
	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}
	
}
