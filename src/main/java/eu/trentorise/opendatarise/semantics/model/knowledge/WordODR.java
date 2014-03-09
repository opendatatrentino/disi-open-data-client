package eu.trentorise.opendatarise.semantics.model.knowledge;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.WordClient;

import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;

public class WordODR implements IWord {

	long id; 
	String lemma;
	String token;
	List<ConceptODR> senses;
	ConceptODR concept;


	public String getLemma() {
		return this.lemma;
	}

	public List<ConceptODR> getSensesODR() {

		return this.senses;
	}

	public List<IConcept> getSenses() {

		return null;
	}

	public IConcept getSelectedSense() {
		// TODO What is selected senses, selected by which criteria ????
		return null;
	}

	public void setToken(String token) {
		// TODO Auto-generated method stub

	}

	public void setLemma(String lemma) {
		// TODO Auto-generated method stub

	}

	public void setSenses(List<IConcept> senses) {
		// TODO Auto-generated method stub

	}

	public void setSelectedSense(IConcept selectedSense) {
		// TODO Auto-generated method stub

	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}



}
