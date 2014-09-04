package eu.trentorise.opendata.disiclient.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 * @date 23 July 2014
 * 
 */
public class KnowledgeService implements IKnowledgeService {

	private static final long rootConceptID=1;
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
		

		//Long conceptId = Long.parseLong(s);
		String s;
		try {
    		s = URL.substring(URL.indexOf("ts/") + 3);
        } catch (Exception e) {
             throw new DisiClientException("Wrong Concept URL!");
        }

        Long conceptId;
        try {
        	conceptId = Long.parseLong(s);
        } catch (Exception e) {
            throw new DisiClientException("Wrong concept ID!");
        }
        
		
		ConceptODR concept = new ConceptODR();
		concept = concept.readConcept(conceptId);

		//System.out.println(concept.getURL());

		return concept;
	}



	public List<IConcept> getConcepts(List<String> URLs) {
		List<IConcept> concepts= new ArrayList<IConcept>();

		for (String url: URLs){
			IConcept c = getConcept(url);
			concepts.add(c);
		}
		return concepts;
	}



	public IConcept getRootConcept() {
		ConceptODR concept = new ConceptODR();
		concept = concept.readConcept(rootConceptID);
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

    public List<ISearchResult> searchConcepts(String partialName) {
    	List<ISearchResult> concepts = new ArrayList<ISearchResult>();
		return concepts;
    }

}
