package eu.trentorise.opendatarise.semantics.test.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendatarise.semantics.services.EntityExportService;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import org.junit.Test;


public class EntityExportServiceTest {

	@Test
	public void generateContextTest() throws IOException{
		EntityExportService ess = new EntityExportService();
		EntityTypeService ets = new EntityTypeService();
		//EntityType etype = ets.getEntityType(12L);

		List<Long> entities = new ArrayList<Long>();
		entities.add(1L);
		entities.add(4L);
		entities.add(7L);
		String filename="myFirstTest.txt";
		Long fileId = ess.methodPost(entities,filename );
		InputStream is = ess.methodGet(fileId, "sem"+filename);
//		Writer wr = new Writer;
//		ess.convertToJsonLd(is,wr);

		
	}
}
