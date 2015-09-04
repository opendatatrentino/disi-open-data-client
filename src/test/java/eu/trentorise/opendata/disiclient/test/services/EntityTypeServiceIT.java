package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.NAME_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import org.junit.After;
import org.junit.Before;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 *
 */
public class EntityTypeServiceIT extends DisiTest {

  
    private IEntityTypeService ets;
    
    @Before
    public void before() {  
        ets = ekb.getEntityTypeService();
  
    }
    
    @After
    public void after(){        
        ets = null;       
    }

    @Test
    public void testReadEntityTypeByID() {
        
        EntityType etype = (EntityType) ets.readEntityType(FACILITY_URL);
        List<IAttributeDef> atdefs = etype.getAttributeDefs();
        checker.checkEtype(etype);       
        assertEquals(etype.getName1().get("it"), "Infrastruttura");
    }

    @Test
    public void testReadEntityTypesofStructure() {
        
        // EntityType etype = (EntityType) ets.getEntityType(12L);
        EntityType etype = (EntityType) ets.readEntityType(NAME_URL);
        checker.checkEtype(etype);        
    }

    @Test
    public void testReadAllEntityTypes() {
        long timeStart = System.currentTimeMillis();
        
        List<IEntityType> etypes = ets.readAllEntityTypes();
        for (IEntityType etype : etypes) {
            checker.checkEtype(etype);
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
    public void testReadRootTypes() {
        
        IEntityType rootEtype = ets.readRootEtype();                
        assertEquals("Entity", rootEtype.getName().string(Locale.ENGLISH));        
        checker.checkEtype(rootEtype);
        
        IEntityType rootStructure = ets.readRootStructure();        
        assertEquals("Structure", rootStructure.getName().string(Locale.ENGLISH));
        checker.checkEtype(ets.readRootStructure());
    }

    @Test
    public void testReadEntityType() {
        
        List<IEntityType> etypes = ets.readAllEntityTypes();
        for (IEntityType etype : etypes) {
            IEntityType et = ets.readEntityType(etype.getURL());
            checker.checkEtype(et);       
            assertNotNull(et);
        }
    }

  

    @Test
    public void testReadNonExistingEntityType() {        
        assertEquals(null, ekb.getEntityTypeService().readEntityType(SwebConfiguration.getUrlMapper().etypeIdToUrl(100000000000000000L)));

    }

    @Test
    public void testFuzzySearchEtype() {
        
        Locale locale = OdtUtils.languageTagToLocale("en");
        List<SearchResult> searchEtypes = ets.searchEntityTypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().string(Locale.ENGLISH));

    }
}
