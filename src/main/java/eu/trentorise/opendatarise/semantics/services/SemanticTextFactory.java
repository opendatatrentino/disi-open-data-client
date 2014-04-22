package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.core.nlp.model.NLEntityMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLMeaning;
import it.unitn.disi.sweb.core.nlp.model.NLMultiWord;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

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

    public static final String CONCEPT_PREFIX = "ukc:concept/";
    public static final String ENTITY_PREFIX = "ep:entity/";

    public static String entitypediaConceptIDToURL(long ID) {
        return CONCEPT_PREFIX + Long.toString(ID);
    }

    public static long entitypediaURLToConceptID(String URL) {
        return Long.parseLong(URL.substring(CONCEPT_PREFIX.length()));
    }

    public static String entitypediaEntityIDToURL(long ID) {
        return ENTITY_PREFIX + Long.toString(ID);
    }

    public static long entitypediaURLToEntityID(String URL) {
        return Long.parseLong(URL.substring(ENTITY_PREFIX.length()));
    }

    /**
     * TODO - it always return the lemma in english!!!
     *
     * @param senseMeaning
     * @return
     */
    public static IDict senseMeaningToDict(NLSenseMeaning senseMeaning) {
        return new Dict(senseMeaning.getLemma());
    }

    /**
     * TODO - it always return the lemma in english!!!
     *
     * @param entityMeaning
     * @return
     */
    public static IDict entityMeaningToDict(NLEntityMeaning entityMeaning) {
        return new Dict(entityMeaning.getLemma());
    }

    /**
     * Transforms multiwords into tokens and only includes tokens for which
     * startOffset and endOffset are defined.
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

            if (t.isUsedInMultiWord()) {
                NLMultiWord mw = t.getMultiWords().get(0);

                int tokensSize = 1;
                for (int j = i + 1; j < sentence.getTokens().size(); j++) {
                    NLToken q = sentence.getTokens().get(j);
                    if (!(q.isUsedInMultiWord() && q.getMultiWords().get(0).getId() == mw.getId())) {
                        break;
                    } else {
                        tokensSize += 1;
                    }
                }
                // odr d 0.3i patchy solution for multiwords
                // we should also check for named entities

                Integer mwso = (Integer) t.getProp(NLTextUnit.PFX, "sentenceStartOffset");
                Integer mweo = (Integer) sentence.getTokens().get(i + tokensSize - 1).getProp(NLTextUnit.PFX, "sentenceEndOffset");

                if (mwso == null || mweo == null) {
                    i += tokensSize;
                    continue;
                } else {
                    TreeSet<IMeaning> sortedMeanings = makeSortedMeaningsFromSenses(mw.getMeanings());
                    if (sortedMeanings.size() > 0) {

                        // odr d 0.3i
                        MeaningStatus meaningStatus = MeaningStatus.SELECTED;
                        IMeaning selectedMeaning = sortedMeanings.first();   // MeaningStatus meaningStatus = selectedMeaning == null ? MeaningStatus.TO_DISAMBIGUATE : MeaningStatus.SELECTED;
                        words.add(new Word(startOffset + mwso,
                                startOffset + mweo,
                                meaningStatus, selectedMeaning, sortedMeanings));
                    }
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
     * Converts input semantic text into a semantic string. For each Word of input semantic text a
     * ComplexConcept holding one semantic term is created.
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
                        concTerm.setValue(entitypediaURLToConceptID(m.getURL()));

                        concTerm.setWeight(probability);
                        concTerms.add(concTerm);
                        continue;
                    }
                    if (MeaningKind.ENTITY.equals(m.getKind())) {
                        InstanceTerm entityTerm = new InstanceTerm();
                        entityTerm.setValue(entitypediaURLToEntityID(m.getURL()));
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

                            List<IMeaning> meanings = new ArrayList<IMeaning>();
                            if (st.getConceptTerms() != null) {
                                for (ConceptTerm ct : st.getConceptTerms()) {
                                    if (ct.getValue() != null) {
                                        double weight;
                                        if (ct.getWeight() == null) {
                                            weight = 1.0;
                                        } else {
                                            weight = ct.getWeight();
                                        }
                                        meanings.add(new Meaning(entitypediaConceptIDToURL(ct.getValue()),
                                                weight, MeaningKind.CONCEPT));
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
                                        meanings.add(new Meaning(entitypediaConceptIDToURL(it.getValue()), weight, MeaningKind.ENTITY));
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
     * meanings.First element has the highest probability.
     *
     * @param meanings
     * @return
     */
    private static List<IMeaning> makeSortedMeanings(Set<NLMeaning> meanings) {
        ArrayList<IMeaning> ret = new ArrayList<IMeaning>();
        for (NLMeaning m : meanings) {
            if (m instanceof NLSenseMeaning) {
                NLSenseMeaning nlm = ((NLSenseMeaning) m);
                // todo always using english here for the lemma 
                ret.add(new Meaning(entitypediaConceptIDToURL(nlm.getConceptId()), nlm.getProbability(), MeaningKind.CONCEPT, senseMeaningToDict(nlm)));
            } else if (m instanceof NLEntityMeaning) {
                NLEntityMeaning nlm = ((NLEntityMeaning) m);
                // todo using lemma here for the name !!! Totally wrong!!!
                ret.add(new Meaning(entitypediaEntityIDToURL(nlm.getObjectID()), nlm.getProbability(), MeaningKind.ENTITY, entityMeaningToDict(nlm)));
            } else {
                throw new RuntimeException("Meaning class not supported: " + m.getClass());
            }
        }
        Collections.sort(ret, Collections.reverseOrder());
        return ret;
    }

    private static TreeSet<IMeaning> makeSortedMeaningsFromSenses(Set<NLSenseMeaning> meanings) {
        TreeSet<IMeaning> ts = new TreeSet<IMeaning>(Collections.reverseOrder());
        for (NLSenseMeaning m : meanings) {
            ts.add(new Meaning(entitypediaConceptIDToURL(m.getConceptId()), m.getProbability(), MeaningKind.CONCEPT, senseMeaningToDict(m)));
        }
        return ts;
    }

    private static TreeSet<IMeaning> makeSortedMeaningsFromEntities(Set<NLEntityMeaning> meanings) {
        TreeSet<IMeaning> ts = new TreeSet<IMeaning>(Collections.reverseOrder());
        for (NLEntityMeaning m : meanings) {
            ts.add(new Meaning(entitypediaEntityIDToURL(m.getObjectID()), m.getProbability(), MeaningKind.ENTITY, entityMeaningToDict(m)));
        }
        return ts;
    }

    /**
     * @param nlToken must have startOffset and endOffset and at least one
     * meaning throws RuntimeException
     */
    private static Word stWord(NLToken nlToken, int sentenceStartOffset) {

        Integer so = (Integer) nlToken.getProp(NLTextUnit.PFX, "sentenceStartOffset");
        Integer eo = (Integer) nlToken.getProp(NLTextUnit.PFX, "sentenceEndOffset");

        if (so == null || eo == null) {
            throw new RuntimeException("Offsets within the sentence are null! sentenceStartOffset = " + so + " sentenceEndOffset = " + eo);
        }
        if (nlToken.getMeanings().isEmpty()) {
            throw new RuntimeException("Word must have at least one meaning!");
        }
        int startOffset = sentenceStartOffset + so;
        int endOffset = sentenceStartOffset + eo;
        List<IMeaning> meanings = makeSortedMeanings(nlToken.getMeanings());
        IMeaning selectedMeaning = Meaning.disambiguate(meanings);
        MeaningStatus meaningStatus;

        if (selectedMeaning == null) {
            meaningStatus = MeaningStatus.TO_DISAMBIGUATE;
        } else {
            meaningStatus = MeaningStatus.SELECTED;
        }

        return new Word(startOffset, endOffset, meaningStatus, selectedMeaning, meanings);
    }
}
