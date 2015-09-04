package eu.trentorise.opendata.schemamatcher.implementation.service;

import eu.trentorise.opendata.disiclient.test.services.KnowledgeServiceIT;
import static eu.trentorise.opendata.disiclient.test.services.KnowledgeServiceIT.HOURS_CONCEPT_URL;
import static eu.trentorise.opendata.disiclient.test.services.KnowledgeServiceIT.INFORMATION_TECHNOLOGY_CONCEPT_URL;
import static eu.trentorise.opendata.disiclient.test.services.KnowledgeServiceIT.NAME_CONCEPT_URL;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.junit.After;
import org.junit.Before;

public class TestSchemaElementFeatureExtractor {

    IEkb ekb;

    @Before
    public void beforeMethod() {
        ekb = ConfigLoader.init();
     
    }

    @After
    public void afterMethod() {
        ekb = null;
     
    }

    @Test
    public void testFeatureExtractor() throws IOException {
        SchemaImport si = new SchemaImport(ekb);
        File file = new File("impianti risalita.csv");
        ISchema schemaOut = si.parseCSV(file);
        SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();

        List<ISchemaElement> elementsConcept = sefe.runColumnRecognizer(schemaOut.getElements());

        assertEquals(NAME_CONCEPT_URL, elementsConcept.get(0).getElementContext().getElementConcept());
        assertEquals(INFORMATION_TECHNOLOGY_CONCEPT_URL, elementsConcept.get(2).getElementContext().getElementConcept() );
        assertEquals(HOURS_CONCEPT_URL, elementsConcept.get(1).getElementContext().getElementConcept() );
        

    }

}
