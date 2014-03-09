package eu.trentorise.opendatarise.semantics.model.knowledge;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;

public class ConceptODR implements IConcept{

	private long id;
	private String label;
	private long globalId;
	private  Map<String, String> name;

	public ConceptODR(){}

	ConceptODR(Concept con){
		this.label = con.getLabel();
		this.id = con.getId();
		this.globalId = con.getGlobalId();
		this.name=con.getName();
	}


	public ConceptODR readConcept(long conceptId){

		ConceptClient client = new ConceptClient(getClientProtocol());
		Concept conc = client.readConcept(conceptId, false);
		ConceptODR conceptODR = new ConceptODR(conc);
		return conceptODR;
	}

	private List<ConceptODR> readConcepts(String label){
		ConceptClient client = new ConceptClient(getClientProtocol());
		List <ConceptODR> conOdrList = new ArrayList<ConceptODR>();
		Pagination page = new Pagination();
		List<Concept> concList = client.readConcepts(1L, null, null, label, null, null);
		for (Concept con: concList){
			ConceptODR conceptODR = new ConceptODR(con);	
			conOdrList.add(conceptODR);
		}
		return conOdrList;
	}

	public String getSynsetURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCommonlyReferredAs(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSummary(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPartOfSpeech(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}
	
	public Long getId(){
		return this.id;
	}

}
