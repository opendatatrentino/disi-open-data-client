package eu.trentorise.opendatarise.semantics.model.knowledge;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;

public class ConceptODR implements IConcept{

	private long id;
	private String label;
	private long globalID;
	private Map<String, String> name;
	private Map<String, String> description;
	private IProtocolClient api;

	public ConceptODR(){}

	ConceptODR(Concept con){
		this.label = con.getLabel();
		this.id = con.getId();
		this.globalID = con.getGlobalId();
		this.name=con.getName();
		this.description=con.getDescription();
	}


	public ConceptODR readConcept(long conceptId){

		ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
		Concept conc = client.readConcept(conceptId, false);
		ConceptODR conceptODR = new ConceptODR(conc);
		return conceptODR;
	}

	public ConceptODR readConceptGlobalID(long glId){

		ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());

		List<Concept> concepts = client.readConcepts(1L, glId, null, null, null, null);

		ConceptODR conceptODR = new ConceptODR(concepts.get(0));
		return conceptODR;
	}

	public Long readConceptGUID(long glId){
		ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
		List<Concept> concepts = client.readConcepts(1L, glId, null, null, null, null);
		return concepts.get(0).getId();
	}

	private List<ConceptODR> readConcepts(String label){
		ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
		List <ConceptODR> conOdrList = new ArrayList<ConceptODR>();
		Pagination page = new Pagination();
		List<Concept> concList = client.readConcepts(1L, null, null, label, null, null);
		for (Concept con: concList){
		
			ConceptODR conceptODR = new ConceptODR(con);	
			conOdrList.add(conceptODR);
		}
		return conOdrList;
	}

	public Long getId(){
		return this.id;
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/concepts/"+this.id;
		return url;
	}

	public Long getGUID() {
		return globalID;
	}

	public IDict getDescription() {
		Dict dict = new Dict();
		Iterator it = this.description.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = Locale.forLanguageTag((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());

		}
		return dict;
	}

	public IDict getName() {
		Dict dict = new Dict();
		Iterator it = this.name.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = Locale.forLanguageTag((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());

		}
		return dict;
	}

}
