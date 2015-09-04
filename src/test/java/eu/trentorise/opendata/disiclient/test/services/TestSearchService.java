package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.Search;

import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.LOCATION_URL;
import org.junit.After;


public class TestSearchService {

    private DisiEkb ekb;
    private Search searchService;
    
    @Before
    public void beforeMethod() {
        ekb = (DisiEkb) ConfigLoader.init();
        searchService = ekb.getSearchService();
    }
    
    @After
    public void after(){
        searchService = null;
        ekb = null;        
    }

    @Test
    public void conceptSearchTest() {
        
        List<IEntity> entities = searchService.conceptSearch("PALAZZETTO DELLO SPORT");
        for (IEntity entity : entities) {

            //			System.out.println(entity.getGUID());
            //			System.out.println("URL:"+entity.getURL());
            assertNotNull(entity);
            //assertEquals("Location",entity.getEtype().getName(Locale.ENGLISH));
        }
    }

    @Test
    public void nameSearchTest() {
        
        List<Name> names = searchService.nameSearch("PALAZZETTO DELLO SPORT");

        assertNotNull(names);
    }

    @Test
    public void testsearchEntities() {
       
        
        Locale locale = OdtUtils.languageTagToLocale("en");

        List<SearchResult> sResults = searchService.searchEntities("Povo", LOCATION_URL, locale);
        for (SearchResult sr : sResults) {
            assertNotNull(sr.getId());
            assertNotNull(sr.getName());

        }
    }

}
