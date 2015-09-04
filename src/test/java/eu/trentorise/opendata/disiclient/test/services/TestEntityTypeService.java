package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.NAME_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_URL;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import org.junit.After;
import org.junit.Before;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 *
 */
public class TestEntityTypeService {

    private IEkb ekb;
    private Checker checker;
    private IEntityTypeService ets;
    
    @Before
    public void before() {
        ekb = ConfigLoader.init();
        ets = ekb.getEntityTypeService();
        checker = Checker.of(ekb);
    }
    
    @After
    public void after(){
        checker = null;
        ets = null;
        ekb = null;
    }

    @Test
    public void testGetEntityTypeByID() {
        
        EntityType etype = (EntityType) ets.readEntityType(FACILITY_URL);
        List<IAttributeDef> atdefs = etype.getAttributeDefs();
        checker.checkEtype(etype);
        //for (IAttributeDef ad:atdefs){
//			System.out.println(ad.getName());
//			System.out.println(ad.getDataType());
//			System.out.println("AttributeDef Etype URL:"+ad.getEtypeURL());
        //	} 
        //		System.out.println("URL:"+etype.getURL());
        //		System.out.println(etype.getName1());
        assertEquals(etype.getName1().get("it"), "Infrastruttura");
    }

    @Test
    public void testGetEntityTypesofStructure() {
        
        // EntityType etype = (EntityType) ets.getEntityType(12L);
        EntityType etype = (EntityType) ets.readEntityType(NAME_URL);
        System.out.println("Etype:" + etype);
        etype.getAttributeDefs();
    }

    @Test
    public void testGetAllEntityTypes() {
        long timeStart = System.currentTimeMillis();
        
        List<IEntityType> etypes = ets.readAllEntityTypes();
        for (IEntityType etype : etypes) {

            List<IAttributeDef> atdefs = etype.getAttributeDefs();
            //System.out.println("AttributeDef ETYPE Name:"+etype.getName().string(Locale.ENGLISH));
            //	System.out.println("AttributeDefs:"+etype.getAttributeDefs());
            //	System.out.println("AttributeDef Name:"+etype.getNameAttrDef());
            //	System.out.println("AttributeDef Description:"+etype.getDescriptionAttrDef());
            for (IAttributeDef ad : atdefs) {
                System.out.println("AttributeDef URL:" + ad.getURL());
                System.out.println("AttributeDef  DataType:" + ad.getEtypeURL());
            }
        }
        long timeEnd = System.currentTimeMillis();
        long finalTime = timeEnd - timeStart;
        System.out.println(finalTime);
        assertNotNull(etypes.get(0));
    }

    @Test
    public void testGetRootsTypes() {
        
        assertEquals("Entity", ets.readRootEtype().getName().string(Locale.ENGLISH));
        assertEquals("Structure", ets.readRootStructure().getName().string(Locale.ENGLISH));

    }

    @Test
    public void testGetEntityTypeByURL() {
        
        List<IEntityType> etypes = ets.readAllEntityTypes();
        for (IEntityType etype : etypes) {

            //System.out.println(etype.getName());
            //System.out.println(etype.getURL());
            IEntityType et = ets.readEntityType(etype.getURL());
            //	System.out.println(et.getName());
            assertNotNull(et);
        }
    }

    @Test
    public void testGetAttributeDefbyUrl() {
        
        List<IEntityType> etypes = ets.readAllEntityTypes();
        List<IAttributeDef> attrDefs = etypes.get(0).getAttributeDefs();
        String attrDefUrl = attrDefs.get(0).getURL();
        IAttributeDef attrDef = etypes.get(0).getAttrDef(attrDefUrl);
        assertNotNull(attrDef);
    }

    @Test
    public void testReadNonExistingEntityType() {        

        assertEquals(null, ekb.getEntityTypeService().readEntityType("http://blabla.com"));

    }

    @Test
    public void testFuzzySearchEtype() {
        
        Locale locale = OdtUtils.languageTagToLocale("en");
        List<SearchResult> searchEtypes = ets.searchEntityTypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().string(Locale.ENGLISH));

    }
}
