package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;

public class TestKnowledgeService {

	Logger logger = LoggerFactory.getLogger(TestKnowledgeService.class);

	
    List<Object> guids = new ArrayList<Object>() {
        {
            add(132L);
            add(46263L);
            add(46270L);
        }
    };


//	@Rule
//	public ExpectedException thrown= ExpectedException.none(); 
//	
	
    @Test    
    public void testReadConcept() {
        KnowledgeService kserv = new KnowledgeService();
        String url = "http://opendata.disi.unitn.it:8080/odr/concepts/120";
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
    public void testSearchConcept(){
        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult>res = ks.searchConcepts("cat");
        for (ISearchResult r: res){ 
        	assertNotNull(r.getName());
        	assertNotNull(r.getURL());
        }
    }
    
}
