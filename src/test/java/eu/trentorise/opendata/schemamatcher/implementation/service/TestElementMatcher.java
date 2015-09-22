package eu.trentorise.opendata.schemamatcher.implementation.service;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.ElementMatcherFactory;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEtypeService;

import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestElementMatcher {

    private final static Logger LOG = LoggerFactory.getLogger(TestSchemaImport.class.getName());
    
    
    private Etype etype;
    private IEkb ekb;
    private static final double DELTA = 1e-15;

    @Before
    public void beforeMethod() {
        ekb = ConfigLoader.init();
        IEtypeService ets = ekb.getEtypeService();     
        etype = ets.readEtype(FACILITY_URL);
    }
    
    @After
    public void afterMethod(){
        ekb = null;
        etype = null;
    }

    @Test
    public void testSchemaElementMatcher() throws IOException, SchemaMatcherException {
        SchemaImport si = new SchemaImport(ekb);
        File file = new File("impianti risalita.csv");

        ISchema schemaCSV = si.parseCSV(file);
        ISchema schemaEtype = si.extractSchema(etype, Locale.ITALIAN);

        ElementMatcherFactory emf = new ElementMatcherFactory();
        @SuppressWarnings("static-access")
        ISchemaElementMatcher elementMatcher = emf.create("EditDistanceBased");

        List<ISchemaElementCorrespondence> correspondences = elementMatcher.matchSchemaElements(schemaCSV.getElements(), schemaEtype.getElements());
        for (ISchemaElementCorrespondence cor : correspondences) {
            if (cor.getSourceElement().getElementContext().getElementName().equalsIgnoreCase("nome")) {
                assertEquals(cor.getElementCorrespondenceScore(), 1.0, DELTA);
            }
            LOG.info("SourceName: " + cor.getSourceElement().getElementContext().getElementName());
            LOG.info("TargetName: " + cor.getTargetElement().getElementContext().getElementName());
            LOG.info("Score: " + cor.getElementCorrespondenceScore());
        }

    }

}
