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

import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.traceprov.types.Concept;
import eu.trentorise.opendata.commons.OdtUtils;

import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import org.junit.After;
import org.junit.Assert;

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
        
        Concept con = kserv.readConcept(CONCEPT_3_URL);
        checker.checkConcept(con);
        assertEquals(con.getId(), CONCEPT_3_URL);
    }

    @Test
    public void testReadNonExistingConcept() {    
	try {
	    Concept con = kserv.readConcept(makeNonExistingConceptUrl());
	    Assert.fail();
	} catch (OpenEntityNotFoundException ex){
	    
	}
    }

    @Test
    public void testReadRootConcept() {
        Concept concept = kserv.readRootConcept();
        checker.checkConcept(concept);        
    }

    @Test
    public void testGetZeroConcepts() {
        assertEquals(kserv.readConcepts(new ArrayList<String>()).size(), 0);
    }

    @Test
    public void testReadConcepts() {
        List<String> conceptURLs = new ArrayList();
        String rootConceptURL = kserv.readRootConcept().getId();

        conceptURLs.add(rootConceptURL);
        
        List<Concept> concepts = kserv.readConcepts(conceptURLs);
        assertEquals(concepts.get(0).getId(), rootConceptURL);
    }
    
    @Test
    public void testReadNonExistingConcepts() {
        List<String> conceptURLs = new ArrayList();
        String rootConceptURL = kserv.readRootConcept().getId();

        conceptURLs.add(rootConceptURL);
        conceptURLs.add(makeNonExistingConceptUrl());
        
        try {
            List<Concept> concepts = kserv.readConcepts(conceptURLs);
            Assert.fail();
        } catch (OpenEntityNotFoundException ex){
            
        }
        
    }
    

    @Test
    public void testSearchConcept() {        
        List<SearchResult> res = kserv.searchConcepts("vacation", Locale.ENGLISH);
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
	
	Concept rootConcept = kserv.readRootConcept();
        double scoreDist = kserv.getConceptsDistance(rootConcept.getId(), rootConcept.getId());        
        assertEquals(0, scoreDist, OdtUtils.TOLERANCE);                       
    }
    
    @Test
    public void testGetConceptNonZeroDistance() {
	
	Concept rootConcept = kserv.readRootConcept();
        double scoreDist = kserv.getConceptsDistance(rootConcept.getId(), um.conceptIdToUrl(GYMNASIUM_CONCEPT_ID));        
        assertTrue(scoreDist > 0.0);                       
    }
    
    @Test
    public void testLocalGlobalConceptId(){
        
        assertTrue(SwebClientCrap.readConceptGUID(1) > 0);
    }

}
