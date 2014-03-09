package eu.trentorise.opendatarise.semantic.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.WordClient;

import java.util.Locale;

import eu.trentorise.opendatarise.semantic.services.EntityTypeService;

public class ODRTest {
	
	public static void main(String[] args){
		
		EntityTypeService ets = new EntityTypeService();
		//ets.getAllEntityTypes();
		//ets.getEntityType(4L);
		//ets.getAllEntityTypes();
		//WordClient wClient=new WordClient(getClientProtocol());
		//TODO check which vocabulary to return
		//for the moment vocabulary is English vocabulary = 1
		//String wordLemma="run";
		//System.out.println(wClient.readWords(1L, null, wordLemma, null, null, null, null));
	}

	private static IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}
}
