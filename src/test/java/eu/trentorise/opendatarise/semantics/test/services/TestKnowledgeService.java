package eu.trentorise.opendatarise.semantics.test.services;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendatarise.semantics.services.KnowledgeService;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class TestKnowledgeService {

    List<Long> guids = new ArrayList() {
        {
            add(132L);
            add(46263L);
            add(46270L);
        }
    };

    @Test
    public void testReadConcept() {
        KnowledgeService kserv = new KnowledgeService();
        String url = "http://opendata.disi.unitn.it:8080/odr/concepts/120";
        IConcept con = kserv.readConcept(url);
        assertEquals(con.getURL(), url);
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
        assertEquals(kserv.getConcepts(new ArrayList()).size(), 0);
    }

    @Test
    public void testReadConcepts() {
        KnowledgeService ets = new KnowledgeService();
        List<String> conceptURLs = new ArrayList();
        String rootConceptURL = ets.getRootConcept().getURL();

        conceptURLs.add("non-existing-url");
        conceptURLs.add(rootConceptURL);

        List<IConcept> concepts = ets.readConcepts(conceptURLs);
        assertEquals(concepts.get(0), null);
        assertEquals(concepts.get(1).getURL(), rootConceptURL);

    }

}
