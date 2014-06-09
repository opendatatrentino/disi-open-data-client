package eu.trentorise.opendatarise.semantics.test.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
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
		assertNotNull(attribute);
	}
}
