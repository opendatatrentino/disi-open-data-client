package eu.trentorise.opendatarise.semantics.test.services;

import static org.junit.Assert.*;

import eu.trentorise.opendatarise.semantics.services.Search;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IEntity;


public class TestSearchService {

	private IProtocolClient api;

	@Before
	public void getClientProtocol(){
		this.api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
	}

	//@Test
	public void conceptSearchTest(){
		Search searchService = new Search(api);
		List<IEntity>  entities = searchService.conceptSearch("PALAZZETTO DELLO SPORT");
		for (IEntity entity : entities){
                  			
			System.out.println(entity.getGUID());
			System.out.println("URL:"+entity.getURL());

			assertNotNull(entity);
			//assertEquals("Location",entity.getEtype().getName(Locale.ENGLISH));
		}
	}
	
	@Test
	public void nameSearchTest(){
		Search searchService = new Search(api);
		List<Name> names =  searchService.nameSearch("PALAZZETTO DELLO SPORT");
		for (Name name : names){
  			
			System.out.println("IDs:"+name);

			
			//assertEquals("Location",entity.getEtype().getName(Locale.ENGLISH));
		}
		assertNotNull(names);
	}

}
