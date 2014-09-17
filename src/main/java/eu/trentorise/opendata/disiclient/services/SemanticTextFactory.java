package eu.trentorise.opendata.disiclient.services;

import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.conceptIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.entityIDToURL;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.urlToConceptID;
import static eu.trentorise.opendata.disiclient.services.WebServiceURLs.urlToEntityID;
import it.unitn.disi.sweb.core.nlp.model.NLComplexToken;
import it.unitn.disi.sweb.core.nlp.model.NLEntityMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLMultiWord;
import it.unitn.disi.sweb.core.nlp.model.NLNamedEntity;
import it.unitn.disi.sweb.core.nlp.model.NLSenseMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLSentence;
import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.core.nlp.model.NLTextUnit;
import it.unitn.disi.sweb.core.nlp.model.NLToken;
import it.unitn.disi.sweb.webapi.model.eb.sstring.ComplexConcept;
import it.unitn.disi.sweb.webapi.model.eb.sstring.ConceptTerm;
import it.unitn.disi.sweb.webapi.model.eb.sstring.InstanceTerm;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.IMeaning;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.ISentence;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningKind;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningStatus;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Meaning;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Sentence;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Word;

/**
 * Class to convert SemanticText to/from NLText and SemanticString.
 *
 * @author David Leoni
 * @date 11 Apr 2014
 */
public class SemanticTextFactory {
        
    private final static Logger logger = LoggerFactory.getLogger(SemanticTextFactory.class);


    /**
     * TODO - it always return the lemma in English!!!
     */
    public static IDict meaningToDict(NLMeaning meaning) {
        return new Dict(meaning.getLemma());
    }


    /**
     * We support NLMultiWord and NLNamedEntity
     */
    private static boolean isUsedInComplexToken(NLToken token) {
        return token.isUsedInMultiWord() || token.isUsedInNamedEntity();
    }


    /**
     * We support NLMultiWord and NLNamedEntity
     */
    private static List<NLComplexToken> getMultiTokens(NLToken token) {
        List<NLComplexToken> ret = new ArrayList();
        if (token.isUsedInMultiWord()) {
            ret.addAll(token.getMultiWords());
        }
        if (token.isUsedInNamedEntity()) {
            ret.addAll(token.getNamedEntities());
        }
        return ret;
    }

    private static MeaningKind getKind(NLComplexToken tok){
        if (tok instanceof NLNamedEntity){
            return MeaningKind.ENTITY;            
        } else if (tok instanceof NLMultiWord){
            return MeaningKind.CONCEPT;
        } else {
            throw new UnsupportedOperationException("NLComplexToken of class " + tok.getClass().getSimpleName() + " is not supported!");
        }
    };
    
    /**
     * Transforms multiwords and named entities into tokens and only includes
     * tokens for which startOffset and endOffset are defined.
     *
     * @param sentence
     */
    private static Sentence stSentence(NLSentence sentence) {
        int startOffset = -1;
        int endOffset = -1;
        List<IWord> words = new ArrayList<IWord>();

        Integer so = (Integer) sentence.getProp(NLTextUnit.PFX, "startOffset");
        Integer eo = (Integer) sentence.getProp(NLTextUnit.PFX, "endOffset");

        if (so == null || eo == null) {
            throw new RuntimeException("Offsets are null! startOffset = " + so + " endOffset = " + eo);
        }
        startOffset = so;
        endOffset = eo;

        int i = 0;
        while (i < sentence.getTokens().size()) {
            NLToken t = sentence.getTokens().get(i);

            if (isUsedInComplexToken(t)) {
                if (getMultiTokens(t).size() > 1) {
                    logger.warn("Found a token belonging to multiple words and/or named entities. Taking only the first one.");
                }
                if (getMultiTokens(t).isEmpty()) {
                    throw new DisiClientException("I should get back at least one complex token!");
                }
                
                NLComplexToken multiThing = getMultiTokens(t).get(0); // t.getMultiWords().get(0);

                int tokensSize = 1;
                for (int j = i + 1; j < sentence.getTokens().size(); j++) {
                    NLToken q = sentence.getTokens().get(j);
                    if (!(isUsedInComplexToken(q) && getMultiTokens(q).get(0).getId() == multiThing.getId())) {
                        break;
                    } else {
                        tokensSize += 1;
                    }
                }

                Integer mtso = (Integer) t.getProp(NLTextUnit.PFX, "sentenceStartOffset");
                Integer mteo = (Integer) sentence.getTokens().get(i + tokensSize - 1).getProp(NLTextUnit.PFX, "sentenceEndOffset");

                if (mtso == null || mteo == null) {
                    i += tokensSize;
                    continue;
                } else {
                    Set<NLMeaning> ms = new HashSet(multiThing.getMeanings());
                    
                    if (multiThing.getMeanings().isEmpty() && multiThing.getSelectedMeaning() != null){
                        ms.add(multiThing.getSelectedMeaning());
                    }
                    
                    TreeSet<IMeaning> sortedMeanings;
                    MeaningStatus meaningStatus;
                    IMeaning selectedMeaning;
                    
                    if (ms.size() > 0) {
                        sortedMeanings = makeSortedMeanings(ms);                        
                                                
                        if (sortedMeanings.first().getURL() != null){
                            meaningStatus = MeaningStatus.SELECTED;
                            selectedMeaning = sortedMeanings.first(); 
                        }  else {
                            meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
                            selectedMeaning = null;
                        }
                        
                    } else { // no meanings, but we know the kind                        
                        sortedMeanings = new TreeSet<IMeaning>();
                        sortedMeanings.add(new Meaning(null, 1.0, getKind(multiThing)));
                        meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
                        selectedMeaning = null;
                    }
                    words.add(new Word( startOffset + mtso,
                    startOffset + mteo,
                    meaningStatus, selectedMeaning, sortedMeanings));

                    i += tokensSize;
                }

            } else {
                if (t.getProp(NLTextUnit.PFX, "sentenceStartOffset") != null
                        && t.getProp(NLTextUnit.PFX, "sentenceEndOffset") != null
                        && t.getMeanings().size() > 0) {
                    words.add(stWord(t, startOffset));
                }
                i += 1;
            }
        }

        return new Sentence(startOffset, endOffset, words);
    }

