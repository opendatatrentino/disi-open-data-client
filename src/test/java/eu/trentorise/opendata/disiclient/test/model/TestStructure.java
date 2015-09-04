package eu.trentorise.opendata.disiclient.test.model;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.disiclient.test.services.TestEntityService;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.junit.After;
import static org.junit.Assert.assertEquals;
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

        StructureODR structure = ((EntityService) ekb.getEntityService()).readStructure(TestEntityService.KINDERGARDEN_CONTACT_ID);
        IAttribute attribute = structure.getAttribute(TestEntityService.ATTR_DEF_TELEPHONE_URL);
//		String url = structure.getEtypeURL();
//		System.out.println(url);
        assertEquals(TestEntityService.ATTR_DEF_TELEPHONE_URL, attribute.getAttrDefUrl());
        Checker.of(ekb).checkStructure(structure);
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
