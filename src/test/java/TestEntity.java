import static org.junit.Assert.*;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 28 Mar 2014
 * 
 */
public class TestEntity {

	//@Test
	public void testGetEntityName(){

		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		assertEquals("Comano",entity.getName(Locale.ITALIAN));
	}

	@Test
	public void testSetEntityName(){

		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		entity.setName(Locale.CANADA, "Coman");
		entServ.updateEntity(entity);
		EntityODR entityUpd = (EntityODR) entServ.readEntity(15007L);

		System.out.println(entityUpd.getName(Locale.CANADA));
		//assertEquals("Comano",entity.getName(Locale.ITALIAN));
	}


	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

}
