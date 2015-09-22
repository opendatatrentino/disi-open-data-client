package eu.trentorise.opendata.disiclient.test.model;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.disiclient.test.services.DisiTest;
import eu.trentorise.opendata.disiclient.test.services.EntityServiceIT;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.model.entity.AStruct;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Structure;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

public class TestStructure extends DisiTest {

    IEntityService es;
    EntityService disiEs;
    
    @Before
    public void beforeMethod() {
	this.es = ekb.getEntityService();
	this.disiEs = (EntityService) ekb.getEntityService();
    }
    
    @After
    public void after(){
	es = null;
	disiEs = null;
    }

    @Test
    public void testGetAttributeByURL() {

        AStruct structure = es.readStruct(um.entityIdToUrl(DisiTest.KINDERGARDEN_CONTACT_ID));
        Attr attr = structure.attr(EntityServiceIT.ATTR_DEF_TELEPHONE_URL);
        checkNotNull(attr);
        
//		String url = structure.getEtypeURL();
//		System.out.println(url);
        assertEquals(EntityServiceIT.ATTR_DEF_TELEPHONE_URL, attr.getAttrDefId());
        checker.checkStruct(structure, false);
        assertNotNull(attr);
    }

//	@Test 
//	public void testGetStructureEtypeL(){
//		EntityService es = new EntityService(SwebConfiguration.getClientProtocol());
//		Long entityID= 64008L;
//		StructureODR structure =es.readStructure(entityID);
//		Etype etype= structure.getEtype();
//		assertEquals("Facility", etype.getName().getString(Locale.ENGLISH));
//	}
}
