package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
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
import java.util.HashMap;

/**
 * Updated in 0.11.1 to support caching, see
 * https://github.com/opendatatrentino/disi-open-data-client/issues/23
 * 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class KnowledgeService implements IKnowledgeService {

    /**
     * Note: in odt db it is equal to local id
     */
    private static final long ROOT_CONCEPT_GLOBAL_ID = 1;
    /**
     * Note: in odt db it is equal to local id
     */
    public static final long DESCRIPTION_CONCEPT_GLOBAL_ID = 3L;
    public static final long PART_OF_CONCEPT_GLOBAL_ID = 22L;
    public static final long CONTACT_CONCEPT_GLOBAL_ID = 120775L;

    /**
     * Maps sweb global id to concept
     */
    private static final HashMap<Long, Concept> swebGlobalIdToConcepts = new HashMap();

    /**
     * Maps sweb local id to concept
     */
    private static final HashMap<Long, Concept> swebLocalIdToConcepts = new HashMap();

    Logger logger = LoggerFactory.getLogger(KnowledgeService.class);

    // public List<IConcept> getConcepts(List<Long> GUIDs) {
    // List<IConcept> iconcepts = new ArrayList<IConcept>();
    //
    // for (Long guid :GUIDs){
    // ConceptODR con = new ConceptODR();
    // con = con.readConceptGlobalID(guid);
    // iconcepts.add(con);
    // }
    // return iconcepts;
    // }
    @Override
    public IConcept getConcept(String URL) {

	Long conceptId;

	String s;
	try {
	    s = URL.substring(URL.indexOf("ts/") + 3);
	} catch (Exception e) {
	    return null;

	    // throw new DisiClientException("Wrong Concept URL!");
	}

	try {
	    conceptId = Long.parseLong(s);
	} catch (Exception e) {
	    return null;

	    // throw new DisiClientException("Wrong concept ID!");
	}

	ConceptODR concept = new ConceptODR();
	concept = readConcept(conceptId);

	return concept;
    }

    @Override
    public List<IConcept> getConcepts(List<String> URLs) {
	List<IConcept> concepts = new ArrayList<IConcept>();

	for (String url : URLs) {
	    IConcept c = getConcept(url);
	    concepts.add(c);
	}
	return concepts;
    }

    @Override
    public IConcept getRootConcept() {
	ConceptODR concept = readConcept(ROOT_CONCEPT_GLOBAL_ID);
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
    public List<ISearchResult> searchConcepts(String partialName, Locale locale) {
	logger.warn("TODO - SETTING CONCEPT PARTIAL NAME TO LOWERCASE");
	partialName = partialName.toLowerCase(locale).trim();

	List<ISearchResult> conceptRes = new ArrayList<ISearchResult>();

	ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol(locale));
	logger.warn("Knowledge base is set to default (1)");
	List<Concept> concepts = client.readConcepts(1L, null, partialName, null, null, null);

	for (Concept c : concepts) {
	    swebGlobalIdToConcepts.put(c.getGlobalId(), c);
	    swebLocalIdToConcepts.put(c.getId(), c);

	    ConceptODR codr = new ConceptODR(c);
	    SearchResult sr = new SearchResult(codr);
	    conceptRes.add(sr);
	}

	return conceptRes;
    }

    public ConceptODR readConcept(long conceptId) {
	if (swebLocalIdToConcepts.get(conceptId) == null) {
	    ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
	    Concept conc = client.readConcept(conceptId, false);

	    swebGlobalIdToConcepts.put(conc.getGlobalId(), conc);
	    swebLocalIdToConcepts.put(conc.getId(), conc);

	    ConceptODR conceptODR = new ConceptODR(conc);
	    return conceptODR;
	} else {
	    ConceptODR ret = new ConceptODR(swebLocalIdToConcepts.get(conceptId));
	    logger.info("Reading cached concept " + ret.getURL() + "  ...");
	    return ret;
	}
    }

    /**
     * Ported from ConceptODR
     *
     * @since 0.11.1
     */
    public ConceptODR readConceptGlobalID(long glId) {

	if (swebGlobalIdToConcepts.get(glId) == null) {
	    ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
	    logger.warn("Entity Base is 1");
	    List<Concept> concepts = client.readConcepts(1L, glId, null, null, null, null);
	    ConceptODR conceptODR = new ConceptODR(concepts.get(0));
	    logger.warn("Only the first concept is returned. The number of returned concepts is: " + concepts.size());
	    swebGlobalIdToConcepts.put(glId, concepts.get(0));
	    swebLocalIdToConcepts.put(concepts.get(0).getId(), concepts.get(0));
	    return conceptODR;
	} else {
	    ConceptODR ret = new ConceptODR(swebGlobalIdToConcepts.get(glId));
	    logger.info("Reading cached concept " + ret.getURL() + "  ...");
	    return ret;
	}

    }

    /**
     * Ported from ConceptODR, careful this one actually returns the LOCAL ID
     *
     * @since 0.11.1
     */
    public Long readConceptGUID(long glId) {
	ConceptODR ret = readConceptGlobalID(glId);
	if (ret == null) {
	    return null;
	} else {
	    return ret.getId();
	}
    }

}
