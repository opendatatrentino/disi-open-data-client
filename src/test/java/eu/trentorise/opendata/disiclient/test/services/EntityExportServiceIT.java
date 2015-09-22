package eu.trentorise.opendata.disiclient.test.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import eu.trentorise.opendata.disiclient.services.EntityExportService;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import java.io.FileInputStream;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EntityExportServiceIT extends DisiTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    List<String> entities;
    EntityExportService ess;
    EntityService enServ;


    @Before
    public void beforeMethod() {    
        entities = new ArrayList();
        entities.add(EntityServiceIT.ANDALO_URL);
        entities.add(EntityServiceIT.RAVAZZONE_URL);
        entities.add(EntityServiceIT.PALAZZETTO_URL);
        
        ess = ((DisiEkb) ekb).getEntityExportService();       
        enServ = (EntityService) ekb.getEntityService();        
    }

    @After
    public void after() {
        entities = null;
        ess = null;
        enServ = null;
    }
    
    /**
     * 
     * @param extension for example, "jsonld"
     * @return 
     */
    private File makeTempFile(String extension){
        String dirFilePath = "target/test-output/";
        File dirFile = new File(dirFilePath);
        
        if (dirFile.exists() || dirFile.mkdirs()){
            File file = new File(dirFilePath + "my-first-test-"+System.currentTimeMillis()+ "." + extension);
            logger.info("Creating file " + file.getAbsolutePath());
            return file;
        } else {
            throw new RuntimeException("Couldn't completely create directory " + dirFilePath);
        }
    }

    @Test
    public void testExportToJsonLd() throws IOException, JsonLdError {
        File file = makeTempFile("jsonld");
        Writer writer = new FileWriter(file);
        enServ.exportToJsonLd(entities, writer);
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(file));
        assertNotNull(br.readLine());
        br.close();
        
        String jsonld =  Files.toString(file, Charsets.UTF_8);
                
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(jsonld);
        assertEquals(JsonNodeType.ARRAY, node.getNodeType());
           
        // this weird jsonld library happily parses the string - todo add more checks
        InputStream inputStream = new FileInputStream(file.getAbsolutePath());
        Object jsonObject = JsonUtils.fromString(jsonld);        
        Object normalized = JsonLdProcessor.normalize(jsonObject);
        
    }
    
    

    @Test
    public void testExportToRDF() throws IOException {
        File file = makeTempFile("rdf");
        Writer writer = new FileWriter(file);
        enServ.exportToRdf(entities, writer);
        writer.close();
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(file));
        assertNotNull(br.readLine());
        br.close();
        
        Model model = ModelFactory.createDefaultModel(); 
                        
        model.read(new FileInputStream(file), "RDF/XML") ;        
        
    }

    @Test
    public void testCreateAndExportCertifiedProduct() {
       
        
        Etype et = ekb.getEtypeService().readEtype(CERTIFIED_PRODUCT_URL);

        AttrDef certificateTypeAttrDef = et.attrDefById(EntityServiceIT.ATTR_TYPE_OF_CERTIFICATE_URL);

        assertNotNull(certificateTypeAttrDef);

        Attr attr = Attr.ofObject(certificateTypeAttrDef, "Please work");

        Entity.Builder enb = Entity.builder();
        
        enb.setEtypeId(CERTIFIED_PRODUCT_URL);
               
        enb.putAttrs(certificateTypeAttrDef.getId(), attr);
        
        Entity en = enb.build();

        Entity createdEntity = null;

        try {
            createdEntity = enServ.createEntity(en);
            StringWriter sw = new StringWriter();
            List<String> entityURLs = new ArrayList();
            entityURLs.add(createdEntity.getId());
            enServ.exportToJsonLd(entityURLs, sw);
            logger.info("JSONLD = " + sw.toString());
            assertTrue(sw.toString().length() > 0);
        } finally {
            if (createdEntity != null) {
                enServ.deleteEntity(createdEntity.getId());
            }
        }

    }    
}
