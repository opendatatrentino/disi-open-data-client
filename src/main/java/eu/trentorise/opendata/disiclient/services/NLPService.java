package eu.trentorise.opendata.disiclient.services;

import com.google.common.collect.Lists;
import eu.trentorise.opendata.semantics.impl.model.TermSearchResult;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Term;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.semantics.services.model.ITermSearchResult;
import eu.trentorise.opendata.semtext.nltext.NLTextConverter;
import eu.trentorise.opendata.semtext.nltext.SemanticStringConverter;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.nlp.PipelineClient;
import it.unitn.disi.sweb.webapi.model.NLPInput;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final SemanticStringConverter semanticStringConverter = SemanticStringConverter.of(WebServiceURLs.getSemtextUrlMapper());

    private static final NLTextConverter nltextConverter = NLTextConverter.of(WebServiceURLs.getSemtextUrlMapper());

    private static final Logger logger = LoggerFactory.getLogger(NLPService.class);

    public List<SemText> disambiguateColumns(ITableResource table,
            IResourceContext context) {
        throw new UnsupportedOperationException("Service is not supported yet.");
        // TODO implementation is required
    }

    public String guessType(Iterable<String> cellList) {
        throw new UnsupportedOperationException("Service is not supported yet.");
        // TODO implementation is required
    }

    /**
     * For italian text and 1st knowledge base
     *
     * @param texts
     * @return
     */
    public List<NLText> runNlpItNEP(Iterable<String> texts) {

        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        NLPInput input = new NLPInput();
        input.setText(Lists.newArrayList(texts));
        logger.warn("USING HARDCODED VOCABULARY ID!");
        NLText[] processedTexts = pipClient.run("NamedEntityPipeline", input, 1l);
        //		for (NLText nlext : processedText) {
        //		   System.out.println(nlext.toString());
        //		}

        return Arrays.asList(processedTexts);
    }

    /**
     * For italian text and 1st knowledge base
     *
     * @param texts
     * @return
     */
    public List<NLText> runNlpItODH(Iterable<String> texts) {

        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        NLPInput input = new NLPInput();
        input.setText(Lists.newArrayList(texts));
        logger.warn("USING HARDCODED VOCABULARY ID!");
        NLText[] processedTexts = pipClient.run("ODHPipeline", input, 1l);
		//		for (NLText nlext : processedText) {
        //		   System.out.println(nlext.toString());
        //		}

        return Arrays.asList(processedTexts);
    }

    /**
     * For italian text and 1st knowledge base
     *
     * @param texts
     * @return
     */
    public List<NLText> runNlpItNEDW(Iterable<String> texts) {

        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        NLPInput input = new NLPInput();
        input.setText(Lists.newArrayList(texts));
        logger.warn("USING HARDCODED VOCABULARY ID!");
        NLText[] processedTexts = pipClient.run("NEDWSDPipeline", input, 1l);
		//		for (NLText nlext : processedText) {
        //		   System.out.println(nlext.toString());
        //		}

        return Arrays.asList(processedTexts);
    }

    public NLText runNlpIt(String nlText) {        
        return runNlpItNEDW(Arrays.asList(nlText)).get(0);
    }

    public List<PipelineDescription> readPipelinesDescription() {
        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        return pipClient.readPipelines();
    }

    private IProtocolClient getClientProtocol() {

        return WebServiceURLs.getClientProtocol();
    }

    public SemText runNLP(String text) {
        return runNLP(Arrays.asList(text), null).get(0);
    }

    public SemText runNLP(String text, String domainURL) {
        return runNLP(Arrays.asList(text), domainURL).get(0);
    }

    public List<SemText> runNLP(Iterable<String> texts, @Nullable String domainURL) {
        List<SemText> ret = new ArrayList();
        if (WebServiceURLs.isConceptURL(domainURL)) {
            List<NLText> nlTexts = runNlpItODH(texts);            
            for (NLText nlText : nlTexts) {
                SemText semText = nltextConverter.semText(nlText);                
                ret.add(extractEntities(semText, domainURL));
            }            
            return ret;
        }
        if (WebServiceURLs.isEtypeURL(domainURL)) {
            List<NLText> nlTexts = runNlpItNEP(texts);
            for (NLText nlText : nlTexts) {
                SemText semText = nltextConverter.semText(nlText);
                //extractEntities(semText, domainURL);
                ret.add(extractEntities(semText, domainURL));
            }
            return ret;
        }
        if (domainURL == null) {
            List<NLText> nlTexts = runNlpItNEDW(texts);
            for (NLText nlText : nlTexts) {
                ret.add(nltextConverter.semText(nlText));
            }
            return ret;
        }

        throw new UnsupportedOperationException("Domain " + domainURL + " is not supported yet.");
    }

    private SemText extractEntities(SemText semText, String etypeURL) {
        SemText textEntities;
        List<String> entVocab = collectEntitiesFromMeanings(semText);
        List<String> filteredEntities = filterEntitiesByType(entVocab, etypeURL);

        List<Term> words = new ArrayList<Term>();
        for (Term term : semText.terms()) {            

            Meaning wsm = term.getSelectedMeaning();
            Meaning selectedMeaning;
            MeaningStatus meaningStatus;

            if ((wsm != null && MeaningKind.ENTITY.equals(wsm.getKind())) && (filteredEntities.contains(wsm.getId()))) {
                selectedMeaning = wsm;

            } else {
                selectedMeaning = null;
            }

            List<Meaning> filteredMeanings = new ArrayList<Meaning>();
            for (Meaning m : term.getMeanings()) {
                if (MeaningKind.ENTITY.equals(m.getKind()) && (m.getId().length() > 0)) {
                    if (filteredEntities.contains(m.getId())) {
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
                meaningStatus = term.getMeaningStatus();
            }

            if (meaningStatus != null) {

                words.add(Term.of(term.getStart(), term.getEnd(), meaningStatus, selectedMeaning, filteredMeanings));

            }
        }
        textEntities = semText.withTerms(words);
        return textEntities;
    }

    /**
     * Get all the entities from semantic text and returns only entities that
     * corresponds to a given etype
     *
     * @param semText
     * @return
     */
    private List<String> collectEntitiesFromMeanings(SemText semText) {
        List<String> entitiesId = new ArrayList<String>();
        for (Term term : semText.terms()) {            
            for (Meaning meaning : term.getMeanings()) {
                if (MeaningKind.ENTITY.equals(meaning.getKind()) && (meaning.getId().length() > 0)) {
                    entitiesId.add(meaning.getId());
                }
            }

        }
        return entitiesId;
    }

    private List<String> filterEntitiesByType(List<String> entitiesUrls, String etypeURL) {
        List<String> filteredEntities = new ArrayList<String>();

        EntityService es = new EntityService();
        List<IEntity> entities = es.readEntities(entitiesUrls);

        for (IEntity e : entities) {
            if (e.getEtypeURL().equals(etypeURL)) {
                filteredEntities.add(e.getURL());
            }

        }
        return filteredEntities;
    }

    @Override
    public List<? extends ITermSearchResult> freeSearch(String partialName, Locale locale) {
        
        String lowerCasePartialName = partialName.toLowerCase(locale);
        
        List<ISearchResult> entities;

        Search search = new Search(WebServiceURLs.getClientProtocol());
        entities = search.searchEntities(lowerCasePartialName, null, locale);

        KnowledgeService ks = new KnowledgeService();
        List<ISearchResult> concepts = ks.searchConcepts(lowerCasePartialName, locale);

        List<TermSearchResult> allSearchResult = new ArrayList<TermSearchResult>();

        if (entities.size() > 0) {
            for (ISearchResult en : entities) {
                TermSearchResult wsr = new TermSearchResult(en.getURL(), en.getName(), MeaningKind.ENTITY);
                allSearchResult.add(wsr);
            }
        }
        if (concepts.size() > 0) {
            for (ISearchResult con : concepts) {
                TermSearchResult wsr = new TermSearchResult(con.getURL(), con.getName(), MeaningKind.CONCEPT);
                allSearchResult.add(wsr);

            }
        }

        return allSearchResult;
    }

    public static SemanticStringConverter getSemanticStringConverter() {
        return semanticStringConverter;
    }

    public static NLTextConverter getNLTextConverter() {
        return nltextConverter;
    }
}
