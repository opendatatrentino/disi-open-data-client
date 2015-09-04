package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.*;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for SchemaImport services
 *
 * @author Ivan Tankoyeu
 *
 */
public class TestSchemaImport {

    private static final int INSTANCES = 100;
    private static final String FACILITY = "Facility";
    private final static Logger LOG = LoggerFactory.getLogger(TestSchemaImport.class.getName());
    private IEntityType etype;
    private IEkb ekb;
    
    @Before
    public void readEtype() {
        ekb = ConfigLoader.init();                        
        etype =  ekb.getEntityTypeService().readEntityType(FACILITY_URL);
    }
    
    @After
    public void after(){
        ekb = null;
        etype = null;
    }

    @Test
    public void testSchemaImportEtype() throws SchemaMatcherException {

        SchemaImport si = new SchemaImport(ekb);
        ISchema schema = si.extractSchema(etype, Locale.ENGLISH);
        LOG.info(schema.toString());
        assertEquals(schema.getName(), FACILITY);
    }

    @Test
    public void testSchemaImportCSV() throws SchemaMatcherException, IOException {
        SchemaImport si = new SchemaImport(ekb);
        File file = new File("impianti risalita.csv");
        ISchema schemaOut = si.parseCSV(file);
        assertNotNull(schemaOut);
    }

    @Test
    public void testSchemaImportEntities() throws SchemaMatcherException {
        
        List<Instance> instances = SwebClientCrap.getEntities(etype);
        assertEquals(instances.size(), INSTANCES);
    }

}
