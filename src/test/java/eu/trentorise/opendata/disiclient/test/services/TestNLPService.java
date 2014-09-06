//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import eu.trentorise.opendatarise.semantics.services.NLPService;
//import eu.trentorise.opendatarise.semantics.services.SemanticTextFactory;
//import it.unitn.disi.sweb.webapi.model.PipelineDescription;
//import it.unitn.disi.sweb.webapi.model.eb.sstring.ComplexConcept;
//import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
//
//import org.junit.Test;
//
//
//
//
//
//
//
//
//import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
//
//
///** Testing the client implementaion of NLP services. 
// * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
// * @date 21 Mar 2014
// * 
// */
//public class TestNLPService {
//	List<String> PRODOTTI_CERTIFICATI_DESCRIPTIONS = new ArrayList<String>(){
//		{add("Golden Delicious: forma tronco-conica oblunga e colore dal verde al giallo,"
//				+" a volte con faccetta rosata. La polpa Ã¨ croccante e succosa, con un peculiare sapore dolce-acidulo."
//				+"Red Delicous: colore rosso su fondo verde. La polpa Ã¨ pastosa con gusto dolciastro."
//				+ "Renetta Canada: forma tronco-conica o appiattita con buccia rugosa di colore giallo-verdastra. "
//				+ "A seconda dell'epoca del consumo, la polpa assume diversa consistenza e differenti sapori, da croccante e acidula a pastosa e dolce");
//		add("Olio extravergine ricavato per almeno lÂ´80% dalle cultivar Casaliva, Frantoio, Pendolino e Leccino, impiegate da sole o insieme, "
//				+ "eventualmente completate con altre varietÃ  locali.Le olive vengono raccolte a mano o mediante mezzi meccanici entro il 15 gennaio e sottoposte"
//				+ " alla spremitura entro cinque giorni.LÂ´olio presenta colore verde con possibile presenza di riflessi dorati, profumo delicatamente erbaceo, "
//				+ "fruttato e vegetale e sapore sapido, fruttato, con fondo leggermente piccante e amarognolo con sentori di mandorla dolce.Ottimo da consumare a crudo.");
//		add("Formaggio semigrasso a pasta dura, cotta e a lenta maturazione, prodotto con latte crudo, proveniente da due mungiture, riposato e parzialmente scremato per "
//				+ "affioramento. Al latte si aggiungono siero innesto e caglio di vitello, Ã¨ salato in salamoia con soluzione satura.La forma Ã¨ cilindrica, con diametro di "
//				+ "45 cm circa, scalzo leggermente convesso con altezza di 25-30 cm, peso 35 kg. La pasta Ã¨ finemente granulosa con frattura radiale a scaglie, "
//				+ "di colore paglierino, con aroma e sapore fragrante e delicato. La crosta Ã¨ liscia di tinta scura, con spessore di 4-7 mm. Periodo di maturazione di circa"
//				+ " due anni.Zona di produzione Tutta la provincia di Trento.Riferimenti normativi Registrazione Europea Reg CE n. 1107/1996 del 12 giugno 1996, "
//				+ "pubblicato sulla Guce L. 148 del 21/06/96.");
//		add("Formaggio semigrasso da tavola, che puÃ² essere consumato giÃ  dopo tre mesi dalla produzione nella versione giovane e dopo sei nella versione stagionata. "
//				+ "Ã¨ prodotto con latte vaccino crudo ottenuto da vacche di razza Rendena (autoctona), Bruna, Grigio Alpina, Frisona e Pezzata Rossa, "
//				+ "alimentate prevalentemente con fieno.Il latte, crudo e parzialmente scremato, proviene da due mungiture, quella del mattino e quella della sera."
//				+ " La maturazione avviene in locali freschi ed aerati.La forma Ã¨ cilindrica, 30-35 cm di diametro, 8-11 cm di altezza, peso 7-10 kg.La pasta Ã¨ "
//				+ "semidura, compatta, elastica di colore bianco paglierino con occhiatura sparsa medio piccola. Il sapore Ã¨ dolce, piÃ¹ saporito con la stagionatura."
//				+ "La crosta Ã¨ elastica grigio bruno o ocra.Il periodo di produzione Ã¨ limitato, va dal 10 settembre al 30 giugno.");
//		add("Formaggio a pasta semicotta prodotto con latte di vacca.In base al sapore, al periodo di stagionatura e al tipo di lavorazione, "
//				+ "puÃ² assumere la tipologia di Asiago pressato (fresco) e Asiago dÂ´allevo (stagionato).LÂ´Asiago fresco, ha consistenza morbida "
//				+ "e gusto delicato e dolce, Ã¨ ottenuto con latte intero.LÂ´Asiago stagionato, dal gusto piÃ¹ deciso e variamente saporito a seconda "
//				+ "dellÂ´invecchiamento (dai 3 ai 12 mesi), si ottiene con latte scremato.La forma Ã¨ cilindrica, e pesa 9-12 kg.");
//		add("Formaggio a pasta filata cotta stagionato da 2-3 mesi a 6-12 mesi.La forma Ã¨ variabile: "
//				+ "a pera tronco-conica con base e calotta appiattita, a salame, a melone.La crosta Ã¨ liscia, gialla, trattata "
//				+ "con paraffina.La forma piÃ¹ comune tronco-conica Ã¨ alta 36-45 cm con insenature longitudinali dovute allo spago."
//				+ "Il peso Ã¨ variabile da 0,5 a 100 kg.La pasta Ã¨ di colore bianco-giallo, di consistenza uniforme, lÂ´occhiatura Ã¨ assente.");
//		add("Insaccato cotto, di puro suino, dalla forma cilindrica od ovale, di colore rosa e dal profumo intenso, leggermente speziato."
//				+ "Per la sua preparazione vengono impiegati solo tagli nobili, triturati adeguatamente al fine di ottenere una pasta fine.Il sapore "
//				+ "Ã¨ pieno e ben equilibrato grazie alla presenza di pezzetti di grasso di gola del suino che conferiscono maggiore dolcezza al salume."
//				+ "Una volta tagliata, la superficie si presenta vellutata e di colore rosa vivo uniforme, con profumo particolare e aromatico e gusto tipico e delicato.");
//		add("Formaggio fresco a pasta filata, molle e a fermentazione lattica. Viene impiegato latte vaccino e caglio bovino liquido."
//				+ "La filatura viene fatta con acqua calda eventualmente addizionata di sale.La forma puÃ² essere sferoidale (peso 20-250 g), "
//				+ "eventualmente con testina, o a treccia (peso 125-250 g).La crosta Ã¨ assente e presenta una pelle di consistenza tenera, superficie "
//				+ "liscia e lucente, omogenea, di color bianco latte.La pasta ha una struttura fibrosa, che al taglio rilascia liquido lattiginoso, non "
//				+ "presenta occhiatura e il colore Ã¨ omogeneo bianco latte. La consistenza Ã¨ morbida e leggermente elastica.Il sapore Ã¨ caratteristico, sapido, "
//				+ "fresco, delicatamente acidulo.Viene confezionata in involucro protettivo e commercializzata in contatto con un liquido di governo, costituito da "
//				+ "acqua con eventuale aggiunta di sale.");
//
//		}
//	};
//
//	//@Test
//	public void testGetAllPipelinesDescription(){
//		NLPService nlpService = new NLPService();
//		List<PipelineDescription> pipelines = nlpService.readPipelinesDesription();
//		//System.out.println("NLP Pipelines : ");
//		for (PipelineDescription pipeline : pipelines) {
//		//	System.out.println(pipeline.getName());
//		}
//		assertNotNull(pipelines.get(0));
//	}
//
//    // TODO REVIEW COMMENTED TEST
//	//@Test
//	public void testRunBatchNLP(){
//		
//		NLPService nlpService = new NLPService();
//
//		List<ISemanticText> output= nlpService.runNLP(PRODOTTI_CERTIFICATI_DESCRIPTIONS);
////		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());
////		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getProbability());
////
////		System.out.println(output.get(0).getSentences().get(0).getStartOffset());
////		System.out.println(output.get(0).getSentences().get(0).getEndOffset());
//
//		assertEquals("it", output.get(0).getLocale().toLanguageTag().toString());
//		assertEquals(0,output.get(0).getSentences().get(0).getStartOffset());
//		assertEquals(104,output.get(0).getSentences().get(0).getEndOffset());
//		
//	}
//
//	@Test
//	public void testRunNLP(){
//		String inputStr = "Hello World";
//		NLPService nlpService = new NLPService();
//
//		ISemanticText output= nlpService.runNLP(inputStr);
//		System.out.println(output.getLocale());
//		System.out.println(output.getText());
//		assertEquals("it",output.getLocale().toLanguageTag().toString());
//		assertEquals(inputStr,output.getText());
//		assertEquals(0,output.getSentences().get(0).getStartOffset());
//		assertEquals(11,output.getSentences().get(0).getEndOffset());
//
//		assertNotNull(output);
//	}
//
//
//	//@Test    
//	public void testNLPService() {
//		String testText = "Formaggio fresco a pasta filata, molle e a fermentazione lattica. Viene impiegato latte vaccino e caglio bovino liquido."
//				+ "La filatura viene fatta con acqua calda eventualmente addizionata di sale.La forma puÃ² essere sferoidale (peso 20-250 g), "
//				+ "eventualmente con testina, o a treccia (peso 125-250 g).La crosta Ã¨ assente e presenta una pelle di consistenza tenera, superficie "
//				+ "liscia e lucente, omogenea, di color bianco latte.La pasta ha una struttura fibrosa, che al taglio rilascia liquido lattiginoso, non "
//				+ "presenta occhiatura e il colore Ã¨ omogeneo bianco latte. La consistenza Ã¨ morbida e leggermente elastica.Il sapore Ã¨ caratteristico, sapido, "
//				+ "fresco, delicatamente acidulo.Viene confezionata in involucro protettivo e commercializzata in contatto con un liquido di governo, costituito da "
//				+ "acqua con eventuale aggiunta di sale.";
//
//		NLPService nlpService = new NLPService();
//		//from NLText to SemanticText
//		ISemanticText sText= nlpService.runNLP(testText);
//		System.out.println("Sentences1:"+sText.getSentences().size());
//		System.out.println("Words1:"+sText.getSentences().get(0).getWords().size());
//		System.out.println("Words1:"+sText.getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());
//
//
//		//from SemanticText to SemanticString
//		SemanticString sstring = SemanticTextFactory.semanticString(sText);
//		
//		System.out.println("Complex concepts:"+sstring.getComplexConcepts().size());
//		System.out.println("Complex concepts:"+sstring.getComplexConcepts().get(0).getTerms().get(0).getConceptTerms().get(0).getValue());
//
//
//		List<ComplexConcept> ccList =  sstring.getComplexConcepts();
//		//from SemanticString to SemanticText
//		ISemanticText semText = SemanticTextFactory.semanticText(sstring);
//		System.out.println("Sentences2:"+semText.getSentences().size());
//		System.out.println("Words2:"+semText.getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());
//
//
//	}
//}
//
package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.disiclient.services.SemanticTextFactory;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IMeaning;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningKind;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningStatus;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import it.unitn.disi.sweb.core.nlp.model.NLEntityMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.core.nlp.model.NLToken;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;
import it.unitn.disi.sweb.webapi.model.eb.sstring.ComplexConcept;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import org.junit.Ignore;

