package eu.trentorise.opendata.disiclient.services;

import com.google.common.collect.Lists;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Term;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.semantics.services.TermSearchResult;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClients;
import eu.trentorise.opendata.semtext.nltext.NLTextConverter;
import eu.trentorise.opendata.semtext.nltext.SemanticStringConverter;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.webapi.client.nlp.ComponentClient;
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

    private static final SemanticStringConverter semanticStringConverter = SemanticStringConverter.of(SwebConfiguration.getUrlMapper());

    private static final NLTextConverter nltextConverter = NLTextConverter.of(SwebConfiguration.getUrlMapper());

    private static final Logger logger = LoggerFactory.getLogger(NLPService.class);

    NLPService(){}
    

    /**
     * For italian text and 1st knowledge base
     *
     * @param texts
     * @return
     */
    public List<NLText> runNlpItNEP(Iterable<String> texts) {

        PipelineClient pipClient = new PipelineClient(SwebConfiguration.getClientProtocol());
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

        PipelineClient pipClient = new PipelineClient(SwebConfiguration.getClientProtocol());
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

        PipelineClient pipClient = new PipelineClient(SwebConfiguration.getClientProtocol());
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
        PipelineClient pipClient = new PipelineClient(SwebConfiguration.getClientProtocol());
        return pipClient.readPipelines();
    }
    

    public SemText runNLP(String text) {
        return runNLP(Arrays.asList(text), null).get(0);
    }

    public SemText runNLP(String text, String domainURL) {
        return runNLP(Arrays.asList(text), domainURL).get(0);
    }

    @Override
    public List<SemText> runNLP(Iterable<String> texts, @Nullable String domainURL) {
        List<SemText> ret = new ArrayList();
        if (SwebConfiguration.getUrlMapper().isConceptUrl(domainURL)) {
            List<NLText> nlTexts = runNlpItODH(texts);
            for (NLText nlText : nlTexts) {
                SemText semText = nltextConverter.semText(nlText, false);
                ret.add(extractEntities(semText, domainURL));
            }
            return ret;
        }
        if (SwebConfiguration.getUrlMapper().isEtypeUrl(domainURL)) {
            List<NLText> nlTexts = runNlpItNEP(texts);
            for (NLText nlText : nlTexts) {
                SemText semText = nltextConverter.semText(nlText, false);
                //extractEntities(semText, domainURL);
                //ret.add(extractEntities(semText, domainURL));
                ret.add(semText);
            }
            return ret;
        }
        if (domainURL == null) {
            List<NLText> nlTexts = runNlpItNEDW(texts);
            for (NLText nlText : nlTexts) {
                ret.add(nltextConverter.semText(nlText, false));
            }
            return ret;
        }

        throw new UnsupportedOperationException("Domain " + domainURL + " is not supported yet.");
    }

    private SemText extractEntities(SemText semText, String etypeURL) {

        SemText textEntities;
        List<String> entVocab = collectEntitiesFromMeanings(semText);
        List<String> filteredEntities = filterEntitiesByType(entVocab, etypeURL);

        List<Term> words = new ArrayList();
        for (Term term : semText.terms()) {

            Meaning wsm = term.getSelectedMeaning();
            Meaning selectedMeaning;
            MeaningStatus meaningStatus;

            if ((wsm != null && MeaningKind.ENTITY.equals(wsm.getKind())) && (filteredEntities.contains(wsm.getId()))) {
                selectedMeaning = wsm;

            } else {
                selectedMeaning = null;
            }

            List<Meaning> filteredMeanings = new ArrayList();
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
        List<String> entitiesId = new ArrayList();
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
        List<String> filteredEntities = new ArrayList();

        
        List<IEntity> entities = DisiClients.getClient().getEntityService().readEntities(entitiesUrls);

        for (IEntity e : entities) {
            if (e.getEtypeURL().equals(etypeURL)) {
                filteredEntities.add(e.getUrl());
            }

        }
        return filteredEntities;
    }

    @Override
    public List<TermSearchResult> freeSearch(String partialName, Locale locale) {

        String lowerCasePartialName = partialName.toLowerCase(locale);

        List<SearchResult> entities;

        Search search = new Search();
        entities = search.searchEntities(lowerCasePartialName, null, locale);

        KnowledgeService ks = new KnowledgeService();
        List<SearchResult> concepts = ks.searchConcepts(lowerCasePartialName, locale);

        List<TermSearchResult> allSearchResult = new ArrayList();

        if (entities.size() > 0) {
            for (SearchResult en : entities) {
                TermSearchResult wsr = TermSearchResult.of(en.getId(), en.getName(), MeaningKind.ENTITY);
                allSearchResult.add(wsr);
            }
        }
        if (concepts.size() > 0) {
            for (SearchResult con : concepts) {
                TermSearchResult wsr = TermSearchResult.of(con.getId(), con.getName(), MeaningKind.CONCEPT);
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

    @Override
    public Locale detectLanguage(Iterable<String> inputStr) {
        ComponentClient component = new ComponentClient(SwebConfiguration.getClientProtocol());
        NLPInput input = new NLPInput();
        input.setText(Lists.newArrayList(inputStr));
        logger.warn("USING HARDCODED KB ID!");
        NLText[] processedTexts = component.run("LanguageDetector", input, 1L);

        return OdtUtils.languageTagToLocale(processedTexts[0].getLanguage());
    }
}
