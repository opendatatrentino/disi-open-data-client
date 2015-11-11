package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.NAME_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.semantics.services.mock.MockEntityService;
import eu.trentorise.opendata.commons.TodUtils;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;

import eu.trentorise.opendata.semantics.services.IEtypeService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 *
 */
public class EtypeServiceIT extends DisiTest {

  
    private IEtypeService ets;
    
    @Before
    public void before() {  
        ets = ekb.getEtypeService();
  
    }
    
    @After
    public void after(){        
        ets = null;       
    }

    @Test
    public void testReadEtypeById() {
        
        Etype etype = (Etype) ets.readEtype(FACILITY_URL);        
        checker.checkEtype(etype);       
        assertEquals(etype.getName().str(Locale.ITALIAN), "Infrastruttura");
    }

    @Test
    public void testReadEtypesofStructure() {
               
        Etype etype = (Etype) ets.readEtype(NAME_URL);
        checker.checkEtype(etype);        
    }

    @Test
    public void testReadAllEtypes() {
        long timeStart = System.currentTimeMillis();
        
        List<Etype> etypes = ets.readAllEtypes();
        for (Etype etype : etypes) {
            checker.checkEtype(etype);
                   
            for (AttrDef ad : etype.getAttrDefs().values()) {
                System.out.println("AttributeDef URL:" + ad.getId());
                System.out.println("AttributeDef  Type:" + ad.getType());
            }
        }
        long timeEnd = System.currentTimeMillis();
        long finalTime = timeEnd - timeStart;
        System.out.println(finalTime);
        assertNotNull(etypes.get(0));
    }

    @Test
    public void testReadRootTypes() {
        
        Etype rootEtype = ets.readRootEtype();                
        assertEquals("Entity", rootEtype.getName().str(Locale.ENGLISH));        
        checker.checkEtype(rootEtype);
        
        Etype rootStructure = ets.readRootStruct();        
        assertEquals("Structure", rootStructure.getName().str(Locale.ENGLISH));
        checker.checkEtype(ets.readRootStruct());
    }

    @Test
    public void testReadEtype() {
        
        List<Etype> etypes = ets.readAllEtypes();
        for (Etype etype : etypes) {
            Etype et = ets.readEtype(etype.getId());
            checker.checkEtype(et);       
            assertNotNull(et);
        }
    }

    @Test
    public void testReadNonExistingEntityType() {	
	try {
	    ekb.getEtypeService().readEtype(makeNonExistingEtypeUrl() );
	    Assert.fail("Shouldn't arrive here!");
	} catch (OpenEntityNotFoundException ex){
	    
	}

    }

    @Test
    public void testFuzzySearchEtype() {
        
        Locale locale = TodUtils.languageTagToLocale("en");
        List<SearchResult> searchEtypes = ets.searchEtypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().str(Locale.ENGLISH));

    }
}
