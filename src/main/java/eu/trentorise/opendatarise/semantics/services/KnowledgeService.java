package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.client.kb.WordClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 25 Mar 2014
 * 
 */
public class KnowledgeService implements IKnowledgeService {

	public List<IConcept> getConcepts(List<Long> GUIDs) {
		List<IConcept> iconcepts = new ArrayList<IConcept>();

		for (Long guid :GUIDs){
			ConceptODR con = new ConceptODR();
			con = con.readConceptGlobalID(guid);
			iconcepts.add(con);
		}
		return iconcepts;
	}

}
