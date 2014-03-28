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

//	@Test
	public void testGetEntityName(){

		EntityService entServ = new EntityService(getClientProtocol());
		EntityODR entity = (EntityODR) entServ.readEntity(15007L);
		Name entityName = new Name();
		List<IAttribute> attrs= entity.getEntityAttributes();
		
		System.out.println(entity.toString());
		
//		for (IAttribute attr: attrs){
//			System.out.print(attr.getAttributeDefinition().getName(Locale.ENGLISH));
//			System.out.println( " : "+attr.getFirstValue().getValue().toString());
//			if(attr.getAttributeDefinition().getName(Locale.ENGLISH).equals("Name"))
//			{
//				entityName = (Name) attr.getFirstValue().getValue();
//			}
//
//		}
//		//	Name n = (Name) entServ.readEntity(entityName.getId());
//		AttributeClient attrCl = new AttributeClient(getClientProtocol());
//
//		List<Attribute> attributes = attrCl.readAttributes(entityName.getId(), null, null);
//
//		entityName.setAttributes(attributes);
//	//	entityName.setNames(entityName.getAttributes().get(0));
//		entityName.setNames((Map<String, List<String>>) entityName.getAttributes().get(0).getValues().get(0).getValue());
//		System.out.println(entityName.getNames());
//		System.out.println(entityName.getAttributes().get(0).getValues().get(0));

		//System.out.println("ENTITY NAME:   "+entityName.getNames().toString());
		//	Map<String, List<String>> nameMap = names.get(0).getNames();
		//	System.out.println("map entries: " + nameMap.toString() );
	}


	/** The method returns client protocol 
	 * @return returns an instance of ClientProtocol that contains information where to connect(Url adress and port) and locale
	 */

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

}
