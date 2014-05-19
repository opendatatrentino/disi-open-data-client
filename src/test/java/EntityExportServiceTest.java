import java.io.IOException;

import org.junit.Test;

import eu.trentorise.opendatarise.semantics.services.EntityExportService;


public class EntityExportServiceTest {

	@Test
	public void generateContextTest() throws IOException{
		EntityExportService ess = new EntityExportService();
		
		ess.generateJSONLDContext(15001L);
	}
		

}
