import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.sweb.core.nlp.model.NLSentence;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.core.nlp.model.NLToken;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;

import org.junit.Test;



import eu.trentorise.opendatarise.semantics.services.NLPService;


/** Testing the client implementaion of NLP services. 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 21 Mar 2014
 * 
 */
public class TestNLPService {
	List<String> prodotti_certificati = new ArrayList<String>(){
		{add("Golden Delicious: forma tronco-conica oblunga e colore dal verde al giallo,"
				+" a volte con faccetta rosata. La polpa Ã¨ croccante e succosa, con un peculiare sapore dolce-acidulo."
				+"Red Delicous: colore rosso su fondo verde. La polpa Ã¨ pastosa con gusto dolciastro."
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

	//@Test
	public void testGetAllPipelinesDescription(){
		NLPService nlpService = new NLPService();
		List<PipelineDescription> pipelines = nlpService.readPipelinesDesription();
		System.out.println("NLP Pipelines : ");
		for (PipelineDescription pipeline : pipelines) {
			System.out.println(pipeline.getName());
		}
		assertNotNull(pipelines.get(0));
	}

	//@Test
	public void testRunNLP(){
		String inputStr = "Hello World";
		NLPService nlpService = new NLPService();
		NLText output= nlpService.runNLP(inputStr);
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
		NLText processedText = new NLText();
		List<NLText> processedTexts = new ArrayList<NLText>();

			processedText = nlpService.runNLP(testText);
			processedTexts.add(processedText);
			int selectedMeaningCount = 0;
			int nonEmptyMeaningsCount = 0;
			for (NLSentence sentence : processedText.getSentences()){
				for (NLToken token : sentence.getTokens()){

					if (token.getSelectedMeaning() != null){
						selectedMeaningCount += 1;
					}

					if (token.getMeanings().size() > 0){
						nonEmptyMeaningsCount += 1;
					}

				}
			};
		System.out.println(selectedMeaningCount);
		}
		
	}
