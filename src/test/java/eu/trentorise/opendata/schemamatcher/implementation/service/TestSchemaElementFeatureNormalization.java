package eu.trentorise.opendata.schemamatcher.implementation.service;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import org.junit.After;

public class TestSchemaElementFeatureNormalization {
    private static final double DELTA = 1e-6;
    private static final double DIVERGENCE = 4.0861425;
    
    private Etype etype;
    ISchema sourceSchema;
    ISchema targetSchema;
    
    private IEkb ekb;
    
    
    @Before
    public void importSchemas() throws IOException, SchemaMatcherException {
        ekb = ConfigLoader.init();
        SchemaImport si = new SchemaImport(ekb);
        IEtypeService ets = ekb.getEtypeService();
        
        etype = ets.readEtype(FACILITY_URL);

        File file = new File("impianti risalita.csv");
        sourceSchema = si.parseCSV(file);
        targetSchema = si.extractSchema(etype, Locale.ENGLISH);
    }
    
    @After
    public void after(){
        ekb = null;
        etype = null;
        sourceSchema = null;
        targetSchema = null;
        
    }

    @Test
    public void testKLDivergenceDistance() {

        SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
        List<ISchemaElement> sourceSchemaElements = sourceSchema.getElements();
        List<ISchemaElement> targetSchemaElements = targetSchema.getElements();

        for (ISchemaElement sel : sourceSchemaElements) {
            if (sel.getElementContext().getElementDataType().equalsIgnoreCase("FLOAT")) {
                for (ISchemaElement tel : targetSchemaElements) {
                    if ((tel.getElementContext().getElementDataType().equalsIgnoreCase(DataTypes.FLOAT)) && (tel.getElementContent().getContentSize() > 0)) {
                        if (tel.getElementContext().getElementName().equalsIgnoreCase("Latitude") && sel.getElementContext().getElementName().equalsIgnoreCase("latitudine")) {
                            assertEquals(sefe.getStatisticalDistance(sel.getElementContent(), tel.getElementContent()), DIVERGENCE, DELTA);
                        }
                    }
                }
            }
        }

    }

}
