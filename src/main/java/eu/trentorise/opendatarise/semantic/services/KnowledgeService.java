package eu.trentorise.opendatarise.semantic.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.WordClient;

import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;

public class KnowledgeService implements IKnowledgeService {

	public List<IWord> readByWordLemma(String wordLemma) {
		WordClient wClient=new WordClient(getClientProtocol());
		//TODO check which vocabulary to return
		//for the moment vocabulary is English vocabulary = 1
		
		System.out.println(wClient.readWords(1L, null, wordLemma, null, null, null, null));
		return null;
	}

	public List<IWord> readByWordPrefix(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}
	
}
