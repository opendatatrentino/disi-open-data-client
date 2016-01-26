package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.client.kb.SynsetClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.vocabulary.Synset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestKnowledgeService {

    public static final long CAR_GLOBAL_CONCEPT_ID = 15944L;

    private static Logger logger = LoggerFactory.getLogger(TestKnowledgeService.class);

    private static Set<Long> createdConceptIds = new HashSet();

    /**
     * Returns a new name
     *
     * @since 0.11.1
     */
    public static Map<String, String> makeName(String enName, String itName) {
        Map<String, String> ret = new HashMap();
        ret.put("en", enName);
        ret.put("it", itName);
        return ret;
    }

    
    /**
     * Creates a new random concept on the entity base using raw sweb client.
     *
     * @since 0.11.1
     */
    public static long createConcept() {
        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
        Concept conc = new Concept();
        logger.warn("todo setting hardcoded knowedge base id to 1");
        conc.setKnowledgeBaseId(1L);

        long uuid = UUID.randomUUID().getLeastSignificantBits();
        conc.setLabel("Disi Client Test Concept " + uuid);
        
        // NOTE: name and description are actually computed, so they won't be stored on server as part of the concept !!

        long ret = client.create(conc);
        createdConceptIds.add(ret);
        logger.info("Created concept with id " + ret);
        return ret;
    }

    /**
     * Tries to clean previously created concepts, if fails at least resets
     * internal list.
     *
     * @since 0.11.1
     */
    public static void resetCreatedConcepts() {
        try {
            cleanCreatedConcepts();
        }
        catch (Exception ex) {
            logger.error("Failed to clean created concepts. Current list is : " + createdConceptIds);
            logger.error("Resetting internal list.", ex);
            createdConceptIds = new HashSet();
        }
    }

    /**
     * Cleans previously created concepts.
     * @since 0.11.1
     */
    static void cleanCreatedConcepts() {
        for (Long concId : createdConceptIds) {
            ConceptClient cc = new ConceptClient(WebServiceURLs.getClientProtocol());
            Concept rc = cc.readConcept(concId, true);
            if (rc != null) {
                logger.info("Cleaning concept " + concId + " ...");
                cc.delete(rc);
            }
        }
        createdConceptIds = new HashSet();

    }

    @Before
    public void beforeMethod() {
        ConfigLoader.init();
        resetCreatedConcepts();
    }

    /**
     * @since 0.11.1
     */
    @After
    public void afterMethod() {
        cleanCreatedConcepts();
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
        IConcept concept = kserv.readRootConcept();
        assertNotEquals(concept.getURL(), null);
    }

    @Test
    public void testReadConcepts() {
        KnowledgeService ets = new KnowledgeService();
        List<String> conceptURLs = new ArrayList<String>();
        String rootConceptURL = ets.readRootConcept().getURL();

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

    /**
     * @since 0.11.1
     */
    @Test
    public void testClearConceptCache() {
                
        KnowledgeService ks = new KnowledgeService();
        
        long concId = createConcept();

        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
        
        Concept swebConcept = client.readConcept(concId, false);        

        assertTrue(swebConcept.getName().isEmpty());
     
        IConcept readConcept = ks.readConcept(WebServiceURLs.conceptIDToURL(concId));        
        String enName1 = readConcept.getName().getString(Locale.ENGLISH);
        
        IDict name = readConcept.getName();
                
        
        assertEquals(readConcept.getName().getString(Locale.ENGLISH),
                     swebConcept.getLabel());
        
        String enName2 = "Disi client test concept #2";
        swebConcept.setLabel(enName2);
                
        client.update(swebConcept);
        
        IConcept readConcept2 = ks.readConcept(WebServiceURLs.conceptIDToURL(concId));
        
        assertEquals(enName1, readConcept2.getName().getString(Locale.ENGLISH));

        ks.clearConceptsCache();

        IConcept readConcept3 = ks.readConcept(WebServiceURLs.conceptIDToURL(concId));
        
        assertEquals(enName2, readConcept3.getName().getString(Locale.ENGLISH));
        
    }
    
    
    
}
