import static org.junit.Assert.assertEquals;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.EbClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.EntityBase;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.EntityService;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Mar 2014
 * 
 */
public class TestEntityService {

	private IProtocolClient api;
	private Long entityID;

	@Before
	public void getClientProtocol(){
		this.api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
	}

	//@Test
	public void testEntityRead(){
		EntityService es= new EntityService(api);
		EntityODR entity = (EntityODR) es.readEntity(15001L);
		System.out.println(entity.getEtype().getName(Locale.ITALIAN));
	//	assertEquals(entity.getEtype().getName(Locale.ENGLISH),"Location");
	}

	@Test 
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
	public void testUpdateEntity(){
		InstanceClient instanceClient = new InstanceClient(api);
		//Instance inst1 = instanceClient.readInstance(15007L, null);
		Instance instPreModif = instanceClient.readInstance(189701L, null);
		EntityODR ePreMod = new EntityODR(api,instPreModif);
		List<IAttribute> attrs = ePreMod.getEntityAttributes();
		System.out.println(attrs.size());
		EntityService es = new EntityService(api);
		//Instance inst2 = instanceClient.readInstance(189701L, null);

		List<IAttribute> newListAttrs = new ArrayList<IAttribute>();
		newListAttrs.add(attrs.get(0));
		newListAttrs.add(attrs.get(1));
		
		EntityODR ie = new EntityODR(api, instPreModif);
		ie.setEntityAttributes(newListAttrs);
		es.updateEntity(ie);
		Instance instPostModif = instanceClient.readInstance(189701L, null);
		EntityODR ePostMod = new EntityODR(api,instPostModif);
		List<IAttribute> attrsPost = ePostMod.getEntityAttributes();
		System.out.println(attrsPost.size());


	}

}

