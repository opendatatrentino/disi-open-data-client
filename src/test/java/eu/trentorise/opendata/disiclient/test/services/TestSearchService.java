package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.Search;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;

public class TestSearchService {

    private IProtocolClient api;

    @Before
    public void beforeMethod() {
        ConfigLoader.init();
        this.api = WebServiceURLs.getClientProtocol();
    }

    @Test
    public void conceptSearchTest() {
        Search searchService = new Search(api);
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
        Search searchService = new Search(api);
        List<Name> names = searchService.nameSearch("PALAZZETTO DELLO SPORT");

        assertNotNull(names);
    }

    @Test
    public void testsearchEntities() {

        Search searchService = new Search(api);
        String etypeURL = WebServiceURLs.etypeIDToURL(18L);
        Locale locale = OdtUtils.languageTagToLocale("en");

        List<SearchResult> sResults = searchService.searchEntities("Povo", etypeURL, locale);
        for (SearchResult sr : sResults) {
            assertNotNull(sr.getId());
            assertNotNull(sr.getName());

        }
    }

}
