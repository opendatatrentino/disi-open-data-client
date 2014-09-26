package eu.trentorise.opendata.disiclient.test.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;

public class TestStructure {

    @Test
    public void testGetAttributeByURL() {

        EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
        Long entityID = 64008L;
        StructureODR structure = es.readStructure(entityID);
        IAttribute attribute = structure.getAttribute("http://opendata.disi.unitn.it:8080/odr/attributedefinitions/177");
//		String url = structure.getEtypeURL();
//		System.out.println(url);

        assertNotNull(attribute);
    }

//	@Test 
//	public void testGetStructureEtypeL(){
//		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
//		Long entityID= 64008L;
//		StructureODR structure =es.readStructure(entityID);
//		IEntityType etype= structure.getEtype();
//		assertEquals("Facility", etype.getName().getString(Locale.ENGLISH));
//	}
}
