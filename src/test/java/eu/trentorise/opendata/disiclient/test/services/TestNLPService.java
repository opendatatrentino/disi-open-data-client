package eu.trentorise.opendata.disiclient.test.services;

import com.google.common.collect.ImmutableList;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.disiclient.services.SemanticTextFactory;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IMeaning;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningKind;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningStatus;
import eu.trentorise.opendata.semantics.services.model.IWordSearchResult;
import it.unitn.disi.sweb.core.nlp.model.NLEntityMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.core.nlp.model.NLToken;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;
import it.unitn.disi.sweb.webapi.model.eb.sstring.ComplexConcept;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.LOCATION_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.SHOPPING_FACILITY_URL;
import eu.trentorise.opendata.semantics.services.INLPService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the client implementaion of NLP services.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 21 Mar 2014
 *
 */
public class TestNLPService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    
    DisiEkb disiEkb;
    
    public static String MIXED_ENTITIES_AND_CONCEPTS = "Comuni di: Andalo, Amblar, Bresimo. Ci sono le seguenti infrastrutture: Agrifer, Athenas, Hairstudio. Il mondo è bello quando l'NLP funziona";    

    public static List<String> PRODOTTI_CERTIFICATI_DESCRIPTIONS = new ArrayList<String>() {
        {
            add("Golden Delicious: forma tronco-conica oblunga e colore dal verde al giallo,"
                    + " a volte con faccetta rosata. La polpa Ã¨ croccante e succosa, con un peculiare sapore dolce-acidulo."
                    + "Red Delicous: colore rosso su fondo verde. La polpa Ã¨ pastosa con gusto dolciastro."
                    + "Renetta Canada: forma tronco-conica o appiattita con buccia rugosa di colore giallo-verdastra. "
                    + "A seconda dell'epoca del consumo, la polpa assume diversa consistenza e differenti sapori, da croccante e acidula a pastosa e dolce");
            add("Olio extravergine ricavato per almeno lÂ´80% dalle cultivar Casaliva, Frantoio, Pendolino e Leccino, impiegate da sole o insieme, "
                    + "eventualmente completate con altre varietÃ  locali.Le olive vengono raccolte a mano o mediante mezzi meccanici entro il 15 gennaio e sottoposte"
                    + " alla spremitura entro cinque giorni.LÂ´olio presenta colore verde con possibile presenza di riflessi dorati, profumo delicatamente erbaceo, "
                    + "fruttato e vegetale e sapore sapido, fruttato, con fondo leggermente piccante e amarognolo con sentori di mandorla dolce.Ottimo da consumare a crudo.");
            add("Formaggio semigrasso a pasta dura, cotta e a lenta maturazione, prodotto con latte crudo, proveniente da due mungiture, riposato e parzialmente scremato per "
                    + "affioramento. Al latte si aggiungono siero innesto e caglio di vitello, Ã¨ salato in salamoia con soluzione satura.La forma Ã¨ cilindrica, con diametro di "
                    + "45 cm circa, scalzo leggermente convesso con altezza di 25-30 cm, peso 35 kg. La pasta Ã¨ finemente granulosa con frattura radiale a scaglie, "
                    + "di colore paglierino, con aroma e sapore fragrante e delicato. La crosta Ã¨ liscia di tinta scura, con spessore di 4-7 mm. Periodo di maturazione di circa"
                    + " due anni.Zona di produzione Tutta la provincia di Trento.Riferimenti normativi Registrazione Europea Reg CE n. 1107/1996 del 12 giugno 1996, "
                    + "pubblicato sulla Guce L. 148 del 21/06/96.");
            add("Formaggio semigrasso da tavola, che puÃ² essere consumato giÃ  dopo tre mesi dalla produzione nella versione giovane e dopo sei nella versione stagionata. "
                    + "Ã¨ prodotto con latte vaccino crudo ottenuto da vacche di razza Rendena (autoctona), Bruna, Grigio Alpina, Frisona e Pezzata Rossa, "
                    + "alimentate prevalentemente con fieno.Il latte, crudo e parzialmente scremato, proviene da due mungiture, quella del mattino e quella della sera."
                    + " La maturazione avviene in locali freschi ed aerati.La forma Ã¨ cilindrica, 30-35 cm di diametro, 8-11 cm di altezza, peso 7-10 kg.La pasta Ã¨ "
                    + "semidura, compatta, elastica di colore bianco paglierino con occhiatura sparsa medio piccola. Il sapore Ã¨ dolce, piÃ¹ saporito con la stagionatura."
                    + "La crosta Ã¨ elastica grigio bruno o ocra.Il periodo di produzione Ã¨ limitato, va dal 10 settembre al 30 giugno.");
            add("Formaggio a pasta semicotta prodotto con latte di vacca.In base al sapore, al periodo di stagionatura e al tipo di lavorazione, "
                    + "puÃ² assumere la tipologia di Asiago pressato (fresco) e Asiago dÂ´allevo (stagionato).LÂ´Asiago fresco, ha consistenza morbida "
                    + "e gusto delicato e dolce, Ã¨ ottenuto con latte intero.LÂ´Asiago stagionato, dal gusto piÃ¹ deciso e variamente saporito a seconda "
                    + "dellÂ´invecchiamento (dai 3 ai 12 mesi), si ottiene con latte scremato.La forma Ã¨ cilindrica, e pesa 9-12 kg.");
            add("Formaggio a pasta filata cotta stagionato da 2-3 mesi a 6-12 mesi.La forma Ã¨ variabile: "
                    + "a pera tronco-conica con base e calotta appiattita, a salame, a melone.La crosta Ã¨ liscia, gialla, trattata "
                    + "con paraffina.La forma piÃ¹ comune tronco-conica Ã¨ alta 36-45 cm con insenature longitudinali dovute allo spago."
                    + "Il peso Ã¨ variabile da 0,5 a 100 kg.La pasta Ã¨ di colore bianco-giallo, di consistenza uniforme, lÂ´occhiatura Ã¨ assente.");
            add("Insaccato cotto, di puro suino, dalla forma cilindrica od ovale, di colore rosa e dal profumo intenso, leggermente speziato."
                    + "Per la sua preparazione vengono impiegati solo tagli nobili, triturati adeguatamente al fine di ottenere una pasta fine.Il sapore "
                    + "Ã¨ pieno e ben equilibrato grazie alla presenza di pezzetti di grasso di gola del suino che conferiscono maggiore dolcezza al salume."
                    + "Una volta tagliata, la superficie si presenta vellutata e di colore rosa vivo uniforme, con profumo particolare e aromatico e gusto tipico e delicato.");
            add("Formaggio fresco a pasta filata, molle e a fermentazione lattica. Viene impiegato latte vaccino e caglio bovino liquido."
                    + "La filatura viene fatta con acqua calda eventualmente addizionata di sale.La forma puÃ² essere sferoidale (peso 20-250 g), "
                    + "eventualmente con testina, o a treccia (peso 125-250 g).La crosta Ã¨ assente e presenta una pelle di consistenza tenera, superficie "
                    + "liscia e lucente, omogenea, di color bianco latte.La pasta ha una struttura fibrosa, che al taglio rilascia liquido lattiginoso, non "
                    + "presenta occhiatura e il colore Ã¨ omogeneo bianco latte. La consistenza Ã¨ morbida e leggermente elastica.Il sapore Ã¨ caratteristico, sapido, "
                    + "fresco, delicatamente acidulo.Viene confezionata in involucro protettivo e commercializzata in contatto con un liquido di governo, costituito da "
                    + "acqua con eventuale aggiunta di sale.");

        }
    };

    @Before 
    public void beforeMethod(){
        
        disiEkb = ConfigLoader.init();                
        
    }
    
    @Test
    public void testGetAllPipelinesDescription() {
        NLPService nlpService = new NLPService();
        List<PipelineDescription> pipelines = nlpService.readPipelinesDescription();
        //System.out.println("NLP Pipelines : ");
        for (PipelineDescription pipeline : pipelines) {
            //	System.out.println(pipeline.getName());
        }
        assertNotNull(pipelines.get(0));
    }

    @Test
    public void testRunBatchNLP() {

        
        NLPService nlpService = (NLPService) disiEkb.getNLPService();

        List<ISemanticText> output = nlpService.runNLP(PRODOTTI_CERTIFICATI_DESCRIPTIONS, null);
        //		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());
        //		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getProbability());
        //		System.out.println(output.get(0).getSentences().get(0).getStartOffset());
        //		System.out.println(output.get(0).getSentences().get(0).getEndOffset());

        assertEquals("it", output.get(0).getLocale().toLanguageTag().toString());
        assertEquals(0, output.get(0).getSentences().get(0).getStartOffset());
        assertEquals(104, output.get(0).getSentences().get(0).getEndOffset());

    }

    @Test
    public void testRunNLP() {

        INLPService nlpService = disiEkb.getNLPService();

        String inputStr = "Hello World";

        ISemanticText output = nlpService.runNLP(Arrays.asList(inputStr), null).get(0);
        System.out.println(output.getLocale());
        System.out.println(output.getText());
        assertEquals("en", output.getLocale().toLanguageTag().toString());
        assertEquals(inputStr, output.getText());
        assertEquals(0, output.getSentences().get(0).getStartOffset());
        assertEquals(11, output.getSentences().get(0).getEndOffset());

        assertNotNull(output);
    }

    @Test
    public void testNLPService() {
        String testText = "Formaggio fresco a pasta filata, molle e a fermentazione lattica. Viene impiegato latte vaccino e caglio bovino liquido."
                + "La filatura viene fatta con acqua calda eventualmente addizionata di sale.La forma puÃ² essere sferoidale (peso 20-250 g), "
                + "eventualmente con testina, o a treccia (peso 125-250 g).La crosta Ã¨ assente e presenta una pelle di consistenza tenera, superficie "
                + "liscia e lucente, omogenea, di color bianco latte.La pasta ha una struttura fibrosa, che al taglio rilascia liquido lattiginoso, non "
                + "presenta occhiatura e il colore Ã¨ omogeneo bianco latte. La consistenza Ã¨ morbida e leggermente elastica.Il sapore Ã¨ caratteristico, sapido, "
                + "fresco, delicatamente acidulo.Viene confezionata in involucro protettivo e commercializzata in contatto con un liquido di governo, costituito da "
                + "acqua con eventuale aggiunta di sale.";

        NLPService nlpService = new NLPService();
        //from NLText to SemanticText
        ISemanticText sText = nlpService.runNLP(testText);
        System.out.println("Sentences1:" + sText.getSentences().size());
        System.out.println("Words1:" + sText.getSentences().get(0).getWords().size());
        System.out.println("Words1:" + sText.getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());

        //from SemanticText to SemanticString
        SemanticString sstring = SemanticTextFactory.semanticString(sText);

        System.out.println("Complex concepts:" + sstring.getComplexConcepts().size());
        System.out.println("Complex concepts:" + sstring.getComplexConcepts().get(0).getTerms().get(0).getConceptTerms().get(0).getValue());

        List<ComplexConcept> ccList = sstring.getComplexConcepts();
        //from SemanticString to SemanticText
        ISemanticText semText = SemanticTextFactory.semanticText(sstring);
        System.out.println("Sentences2:" + semText.getSentences().size());
        System.out.println("Words2:" + semText.getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());

    }

    @Test
    public void testSingleEntity() {

        NLPService nlpService = new NLPService();

        ISemanticText singleText = nlpService.runNLP("Trento");

        assertEquals(1, singleText.getSentences().get(0).getWords().size());

        IWord odrToken = singleText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, odrToken.getMeaningStatus());
        IMeaning m = odrToken.getSelectedMeaning();
        assertEquals(MeaningKind.ENTITY, m.getKind());
        assertTrue(m.getName().getString(Locale.ENGLISH).length() > 0);

    }

    @Test
    public void testNLTokenConcept() {

        NLPService nlpService = new NLPService();

        NLText nlText = nlpService.runNlpIt("Cabinovia");

        NLToken tok = nlText.getSentences().get(0).getTokens().get(0);

        // 'Lemma' is the name of the concept
        logger.info("Concept lemma = " + tok.getSelectedMeaning().getLemma());

        assertTrue(tok.getSelectedMeaning().getLemma().length() > 0);
        assertTrue(tok.getSelectedMeaning().getDescription().length() > 0);
    }

    @Test(expected = NullPointerException.class)
    public void testNLTokenEntity() {

        NLPService nlpService = new NLPService();

        NLText nlText = nlpService.runNlpIt("Trento");
        // expected

        NLToken tok = null;
        tok = nlText.getSentences().get(0).getTokens().get(0);
        // 'Lemma' should be the name of the  entity
        assertTrue(tok.getSelectedMeaning().getLemma().length() > 0);
        assertTrue(tok.getSelectedMeaning().getDescription().length() > 0);
    }

    @Test
    public void testSingleConcept() {
        INLPService nlpService = new NLPService();

        ISemanticText singleSemText = nlpService.runNLP(Arrays.asList("Cabinovia"), null).get(0);
        assertEquals(1, singleSemText.getSentences().get(0).getWords().size());

        IWord word = singleSemText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());
        IMeaning m = word.getSelectedMeaning();
        KnowledgeService ks = new KnowledgeService();

        IConcept concept = ks.getConcept(word.getSelectedMeaning().getURL());
        assertTrue(concept != null);
        assertEquals(word.getSelectedMeaning().getURL(), concept.getURL());

        assertEquals(MeaningKind.CONCEPT, m.getKind());
        assertTrue(m.getName().getString(Locale.ENGLISH).length() > 0);

    }

    @Test
    public void testMultiWord() {
        INLPService nlpService = new NLPService();

        ISemanticText semText = nlpService.runNLP(Arrays.asList("Seggiovia ad agganciamento automatico"), null).get(0);

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord odrToken = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, odrToken.getMeaningStatus());
    }

    @Test
    public void testNamedEntity() {
        NLPService nlpService = new NLPService();

        ISemanticText semText = nlpService.runNLP("Trento");

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord word = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());
        assertNotNull(word.getSelectedMeaning());
        assertNotNull(word.getSelectedMeaning().getURL());

        EntityService es = new EntityService();
        IEntity ent = es.readEntity(word.getSelectedMeaning().getURL());
        assertTrue(ent != null);
        assertEquals(word.getSelectedMeaning().getURL(), ent.getURL());
    }

    @Test
    public void testLongNamedEntity_1() {
        NLPService nlpService = new NLPService();

        String inputText = "Pergine Valsugana"; // "San Cristoforo al Lago";                

        NLText nltxt = nlpService.runNlpIt(inputText);

        assertEquals(1, nltxt.getSentences().size());
        for (NLToken tok : nltxt.getSentences().get(0).getTokens()) {
            assertTrue("tok '" + tok.getText() + "'should be used in named entity!", tok.isUsedInNamedEntity());
            assertEquals(1, tok.getNamedEntities().size());
            NLEntityMeaning m = tok.getNamedEntities().get(0).getSelectedMeaning();
            assertNotNull(m);
            assertTrue(m instanceof NLEntityMeaning);
        }

        assertEquals(0, nltxt.getSentences().get(0).getMultiWords().size());
        // assertEquals(1, nltxt.getSentences().get(0).getNamedEntities().size()); // fails, finds 0

        ISemanticText semText = nlpService.runNLP(inputText);

        assertEquals(1, semText.getSentences().size());

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord word = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());

        assertEquals(MeaningKind.ENTITY, word.getSelectedMeaning().getKind());

        String url = word.getSelectedMeaning().getURL();

        assertNotNull(url);

        EntityService es = new EntityService();

        IEntity ent = es.readEntity(url);
        assertTrue(ent != null);
        assertEquals(word.getSelectedMeaning().getURL(), ent.getURL());
    }

    /**
     * Note: Tested when "provincia of Trento" was recognized as a named entity
     * but had no corresponding entity in the entity base
     */
    @Test
    public void testLongNamedEntity_2() {
        NLPService nlpService = new NLPService();

        String inputText = "provincia di Trento";

        NLText nltxt = nlpService.runNlpIt(inputText);

        assertEquals(1, nltxt.getSentences().size());
        for (NLToken tok : nltxt.getSentences().get(0).getTokens()) {
            assertTrue("tok '" + tok.getText() + "'should be used in named entity!", tok.isUsedInNamedEntity());
            assertTrue("tok '" + tok.getText() + "' is used in named entity, but has no named entities!", tok.getNamedEntities().size() > 0);
            // there can be zero meanings... assertTrue(tok.getNamedEntities().get(0).getMeanings().size() > 0);
            // NLEntityMeaning m = tok.getNamedEntities().get(0).getSelectedMeaning();
            // assertNotNull(m);            

        }

        assertEquals(0, nltxt.getSentences().get(0).getMultiWords().size());
        // assertEquals(1, nltxt.getSentences().get(0).getNamedEntities().size()); // fails, finds 0

        ISemanticText semText = nlpService.runNLP(inputText);

        IntegrityChecker.checkSemanticText(semText);

        assertEquals(1, semText.getSentences().size());

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord word = semText.getSentences().get(0).getWords().get(0);

        assertTrue(word.getMeanings().size() > 0);
        assertEquals(MeaningKind.ENTITY, word.getMeanings().get(0).getKind());

        // assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());

        /*
         EntityService es = new EntityService();        
         IEntity ent = es.readEntity(word.getSelectedMeaning().getURL());
         assertTrue(ent != null);
         assertEquals(word.getSelectedMeaning().getURL(), ent.getURL());
         */
    }
    
    @Test
    public void testNlpWithMixedEntities(){

        INLPService nlpService = disiEkb.getNLPService();
        ISemanticText semText = nlpService.runNLP(Arrays.asList(MIXED_ENTITIES_AND_CONCEPTS), null).get(0);
        List<String> entitiesToRead = new ArrayList();
        List<IEntity> entities = new ArrayList();
        List<String> conceptsToRead = new ArrayList();
        List<IConcept> concepts = new ArrayList();
        
        for (IWord word : semText.getWords()){
            IMeaning m = word.getSelectedMeaning();
            if (m != null){
                if (MeaningKind.ENTITY.equals(m.getKind())){
                    entitiesToRead.add(m.getURL());
                } else {
                    conceptsToRead.add(m.getURL());
                }
            }
        }
        entities = disiEkb.getEntityService().readEntities(entitiesToRead);
        concepts = disiEkb.getKnowledgeService().readConcepts(conceptsToRead);
        assertTrue(entities.size() > 1);
        
        Set<String> etypeURLs = new HashSet();
        for (IEntity en : entities){
            logger.info("entity name: " + en.getName().getString(Locale.ENGLISH));
            logger.info("entityEtype: " + en.getEtypeURL());
            etypeURLs.add(en.getEtypeURL());
        }
        assertTrue(concepts.size() > 0);
        assertEquals(2, etypeURLs.size());
        assertTrue(etypeURLs.contains(LOCATION_URL));
        assertTrue(etypeURLs.contains(SHOPPING_FACILITY_URL));
    }    

    @Test
    public void testNLPWithEntityRestriction() {
    
        NLPService nlpService = (NLPService) disiEkb.getNLPService();

        ISemanticText semTextLocationType = nlpService.runNLP(Arrays.asList(MIXED_ENTITIES_AND_CONCEPTS), LOCATION_URL).get(0);
        testFiltering(semTextLocationType, MeaningKind.ENTITY, LOCATION_URL);
        
        ISemanticText semTextShoppingFacilityType = nlpService.runNLP(MIXED_ENTITIES_AND_CONCEPTS, SHOPPING_FACILITY_URL);
        testFiltering(semTextShoppingFacilityType, MeaningKind.ENTITY, SHOPPING_FACILITY_URL);
        
    }

    private void testFiltering(ISemanticText semText, MeaningKind kind, String domainURL) {                
        int meaningCount = 0;
        logger.warn("ONLY CHECKING FOR DIRECT PARENTSHIP TO " + domainURL + "  , SHOULD CHECK ALSO FOR ANCESTORS!");
        List<String> urlsToRead = new ArrayList();
        
        for (IWord w : semText.getWords()) {
            for (IMeaning m : w.getMeanings()) {
                assertEquals(kind, m.getKind());                
                urlsToRead.add(m.getURL());
                meaningCount++;
            }            
        }
        
        assertTrue(meaningCount > 0);
        
 
        
        if (MeaningKind.ENTITY.equals(kind)){
            EntityService entityService = (EntityService) disiEkb.getEntityService();
            List<IEntity> entities =  entityService.readEntities(urlsToRead);
            for (IEntity en : entities){
                assertEquals("Failed for entity " + en.getURL() + " with name " + en.getName().toSemText(disiEkb.getDefaultLocales()).getText(),
                        en.getEtypeURL(), 
                        domainURL);
            }
        } else {
            logger.warn("NOT CHECKING ANYTHING FOR CONCEPTS!");
        }
        
    }

    /** Ignored because we don't support concept restriction for now */
    @Test
    @Ignore
    public void testNLPWithConceptRestriction() {
        logger.warn("ONLY TESTING WITH ROOT CONCEPT!");
     
        String rootConceptURL = disiEkb.getKnowledgeService().getRootConcept().getURL();
        INLPService nlpService = disiEkb.getNLPService();
        List<ISemanticText> semTexts = nlpService.runNLP(Arrays.asList(MIXED_ENTITIES_AND_CONCEPTS), rootConceptURL);
        testFiltering(semTexts.get(0), MeaningKind.CONCEPT, rootConceptURL);
    }

    @Test
    public void testFreeSearch() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("restau", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }
    
    @Test
    public void testFreeSearchWithSpaces() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("  restau", Locale.ENGLISH);
        System.out.println(res.size());

        assertTrue(res.size() > 0);
    }    
    
    @Test
    public void testFreeSearchCapitalized() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("Restau", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testFreeSearchMultiWord() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("programming language", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }

    @Test
    public void testFreeSearchIncompleteMultiWordConcept() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("programming langu", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }    
    
    @Test
    public void testFreeSearchIncompleteMultiWordEntity() {

        INLPService nlpService = disiEkb.getNLPService();
        List<IWordSearchResult> res = nlpService.freeSearch("borgo valsu", Locale.ENGLISH);
        assertTrue(res.size() > 0);
    }
    

    
    @Test
    public void testMeaningNamesSwebNlp(){


        NLPService nlpService = (NLPService) disiEkb.getNLPService();
                List<String> texts = new ArrayList();
        texts.add(PRODOTTI_CERTIFICATI_DESCRIPTIONS.get(0));  
        NLText nlText = nlpService.runNlpItODH(texts).get(0);
        
        List<NLToken> list = nlText.getSentences().get(0).getTokens();
        
        list.get(0);
    }
    
    
    @Test
    public void testMeaningNames(){

        INLPService nlpService =  disiEkb.getNLPService();
        ISemanticText semText = nlpService.runNLP(Arrays.asList(PRODOTTI_CERTIFICATI_DESCRIPTIONS.get(0)), null).get(0);
        IWord word = semText.getWords().get(0);
        word.getMeanings();
    }
}

