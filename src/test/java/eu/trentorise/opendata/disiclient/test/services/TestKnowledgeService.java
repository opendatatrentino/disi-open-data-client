package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

public class TestKnowledgeService {

    Logger logger = LoggerFactory.getLogger(TestKnowledgeService.class);

    @Before
    public void beforeMethod() {
        ConfigLoader.init();
    }

    @Test
    public void testReadConcept() {
        KnowledgeService kserv = new KnowledgeService();
        String url = WebServiceURLs.conceptIDToURL(120);
        IConcept con = kserv.readConcept(url);
        assertEquals(con.getURL(), url);
    }

    @Test
    public void testReadNonExistingConcept() {
        KnowledgeService kserv = new KnowledgeService();
        String url = "blabla";
        IConcept con = kserv.readConcept(url);
        // thrown.expect(DisiClientException.class);

        assertEquals(con, null);
    }

    @Test
    public void testGetRootConcept() {
        KnowledgeService kserv = new KnowledgeService();
        IConcept concept = kserv.getRootConcept();
        assertNotEquals(concept.getURL(), null);
    }

    @Test
    public void testGetZeroConcepts() {
        KnowledgeService kserv = new KnowledgeService();
        assertEquals(kserv.getConcepts(new ArrayList<String>()).size(), 0);
    }

    @Test
    public void testReadConcepts() {
        KnowledgeService ets = new KnowledgeService();
        List<String> conceptURLs = new ArrayList<String>();
        String rootConceptURL = ets.getRootConcept().getURL();

        conceptURLs.add("non-existing-url");
        conceptURLs.add(rootConceptURL);
        List<IConcept> concepts = ets.readConcepts(conceptURLs);
        assertEquals(concepts.get(0), null);
        assertEquals(concepts.get(1).getURL(), rootConceptURL);
    }

    @Test
    public void testSearchConcept() {
        KnowledgeService ks = new KnowledgeService();
        Locale locale = TraceProvUtils.languageTagToLocale("en");
        List<ISearchResult> res = ks.searchConcepts("vacation", locale);
        for (ISearchResult r : res) {
            assertNotNull(r.getName());
            assertNotNull(r.getURL());
        }
    }

    @Test
    public void testCapitalizedConcept() {
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> res = ks.searchConcepts("Vacation", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSpacesConcept() {
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> res = ks.searchConcepts("   vacation", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteConcept() {
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> res = ks.searchConcepts("vacatio", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchMultiWordConcept() {
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> res = ks.searchConcepts("programming language", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteMultiWordConcept() {
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> res = ks.searchConcepts("programming langu", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

}
