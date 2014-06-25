package eu.trentorise.opendatarise.semantics.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.model.entity.Structure;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;


public class TestStructure {

	
	@Test 
	public void testGetAttributeByURL(){
		
		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
		Long entityID= 64008L;
		Structure structure =es.readStructure(entityID);
		IAttribute attribute = structure.getAttribute("http://opendata.disi.unitn.it:8080/odr/attributedefinitions/177");
//		String url = structure.getEtypeURL();
//		System.out.println(url);
	
		assertNotNull(attribute);
	}
	

//	@Test 
//	public void testGetStructureEtypeL(){
//		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
//		Long entityID= 64008L;
//		Structure structure =es.readStructure(entityID);
//		IEntityType etype= structure.getEtype();
//		assertEquals("Facility", etype.getName().getString(Locale.ENGLISH));
//	}
}