    public static ISemanticText semanticText(@Nullable NLText nltext) {

        if (nltext == null) {
            return new SemanticText();
        }

        List<ISentence> sentences = new ArrayList<ISentence>();

        for (NLSentence nls : nltext.getSentences()) {
            Integer so = (Integer) nls.getProp(NLTextUnit.PFX, "startOffset");
            Integer eo = (Integer) nls.getProp(NLTextUnit.PFX, "endOffset");

            if (so != null && eo != null) {
                sentences.add(stSentence(nls));
            }
        }
        Locale locale;
        String lang = nltext.getLanguage();
        if (lang == null) {
            locale = null;
        } else {
            locale = new Locale(lang);
        }

        return new SemanticText(nltext.getText(), locale, sentences);
    }

    /**
     * Converts input semantic text into a semantic string. For each Word of
     * input semantic text a ComplexConcept holding one semantic term is
     * created.
     *
     * @param st the semantic string to convert
     * @return a semantic string representation of input semantic text
     */
    public static SemanticString semanticString(ISemanticText st) {
        List<ComplexConcept> complexConcepts = new ArrayList<ComplexConcept>();

        for (ISentence sentence : st.getSentences()) {
            for (IWord word : sentence.getWords()) {
                List<SemanticTerm> semTerms = new ArrayList<SemanticTerm>();
                List<ConceptTerm> concTerms = new ArrayList<ConceptTerm>();
                List<InstanceTerm> entityTerms = new ArrayList<InstanceTerm>();

                SemanticTerm semTerm = new SemanticTerm();

                for (IMeaning m : word.getMeanings()) {
                    double probability;
                    if (MeaningStatus.SELECTED.equals(word.getMeaningStatus())) {
                        probability = 5.0; // so we're sure selected meaning gets the highest weight
                    } else {
                        probability = m.getProbability();
                    }
                    if (MeaningKind.CONCEPT.equals(m.getKind())) {
                        ConceptTerm concTerm = new ConceptTerm();
                        concTerm.setValue(urlToConceptID(m.getURL()));

                        concTerm.setWeight(probability);
                        concTerms.add(concTerm);
                        continue;
                    }
                    if (MeaningKind.ENTITY.equals(m.getKind())) {
                        InstanceTerm entityTerm = new InstanceTerm();
                        entityTerm.setValue(urlToEntityID(m.getURL()));
                        entityTerm.setWeight(probability);
                        entityTerms.add(entityTerm);
                        continue;
                    }
                    throw new RuntimeException("Found not supported MeaningKind: " + m.getKind());
                }

                semTerm.setOffset(word.getStartOffset());
                semTerm.setText(st.getText(word));
                semTerm.setConceptTerms(concTerms);
                semTerm.setInstanceTerms(entityTerms);

                semTerms.add(semTerm);
                ComplexConcept cc = new ComplexConcept(semTerms);
                complexConcepts.add(cc);
            }
        }

        return new SemanticString(st.getText(), complexConcepts);
    }

