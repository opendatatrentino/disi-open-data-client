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

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;

import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

public class KnowledgeServiceIT extends DisiTest {

    
    Logger LOG = LoggerFactory.getLogger(KnowledgeServiceIT.class);
    
    IKnowledgeService kserv;

    @Before
    public void beforeMethod() {
        
        kserv = ekb.getKnowledgeService();
    }

    @After
    public void afterMethod() {
        kserv = null;        
    }

    @Test
    public void testReadConcept() {
        
        IConcept con = kserv.readConcept(CONCEPT_3_URL);
        assertEquals(con.getURL(), CONCEPT_3_URL);
    }

    @Test
    public void testReadNonExistingConcept() {        
        IConcept con = kserv.readConcept("blabla");
        assertEquals(con, null);
    }

    @Test
    public void testGetRootConcept() {
        IConcept concept = kserv.readRootConcept();
        assertNotEquals(concept.getURL(), null);
    }

    @Test
    public void testGetZeroConcepts() {
        assertEquals(kserv.readConcepts(new ArrayList<String>()).size(), 0);
    }

    @Test
    public void testReadConcepts() {
        List<String> conceptURLs = new ArrayList();
        String rootConceptURL = kserv.readRootConcept().getURL();

        conceptURLs.add("non-existing-url");
        conceptURLs.add(rootConceptURL);
        List<IConcept> concepts = kserv.readConcepts(conceptURLs);
        assertEquals(concepts.get(0), null);
        assertEquals(concepts.get(1).getURL(), rootConceptURL);
    }

    @Test
    public void testSearchConcept() {
        Locale locale = OdtUtils.languageTagToLocale("en");
        List<SearchResult> res = kserv.searchConcepts("vacation", locale);
        for (SearchResult r : res) {

            assertNotNull(r.getName());
            assertNotNull(r.getId());
        }
    }

    @Test
    public void testCapitalizedConcept() {
        List<SearchResult> res = kserv.searchConcepts("Vacation", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSpacesConcept() {
        List<SearchResult> res = kserv.searchConcepts("   vacation", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteConcept() {
        List<SearchResult> res = kserv.searchConcepts("vacatio", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchMultiWordConcept() {
        List<SearchResult> res = kserv.searchConcepts("programming language", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testSearchIncompleteMultiWordConcept() {
        List<SearchResult> res = kserv.searchConcepts("programming langu", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testGetConceptDistance() {
        double scoreDist = kserv.getConceptsDistance(CONCEPT_1_URL, NAME_CONCEPT_URL);
        ((KnowledgeService) ekb.getKnowledgeService()).getConceptHierarchyDiameter();
        assertEquals(0, scoreDist, 0.1);
        throw new UnsupportedOperationException("TODO IMPLEMENT ME!");
        
    }
    
    @Test
    public void testLocalGlobalConceptId(){
        
        SwebClientCrap.readConceptGUID(1);
    }

}
