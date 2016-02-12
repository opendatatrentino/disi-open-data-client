package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.services.EntityExportService;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.MELA_VAL_DI_NON_URL;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;



public class EntityExportServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final long ENTITY1 = 1L;
    public static  String ENTITY1_URL;
    public static final long ENTITY2 = 4L;
    public static String ENTITY2_URL;
    public static final long ENTITY3 = 7;
    public static String ENTITY3_URL;
    List<String> entities;
    EntityExportService ess;
    EntityService es;


    @Before
    public void beforeMethod() {
        ConfigLoader.init();
        ess = new EntityExportService();
        es = new EntityService();
        
        entities = new ArrayList<String>();
        entities.add(ENTITY1_URL);
        entities.add(ENTITY2_URL);
        entities.add(ENTITY3_URL);
    }

    private File makeTempFile(){        
        String dirFilePath = "target/test-output/";
        File dirFile = new File(dirFilePath);
        
        if (dirFile.exists() || dirFile.mkdirs()){
            return new File(dirFilePath + "my-first-test-"+System.currentTimeMillis()+ ".txt");
        } else {
            throw new RuntimeException("Couldn't completely create directory " + dirFilePath);
        }
    }

    @Test
    public void testExportToJsonLd() throws IOException {
        File file = makeTempFile();
        Writer writer = new FileWriter(file);
        es.exportToJsonLd(entities, writer);
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(file));
        assertNotNull(br.readLine());
        br.close();
    }

    @Test
    public void testExportToRDF() throws IOException {
        File file = makeTempFile();
        Writer writer = new FileWriter(file);
        es.exportToRdf(entities, writer);
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(file));
        assertNotNull(br.readLine());
        br.close();
    }

    @Test
    public void testCreateAndExportCertifiedProduct() {

        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

        EntityTypeService ets = new EntityTypeService();
        IEntityType et = ets.readEntityType(CERTIFIED_PRODUCT_URL);

        IAttributeDef certificateTypeAttrDef = et.getAttrDef(TestEntityService.ATTR_TYPE_OF_CERTIFICATE_URL);

        assertNotNull(certificateTypeAttrDef);

        IAttribute attr = enServ.createAttribute(certificateTypeAttrDef, "Please work");

        EntityODR en = new EntityODR();
        en.setEntityBaseId(1L);
        en.setTypeId(CERTIFIED_PRODUCT_ID);

        List<IAttribute> attrs = new ArrayList();
        attrs.add(attr);
        en.setStructureAttributes(attrs);

        String enURL = null;

        try {
            enURL = enServ.createEntityURL(en);
            StringWriter sw = new StringWriter();
            List<String> entityURLs = new ArrayList();
            entityURLs.add(enURL);
            enServ.exportToJsonLd(entityURLs, sw);
            logger.info("JSONLD = " + sw.toString());
            assertTrue(sw.toString().length() > 0);
        } finally {
            if (enURL != null) {
                enServ.deleteEntity(enURL);
            }
        }

    }
    
  
    
}
