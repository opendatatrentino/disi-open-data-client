package eu.trentorise.opendata.disiclient.test.services;


import static org.junit.Assert.assertNotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.EntityExportService;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;


public class EntityExportServiceTest {

	public static final long ENTITY1 = 1L;
	public static final String ENTITY1_URL = WebServiceURLs.entityIDToURL(ENTITY1);
	public static final long ENTITY2 = 4L;
	public static final String ENTITY2_URL =  WebServiceURLs.entityIDToURL(ENTITY2);
	public static final long ENTITY3 = 7L;
	public static final String ENTITY3_URL =  WebServiceURLs.entityIDToURL(ENTITY3);

	@Test
	public void generateContextTest() throws IOException{
		EntityExportService ess = new EntityExportService();
		EntityService es = new EntityService();

		List<String> entities = new ArrayList<String>();
		entities.add(ENTITY1_URL);
		entities.add(ENTITY2_URL);
		entities.add(ENTITY3_URL);
		String filename="myFirstTest.txt";

		Writer writer = new FileWriter(System.currentTimeMillis()+filename);
		es.exportToJsonLd(entities,  writer);
		assertNotNull(writer);

	}


}
