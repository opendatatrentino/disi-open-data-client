package eu.trentorise.opendata.disiclient.test.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.EntityExportService;


public class EntityExportServiceTest {

	@Test
	public void generateContextTest() throws IOException{
		EntityExportService ess = new EntityExportService();
		//EntityTypeService ets = new EntityTypeService();
		//EntityType etype = ets.getEntityType(12L);

		List<Long> entities = new ArrayList<Long>();
		entities.add(1L);
		entities.add(4L);
		entities.add(7L);
		String filename="myFirstTest.txt";
		Long fileId = ess.methodPostRDF(entities,filename );
		InputStream is = ess.methodGet(fileId, "sem"+filename);
//		Writer wr = new Writer;
//		ess.convertToJsonLd(is,wr);

		
	}
}
