package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.model.SearchResult;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class KnowledgeService implements IKnowledgeService {

    Logger logger = LoggerFactory.getLogger(KnowledgeService.class);

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

    public List<IConcept> getConcepts(List<String> URLs) {
        List<IConcept> concepts = new ArrayList<IConcept>();

        for (String url : URLs) {
            IConcept c = getConcept(url);
            concepts.add(c);
        }
        return concepts;
    }

    public IConcept getRootConcept() {
        ConceptODR concept = new ConceptODR();
        concept = concept.readConcept(ROOT_CONCEPT_ID);
        return concept;
    }

    public List<IConcept> readConcepts(List<String> URLs) {
        return getConcepts(URLs);
    }

    public IConcept readConcept(String URL) {
        return getConcept(URL);
    }

    public IConcept readRootConcept() {
        return getRootConcept();
    }

    public List<ISearchResult> searchConcepts(String partialName, Locale locale) {

        List<ISearchResult> conceptRes = new ArrayList<ISearchResult>();
        
        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol(locale));
        logger.warn("Knowledge base is set to default (1)");
        List<Concept> concepts = client.readConcepts(1L, null, partialName, null, null, null);

        for (Concept c : concepts) {
            ConceptODR codr = new ConceptODR(c);
            SearchResult sr = new SearchResult(codr);
            conceptRes.add(sr);
        }

        return conceptRes;
    }

}
