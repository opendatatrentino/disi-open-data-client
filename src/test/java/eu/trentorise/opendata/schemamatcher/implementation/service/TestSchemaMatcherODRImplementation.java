package eu.trentorise.opendata.schemamatcher.implementation.service;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.odr.impl.MatchingService;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.SchemaMapping;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSchemaMatcherODRImplementation {

    private static final String FACILITY = "Facility";

    private final static Logger LOG = LoggerFactory.getLogger(TestSchemaImport.class.getName());

    private IEkb ekb;
    
    @Before
    private void before(){
        ekb = ConfigLoader.init();
    }
    
    @After
    private void after(){
        ekb = null;
    }
    
    
    @Test
    public void testSchemaElementMatcherAllEtypes() throws IOException, SchemaMatcherException {

        File file = new File("impianti risalita.csv");
        MatchingService ms = new MatchingService(ekb);
        List<SchemaMapping> sc = ms.matchSchemasFile(file);
        for (SchemaMapping c : sc) {
            LOG.info("Etype name: " + c.getTargetEtype().getName().string(Locale.ENGLISH) + " " + c.getScore());
//			for(IAttributeCorrespondence ac: c.getAttributeCorrespondences()){
//				LOGGER.info("Attribute: "+ac.getAttrDef().getName().getString(Locale.ENGLISH)+" score: "+ac.getScore()+ " index: "+ac.getColumnIndex());
//			}
        }
        assertEquals(sc.get(0).getTargetEtype().getName().string(Locale.ENGLISH), FACILITY);
    }

}