    public static ISemanticText semanticText(@Nullable SemanticString ss) {
        if (ss == null) {
            return new SemanticText();
        }

        String text;

        if (ss.getText() == null) {
            text = "";
        } else {
            text = ss.getText();
        }

        List<ISentence> sentences = new ArrayList<ISentence>();
        List<IWord> words = new ArrayList<IWord>();

        int pos = 0;
        if (ss.getComplexConcepts() != null) {
            for (ComplexConcept cc : ss.getComplexConcepts()) {
                if (cc.getTerms() != null) {
                    for (SemanticTerm st : cc.getTerms()) {

                        // overlapping terms are ignored
                        if (st.getOffset() != null && st.getOffset() >= pos) {
                            
                            List<IMeaning> meanings = new ArrayList();
                            
                            if (st.getConceptTerms() != null) {
                                for (ConceptTerm ct : st.getConceptTerms()) {
                                    if (ct.getValue() != null) {
                                        double weight;
                                        if (ct.getWeight() == null) {
                                            weight = 1.0;
                                        } else {
                                            weight = ct.getWeight();
                                        }
                                        Long id = ct.getValue();                                        
                                        if (id != null){
                                            meanings.add(new Meaning(conceptIDToURL(id),
                                                                                    weight, MeaningKind.CONCEPT));                                            
                                        }
                                        
                                    }
                                }
                            }
                            if (st.getInstanceTerms() != null) {
                                for (InstanceTerm it : st.getInstanceTerms()) {
                                    if (it.getValue() != null) {
                                        double weight;
                                        if (it.getWeight() == null) {
                                            weight = 1.0;
                                        } else {
                                            weight = it.getWeight();
                                        }
                                        Long id = it.getValue();                                        
                                        if (id != null){                                        
                                            meanings.add(new Meaning(entityIDToURL(it.getValue()), weight, MeaningKind.ENTITY));
                                        }
                                    }

                                }
                            }
                            if (meanings.size() > 0) {                                
                                
                                IMeaning selectedMeaning = Meaning.disambiguate(meanings);
                                MeaningStatus meaningStatus;
                                if (selectedMeaning == null) {
                                    meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
                                } else {
                                    meaningStatus = MeaningStatus.SELECTED;
                                }
                                words.add(new Word(st.getOffset(),
                                        st.getOffset() + st.getText().length(),
                                        meaningStatus,
                                        selectedMeaning,
                                        meanings
                                ));
                                pos = st.getOffset() + st.getText().length();
                            }
                        }
                    }
                }
            }
        }

        sentences.add(new Sentence(0, text.length(), words));

        return new SemanticText(text, null, sentences);
    }

    /**
     * Returns a sorted set according to the probability of provided
     * meanings. First element has the highest probability.

     */
    private static TreeSet<IMeaning> makeSortedMeanings(Set<? extends NLMeaning> meanings) {
        TreeSet<IMeaning> ts = new TreeSet<IMeaning>(Collections.reverseOrder());
        for (NLMeaning m : meanings) {    
            MeaningKind kind = null;
            String url = null;
            Long id = null;
            if (m instanceof NLSenseMeaning){
                kind = MeaningKind.CONCEPT;
                id = ((NLSenseMeaning) m).getConceptId();
                if (id != null){
                    url = conceptIDToURL(id);
                }                
            } else if (m instanceof NLEntityMeaning){
                kind = MeaningKind.ENTITY;
                id = ((NLEntityMeaning) m).getObjectID();
                if (id != null){
                    url = entityIDToURL(id);
                }                
            } else {
                throw new IllegalArgumentException("Found an unsupported meaning type: " + m.getClass().getName());
            }
            ts.add(new Meaning(url, m.getProbability(), kind, meaningToDict(m)));
        }
        return ts;
    }


    /**
     * @param nlToken must have startOffset and endOffset a
     * throws RuntimeException
     */
    private static Word stWord(NLToken nlToken, int sentenceStartOffset) {

        Integer so = (Integer) nlToken.getProp(NLTextUnit.PFX, "sentenceStartOffset");
        Integer eo = (Integer) nlToken.getProp(NLTextUnit.PFX, "sentenceEndOffset");

        if (so == null || eo == null) {
            throw new IllegalArgumentException("Offsets within the sentence are null! sentenceStartOffset = " + so + " sentenceEndOffset = " + eo);
        }

        int startOffset = sentenceStartOffset + so;
        int endOffset = sentenceStartOffset + eo;
        TreeSet<IMeaning> meanings = makeSortedMeanings(nlToken.getMeanings());
        IMeaning selectedMeaning = Meaning.disambiguate(new ArrayList(meanings));
        MeaningStatus meaningStatus;

        if (selectedMeaning == null) {
            meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
        } else {
            meaningStatus = MeaningStatus.SELECTED;
        }

        return new Word(startOffset, endOffset, meaningStatus, selectedMeaning, meanings);
    }
}
