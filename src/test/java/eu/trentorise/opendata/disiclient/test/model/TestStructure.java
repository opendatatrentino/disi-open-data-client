package eu.trentorise.opendata.disiclient.test.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.junit.After;
import org.junit.Before;

public class TestStructure {

    private IEkb ekb;
    
    @Before
    public void beforeMethod() {
        ekb = ConfigLoader.init();
    }
    
    @After
    public void after(){
        ekb = null;
    }

    @Test
    public void testGetAttributeByURL() {


        Long entityID = 64008L;
        StructureODR structure = ((EntityService) ekb.getEntityService()).readStructure(entityID);
        IAttribute attribute = structure.getAttribute("http://opendata.disi.unitn.it:8080/odr/attributedefinitions/177");
//		String url = structure.getEtypeURL();
//		System.out.println(url);

        assertNotNull(attribute);
    }

//	@Test 
//	public void testGetStructureEtypeL(){
//		EntityService es = new EntityService(SwebConfiguration.getClientProtocol());
//		Long entityID= 64008L;
//		StructureODR structure =es.readStructure(entityID);
//		IEntityType etype= structure.getEtype();
//		assertEquals("Facility", etype.getName().getString(Locale.ENGLISH));
//	}
}
