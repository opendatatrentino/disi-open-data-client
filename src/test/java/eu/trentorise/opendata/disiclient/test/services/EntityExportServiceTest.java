package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.services.EntityExportService;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_URL;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EntityExportServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final long ENTITY1 = 1L;
    public static final String ENTITY1_URL = WebServiceURLs.entityIDToURL(ENTITY1);
    public static final long ENTITY2 = 4L;
    public static final String ENTITY2_URL = WebServiceURLs.entityIDToURL(ENTITY2);
    public static final long ENTITY3 = 7;
    public static final String ENTITY3_URL = WebServiceURLs.entityIDToURL(ENTITY3);
    List<String> entities;
    EntityExportService ess = new EntityExportService();
    EntityService es = new EntityService();

    /**
     * NOTE: CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS REGENERATED
     */
    private static final long MELA_VAL_DI_NON = 75167L;
    /**
     * NOTE: CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS REGENERATED
     */
    private static final String MELA_VAL_DI_NON_URL = WebServiceURLs.entityIDToURL(MELA_VAL_DI_NON);

    @Before
    public void test() {

        entities = new ArrayList<String>();
        entities.add(ENTITY1_URL);
        entities.add(ENTITY2_URL);
        entities.add(ENTITY3_URL);
    }

  

    @Test
    public void testExportToJsonLd() throws IOException {
        String filename = System.currentTimeMillis() + "myFirstTest.txt";
        Writer writer = new FileWriter(filename);
        es.exportToJsonLd(entities, writer);
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        assertNotNull(br.readLine());
        br.close();
    }

    @Test
    public void testExportToRDF() throws IOException {
        String filename = System.currentTimeMillis() + "myFirstTest.txt";
        Writer writer = new FileWriter(filename);
        es.exportToRdf(entities, writer);
        assertNotNull(writer);
        BufferedReader br = new BufferedReader(new FileReader(filename));
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

    /**
     * NOTE: USES ENTITY CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS
     * REGENERATED
     */
    @Test
    public void testExportMelaValDiNon() {

        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

        StringWriter sw = new StringWriter();
        List<String> entityURLs = new ArrayList<String>();
        entityURLs.add(MELA_VAL_DI_NON_URL);
        enServ.exportToJsonLd(entityURLs, sw);
        logger.info("JSONLD = " + sw.toString());
        assertTrue(sw.toString().length() > 0);
    }

}
