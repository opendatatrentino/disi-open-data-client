package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.Search;

import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.LOCATION_URL;
import org.junit.After;


public class SearchServiceIT extends DisiTest {

    
    private Search searchService;
    
    @Before
    public void beforeMethod() {
    
        searchService = ((DisiEkb) ekb).getSearchService();
    }
    
    @After
    public void after(){
        searchService = null;        
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
