package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.DisiClients;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.SearchResult;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class KnowledgeService implements IKnowledgeService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeService.class);

    private static final long ROOT_CONCEPT_ID = 1;
    public static final long DESCRIPTION_CONCEPT_ID = 3L;
    public static final long PARTOF_CONCEPT_ID = 3L;
    //	public List<IConcept> getConcepts(List<Long> GUIDs) {
    //		List<IConcept> iconcepts = new ArrayList<IConcept>();
    //
    //		for (Long guid :GUIDs){
    //			ConceptODR con = new ConceptODR();
    //			con = con.readConceptGlobalID(guid);
    //			iconcepts.add(con);
    //		}
    //		return iconcepts;
    //	}

    @Override
    public IConcept getConcept(String URL) {

        Long conceptId;

        String s;
        try {
            s = URL.substring(URL.indexOf("ts/") + 3);
        }
        catch (Exception e) {
            return null;

            //throw new DisiClientException("Wrong Concept URL!");
        }

        try {
            conceptId = Long.parseLong(s);
        }
        catch (Exception e) {
            return null;

            //throw new DisiClientException("Wrong concept ID!");
        }

        ConceptODR concept = new ConceptODR();
        concept = concept.readConcept(conceptId);

        return concept;
    }

    @Override
    public List<IConcept> getConcepts(List<String> URLs) {
        List<IConcept> concepts = new ArrayList();

        for (String url : URLs) {
            IConcept c = getConcept(url);
            concepts.add(c);
        }
        return concepts;
    }

    @Override
    public IConcept getRootConcept() {
        ConceptODR concept = new ConceptODR();
        concept = concept.readConcept(ROOT_CONCEPT_ID);
        return concept;
    }

    @Override
    public List<IConcept> readConcepts(List<String> URLs) {
        return getConcepts(URLs);
    }

    @Override
    public IConcept readConcept(String URL) {
        return getConcept(URL);
    }

    @Override
    public IConcept readRootConcept() {
        return getRootConcept();
    }

    @Override
    public List<SearchResult> searchConcepts(String partialName, Locale locale) {

        LOG.warn("TODO - SETTING CONCEPT PARTIAL NAME TO LOWERCASE");
        String lowerCasePartialName = partialName.toLowerCase(locale);

        List<SearchResult> conceptRes = new ArrayList();

        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol(locale));
        LOG.warn("Knowledge base is set to default (1)");
        List<Concept> concepts = client.readConcepts(1L, null, lowerCasePartialName, null, null, null);

        for (Concept c : concepts) {
            ConceptODR codr = new ConceptODR(c);
            SearchResult sr = DisiClients.makeSearchResult(codr);
            conceptRes.add(sr);
        }

        return conceptRes;
    }

    
    /**
     * The maximum distance between two concepts todo super arbitrary number
     * @return 
     */
    public int getConceptHierarchyDiameter(){        
        return 50;
    }

    /**
     * Returns the distance between two concept. The method uses LCA approach.
     *
     * @param source source concept
     * @param target target concept
     * @return
     */
    public double getConceptsDistance(long source, long target) {
        if ((source < 0) || (target < 0)) {
            throw new IllegalArgumentException("Invalid concept ids: source " + source + ", target " + target);
        }
        ConceptClient cClient = new ConceptClient(WebServiceURLs.getClientProtocol());
        Integer distanceInteger = cClient.getDistanceUsingLca(source, target);        
        if (distanceInteger == null) {
            throw new DisiClientException("Server returned null distance between concepts!");
        }       
        int distanceInt = (int) distanceInteger;
        if (distanceInt < 0){
            return 1.0;
        }
        if (Math.abs(distanceInt) == 1) {
            return 0.0;
        }
        return distanceInt * 1.0 / getConceptHierarchyDiameter();
    }
  

    @Override
    public double getConceptsDistance(String sourceUrl, String targetUrl) {
        return getConceptsDistance(WebServiceURLs.urlToConceptID(sourceUrl),
                WebServiceURLs.urlToConceptID(sourceUrl));
    }

}