import org.junit.Rule;
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

    // TODO REVIEW IGNORED TEST        
    @Test
    @Ignore
    public void testGetAllPipelinesDescription() {
        NLPService nlpService = new NLPService();
        List<PipelineDescription> pipelines = nlpService.readPipelinesDescription();
        //System.out.println("NLP Pipelines : ");
        for (PipelineDescription pipeline : pipelines) {
            //	System.out.println(pipeline.getName());
        }
        assertNotNull(pipelines.get(0));
    }

    // TODO REVIEW COMMENTED TEST
    @Test
    public void testRunBatchNLP() {

        NLPService nlpService = new NLPService();

        List<ISemanticText> output = nlpService.runNLP(PRODOTTI_CERTIFICATI_DESCRIPTIONS);
        //		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getURL());
        //		System.out.println(output.get(0).getSentences().get(0).getWords().get(0).getMeanings().get(0).getProbability());
        //
        //		System.out.println(output.get(0).getSentences().get(0).getStartOffset());
        //		System.out.println(output.get(0).getSentences().get(0).getEndOffset());

        assertEquals("it", output.get(0).getLocale().toLanguageTag().toString());
        assertEquals(0, output.get(0).getSentences().get(0).getStartOffset());
        assertEquals(104, output.get(0).getSentences().get(0).getEndOffset());

    }

    @Test
    public void testRunNLP() {
        String inputStr = "Hello World";
        NLPService nlpService = new NLPService();

        ISemanticText output = nlpService.runNLP(inputStr);
        System.out.println(output.getLocale());
        System.out.println(output.getText());
        assertEquals("en", output.getLocale().toLanguageTag().toString());
        assertEquals(inputStr, output.getText());
        assertEquals(0, output.getSentences().get(0).getStartOffset());
        assertEquals(11, output.getSentences().get(0).getEndOffset());

        assertNotNull(output);
    }

    // TODO REVIEW COMMENTED TEST
    @Test
    @Ignore
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

        SemanticText singleText = new SemanticText(nlpService.runNLP("Trento"));

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
        NLPService nlpService = new NLPService();

        SemanticText singleOdrText = new SemanticText(nlpService.runNLP("Cabinovia"));
        assertEquals(1, singleOdrText.getSentences().get(0).getWords().size());

        IWord word = singleOdrText.getSentences().get(0).getWords().get(0);

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
        NLPService nlpService = new NLPService();

        ISemanticText semText = new SemanticText(nlpService.runNLP("Seggiovia ad agganciamento automatico"));

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord odrToken = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, odrToken.getMeaningStatus());
    }

    @Test
    public void testNamedEntity() {
        NLPService nlpService = new NLPService();

        ISemanticText semText = new SemanticText(nlpService.runNLP("Trento"));

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord word = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());

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

        EntityService es = new EntityService();
        IEntity ent = es.readEntity(word.getSelectedMeaning().getURL());
        assertTrue(ent != null);
        assertEquals(word.getSelectedMeaning().getURL(), ent.getURL());
    }

    /**
     * Tests tokens in named entity actually point to a named entity
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
            assertTrue(tok.getNamedEntities().get(0).getMeanings().size() > 0);
            NLEntityMeaning m = tok.getNamedEntities().get(0).getSelectedMeaning();
            assertNotNull(m);            
            
        }

        assertEquals(0, nltxt.getSentences().get(0).getMultiWords().size());
        // assertEquals(1, nltxt.getSentences().get(0).getNamedEntities().size()); // fails, finds 0

        ISemanticText semText = nlpService.runNLP(inputText);

        assertEquals(1, semText.getSentences().size());

        assertEquals(1, semText.getSentences().get(0).getWords().size());

        IWord word = semText.getSentences().get(0).getWords().get(0);

        assertEquals(MeaningStatus.SELECTED, word.getMeaningStatus());

        assertEquals(MeaningKind.ENTITY, word.getSelectedMeaning().getKind());

        EntityService es = new EntityService();
        IEntity ent = es.readEntity(word.getSelectedMeaning().getURL());
        assertTrue(ent != null);
        assertEquals(word.getSelectedMeaning().getURL(), ent.getURL());
    }

    /**
     * Stripped down version of {@link #testLongNamedEntity_2}
     */
    @Test
    public void testLongNamedEntity_3() {
        NLPService nlpService = new NLPService();

        String inputText = "provincia di Trento";

        NLText nltxt = nlpService.runNlpIt(inputText);

        assertEquals(1, nltxt.getSentences().size());
        for (NLToken tok : nltxt.getSentences().get(0).getTokens()) {
            assertTrue("tok '" + tok.getText() + "' should be used in named entity!", tok.isUsedInNamedEntity());
            assertTrue("tok '" + tok.getText() + "' is used in named entity, but has no named entities!", tok.getNamedEntities().size() > 0);
            
            assertTrue(tok.getNamedEntities().get(0).getMeanings().size() > 0); 
            NLEntityMeaning m = tok.getNamedEntities().get(0).getSelectedMeaning();
            assertNotNull(m); 
        }
    }
}
