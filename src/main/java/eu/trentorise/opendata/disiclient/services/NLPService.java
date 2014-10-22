package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.semantics.impl.model.WordSearchResult;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IMeaning;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningKind;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningStatus;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Meaning;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Word;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.semantics.services.model.IWordSearchResult;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.nlp.PipelineClient;
import it.unitn.disi.sweb.webapi.model.NLPInput;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>ì
 *
 */
public class NLPService implements INLPService {

	Logger logger = LoggerFactory.getLogger(NLPService.class);

	public List<ISemanticText> disambiguateColumns(ITableResource table,
			IResourceContext context) {
		throw new UnsupportedOperationException("Service is not supported yet.");
		// TODO implementation is required
	}

	public String guessType(List<String> cellList) {
		throw new UnsupportedOperationException("Service is not supported yet.");
		// TODO implementation is required
	}

	/**
	 * For italian text and 1st knowledge base
	 *
	 * @param texts
	 * @return
	 */    
	public List<NLText> runNlpIt(List<String> texts) { 

		PipelineClient pipClient = new PipelineClient(getClientProtocol());
		NLPInput input = new NLPInput();
		input.setText(texts);
		logger.warn("USING HARDCODED VOCABULARY ID!");
		NLText[] processedTexts = pipClient.run("NamedEntityPipeline", input, 1l);
		//		for (NLText nlext : processedText) {
		//		   System.out.println(nlext.toString());
		//		}

		return Arrays.asList(processedTexts);
	}

	public NLText runNlpIt(String nlText) { 
		return runNlpIt(Arrays.asList(nlText)).get(0);
	}

	public List<PipelineDescription> readPipelinesDescription() {
		PipelineClient pipClient = new PipelineClient(getClientProtocol());
		return pipClient.readPipelines();
	}

	private IProtocolClient getClientProtocol() {

		return WebServiceURLs.getClientProtocol();
	}

	public ISemanticText runNLP(String text) {
		return runNLP(Arrays.asList(text), null).get(0);
	}


	public ISemanticText runNLP(String text, String domainURL) {
		return runNLP(Arrays.asList(text), domainURL).get(0);
	}

	public List<ISemanticText> runNLP(List<String> texts, String domainURL) {
                List<ISemanticText> ret = new ArrayList();            
		if (WebServiceURLs.isConceptURL(domainURL)){
                    
		}
		if (WebServiceURLs.isEtypeURL(domainURL)){
			List<NLText> nlTexts = runNlpIt(texts);
			for (NLText nlText : nlTexts){
				ISemanticText semText = SemanticTextFactory.semanticText(nlText);
				//extractEntities(semText, domainURL);
				ret.add(extractEntities(semText, domainURL));
			}
			return ret;
		}
		if (domainURL == null){
			List<NLText> nlTexts = runNlpIt(texts);			
			for (NLText nlText : nlTexts){
				ret.add(SemanticTextFactory.semanticText(nlText));
			}
			return ret;
		}

		throw new UnsupportedOperationException("domain " + domainURL  + " is not supported yet."); 
	}

        
	private ISemanticText extractEntities(ISemanticText semText, String etypeURL) {
		ISemanticText textEntities = new SemanticText();
		List<String> entVocab = collectEntitiesFromMeanings(semText);
		List<String> filteredEntities = filterEntitiesByType(entVocab, etypeURL);

		List<Word> words = new ArrayList<Word>();
		for (IWord w : semText.getWords()) {

			IMeaning wsm = w.getSelectedMeaning();
			IMeaning selectedMeaning;
			MeaningStatus meaningStatus;


			if ((wsm != null && MeaningKind.ENTITY.equals(wsm.getKind()))&&(filteredEntities.contains(wsm.getURL()))) {
				selectedMeaning = wsm;
				
			} else {
				selectedMeaning = null;
			}

			List<IMeaning> filteredMeanings = new ArrayList<IMeaning>();
			for (IMeaning m : w.getMeanings()) {
				if (MeaningKind.ENTITY.equals(m.getKind())&&(m.getURL()!=null)) {
					if(filteredEntities.contains(m.getURL())){
						filteredMeanings.add(m);
					}
				}
			}
			if (selectedMeaning == null) {
				if (filteredMeanings.size() > 0) {
					meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
				} else {
					meaningStatus = null;
				}
			} else {
				meaningStatus = w.getMeaningStatus();
			}

			if (meaningStatus != null) {

				words.add(new Word(w.getStartOffset(), w.getEndOffset(),  meaningStatus, selectedMeaning, filteredMeanings));


			}
		}
		textEntities=SemanticText.copyOf(semText).with(words);
		return textEntities;
	} 

	/** Get all the entities from semantic text and returns only entities that corresponds to a given etype
	 * @param semText
	 * @return
	 */
	private List<String> collectEntitiesFromMeanings(ISemanticText semText){
		List<String> entitiesId = new ArrayList<String>();
		for (IWord w : semText.getWords()) {
			for(IMeaning mean: w.getMeanings()){
				if (MeaningKind.ENTITY.equals(mean.getKind())&&(mean.getURL()!=null)) {
				entitiesId.add(mean.getURL());
				}
			}

		}
		return entitiesId;
	}

	private List<String> filterEntitiesByType(List<String> entitiesIds, String etypeURL){
		List<String> filteredEntities = new ArrayList<String>();

		EntityService es = new EntityService();
		List<IEntity> entities = es.readEntities(entitiesIds);

		for(IEntity e:entities ){
			if(e.getEtypeURL().equals(etypeURL)){
				filteredEntities.add(e.getURL());
			}

		}
		return filteredEntities;
	}

	public List<IWordSearchResult> freeSearch(String partialName, Locale locale) {
		//logger.warn("TODO FREESEARCH NOT IMPLEMENTED, RETURNING EMPTY ARRAY!");
		List<ISearchResult> entities = new ArrayList<ISearchResult>();

		Search search = new Search( WebServiceURLs.getClientProtocol());
		entities = search.searchEntities(partialName, null, locale);

		KnowledgeService ks = new KnowledgeService();
		List<ISearchResult> concepts  = ks.searchConcepts(partialName, locale);

		List<IWordSearchResult> allSearchResult = new ArrayList<IWordSearchResult>();

		if (entities.size()>0)
		{
			for (ISearchResult en: entities){
				WordSearchResult wsr = new WordSearchResult(en.getURL(), en.getName(), MeaningKind.ENTITY);
				allSearchResult.add(wsr);
			}
		}        
		if (concepts.size()>0){
			for (ISearchResult con: concepts){
				WordSearchResult wsr = new WordSearchResult(con.getURL(), con.getName(), MeaningKind.CONCEPT);
				allSearchResult.add(wsr);

			}
		}

		return allSearchResult;
	}

}
