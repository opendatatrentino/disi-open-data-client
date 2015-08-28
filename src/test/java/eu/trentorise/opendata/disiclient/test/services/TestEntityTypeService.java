package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.NAME_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import org.junit.Before;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 *
 */
public class TestEntityTypeService {

    private IEkb disiEkb;
    
    @Before
    public void beforeMethod() {
        disiEkb = ConfigLoader.init();
    }

    @Test
    public void testGetEntityTypeByID() {
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = (EntityType) ets.getEntityType(12L);
        List<IAttributeDef> atdefs = etype.getAttributeDefs();
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
        EntityTypeService ets = new EntityTypeService();
        // EntityType etype = (EntityType) ets.getEntityType(12L);
        EntityType etype = (EntityType) ets.readEntityType(NAME_URL);
        System.out.println("Etype:" + etype);
        etype.getAttributeDefs();
    }

    @Test
    public void testGetAllEntityTypes() {
        long timeStart = System.currentTimeMillis();
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        for (IEntityType etype : etypes) {

            List<IAttributeDef> atdefs = etype.getAttributeDefs();
            //System.out.println("AttributeDef ETYPE Name:"+etype.getName().string(Locale.ENGLISH));
            //	System.out.println("AttributeDefs:"+etype.getAttributeDefs());
            //	System.out.println("AttributeDef Name:"+etype.getNameAttrDef());
            //	System.out.println("AttributeDef Description:"+etype.getDescriptionAttrDef());
            for (IAttributeDef ad : atdefs) {
                System.out.println("AttributeDef URL:" + ad.getURL());
                System.out.println("AttributeDef  DataType:" + ad.getEType());
            }
        }
        long timeEnd = System.currentTimeMillis();
        long finalTime = timeEnd - timeStart;
        System.out.println(finalTime);
        assertNotNull(etypes.get(0));
    }

    @Test
    public void testGetRootsTypes() {
        EntityTypeService ets = new EntityTypeService();
        assertEquals("Entity", ets.getRootEtype().getName().string(Locale.ENGLISH));
        assertEquals("Structure", ets.getRootStructure().getName().string(Locale.ENGLISH));

    }

    @Test
    public void testGetEntityTypeByURL() {
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        for (IEntityType etype : etypes) {

            //System.out.println(etype.getName());
            //System.out.println(etype.getURL());
            IEntityType et = ets.getEntityType(etype.getURL());
            //	System.out.println(et.getName());
            assertNotNull(et);
        }
    }

    @Test
    public void testGetAttributeDefbyUrl() {
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        List<IAttributeDef> attrDefs = etypes.get(0).getAttributeDefs();
        String attrDefUrl = attrDefs.get(0).getURL();
        IAttributeDef attrDef = etypes.get(0).getAttrDef(attrDefUrl);
        assertNotNull(attrDef);
    }

    @Test
    public void testReadNonExistingEntityType() {        

        assertEquals(null, disiEkb.getEntityTypeService().getEntityType("http://blabla.com"));

    }

    @Test
    public void testFuzzySearchEtype() {
        EntityTypeService ets = new EntityTypeService();
        Locale locale = OdtUtils.languageTagToLocale("en");
        List<SearchResult> searchEtypes = ets.searchEntityTypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().string(Locale.ENGLISH));

    }
}
