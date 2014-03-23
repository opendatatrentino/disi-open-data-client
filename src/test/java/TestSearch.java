//import static org.junit.Assert.*;
//import it.unitn.disi.sweb.webapi.client.IProtocolClient;
//import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
//
//import java.util.List;
//import java.util.Locale;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import eu.trentorise.opendata.semantics.model.entity.IEntity;
//import eu.trentorise.opendatarise.semantics.services.Search;
//
//
//public class TestSearch {
//
//	private IProtocolClient api;
//
//	@Before
//	public void getClientProtocol(){
//		this.api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
//	}
//	
//	@Test
//	public void conceptSearchTest(){
//		Search searchService = new Search(api);
//		List<IEntity>  entities = searchService.conceptSearch("Trento");
//		IEntity entity = entities.get(0);
//		System.out.println(entity.getEtype().getName(Locale.ENGLISH));
//		assertNotNull(entity);
//		assertEquals("Location",entity.getEtype().getName(Locale.ENGLISH));
//	}
//	
//	@Test
//	public void searchTest(){
//		
//	}
//}
