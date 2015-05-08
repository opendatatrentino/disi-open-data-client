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
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import org.junit.Before;
import org.parboiled.common.ImmutableList;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 * @date 28 July 2014
 *
 */
public class TestEntityTypeService {

    @Before
    public void beforeMethod(){
        ConfigLoader.init();
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
            //System.out.println("AttributeDef ETYPE Name:"+etype.getName().getString(Locale.ENGLISH));
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
        assertEquals("Entity", ets.getRootEtype().getName().getString(Locale.ENGLISH));
        assertEquals("Structure", ets.getRootStructure().getName().getString(Locale.ENGLISH));

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
        IEkb disiEkb = new DisiEkb();

        assertEquals(disiEkb.getEntityTypeService().getEntityType("http://blabla.com"), null);

    }

    @Test
    public void testFuzzySearchEtype() {
        EntityTypeService ets = new EntityTypeService();
        Locale locale = TraceProvUtils.languageTagToLocale("en");
        List<ISearchResult> searchEtypes = ets.searchEntityTypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().getString(Locale.ENGLISH));

    }
}
