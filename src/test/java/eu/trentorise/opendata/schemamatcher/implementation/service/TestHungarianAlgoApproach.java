package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.KuhnMunkres;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import org.junit.After;
import org.junit.Before;

public class TestHungarianAlgoApproach {
    private static final double DELTA = 1e-15;
    
    KuhnMunkres ha;
    IEkb ekb;        

    @Before
    public void beforeMethod() {
        ekb = ConfigLoader.init();
        ha = new KuhnMunkres();                
    }
    
    @After
    public void afterMethod(){
        ekb = null;   
        ha = null;
    }    
    
    @Test
    public void testHungarianMethod() {
        double[][] inputMatrix = new double[5][5];
        float minX = 0.0f;
        float maxX = 1.0f;
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                float finalX = rand.nextFloat() * (maxX - minX) + minX;
                inputMatrix[i][j] = finalX;
                System.out.print(finalX + "    ");
            }
            System.out.println();
        }
        int[][] assignedAttrs = KuhnMunkres.computeAssignments(inputMatrix);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(assignedAttrs[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void testSchemaElementMatcher() throws IOException, SchemaMatcherException {
        SchemaImport si = new SchemaImport(ekb);
        //File file = new File("//home/ivan/work/development/Schema Matching dataset/10_LuoghiStoriciCommercio2010_2013.1387270045.csv");
        File file = new File("impianti risalita.csv");

        ISchema schemaCSV = si.parseCSV(file);

        ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("HungarianAllocationAndEditDistance");
        List<ISchema> sourceSchemas = new ArrayList();
        sourceSchemas.add(schemaCSV);
        List<ISchema> targetSchemas = getAllTargetSchemas();
        List<ISchemaCorrespondence> schemaCor = schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "ConceptDistanceBased");
        for (ISchemaElementCorrespondence sec : schemaCor.get(0).getSchemaElementCorrespondence()) {
            System.out.print("Source Name: " + sec.getSourceElement().getElementContext().getElementName());
            System.out.print(" Target Name: " + sec.getTargetElement().getElementContext().getElementName());
            System.out.println(" Score: " + sec.getElementCorrespondenceScore());
            if (sec.getSourceElement().getElementContext().getElementName().equalsIgnoreCase("nome")) {
                assertEquals(sec.getElementCorrespondenceScore(), 1.0, DELTA);
            }

        }
        //LOGGER.info(schemaCor.getSchemaElementCorrespondence().toString());
    }

    public  List<ISchema> getAllTargetSchemas() throws SchemaMatcherException {
        IEntityTypeService etypeService = ekb.getEntityTypeService();
        List<IEntityType> etypeList = etypeService.readAllEntityTypes();
        List<ISchema> targetSchemas = new ArrayList();
        SchemaImport si = new SchemaImport(ekb);

        for (IEntityType etype : etypeList) {
            ISchema schemaEtype = si.extractSchema(etype, Locale.ENGLISH);
            targetSchemas.add(schemaEtype);
        }
        return targetSchemas;
    }
}
