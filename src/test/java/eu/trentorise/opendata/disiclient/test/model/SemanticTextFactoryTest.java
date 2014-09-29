package eu.trentorise.opendata.disiclient.test.model;

import static eu.trentorise.opendata.semantics.IntegrityChecker.checkSemanticText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import it.unitn.disi.sweb.webapi.model.eb.sstring.StringTerm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.SemanticTextFactory;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.model.knowledge.IMeaning;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.ISentence;
import eu.trentorise.opendata.semantics.model.knowledge.IWord;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningKind;
import eu.trentorise.opendata.semantics.model.knowledge.MeaningStatus;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Meaning;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Sentence;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Word;

/**
 *
 * @author David Leoni
 */
public class SemanticTextFactoryTest {

    static final long TEST_CONCEPT_1_ID = 1L;
    static final long TEST_CONCEPT_2_ID = 2L;

    @Test
    public void testConceptURLConverter() {
        String conceptURL = WebServiceURLs.conceptIDToURL(1L);
        System.out.println(conceptURL);
    }

    @Test
    public void testConceptIDConverter() {
        Long conceptID = WebServiceURLs.urlToConceptID("http://opendata.disi.unitn.it:8080/odr/concepts/1");
        System.out.println(conceptID);
    }

    @Test
    public void testEntityURLConverter() {
        String entityURL = WebServiceURLs.entityIDToURL(1L);
        System.out.println(entityURL);
    }

    @Test
    public void testEntityIDConverter() {
        Long conceptID = WebServiceURLs.urlToEntityID("http://opendata.disi.unitn.it:8080/odr/instances/1");
        System.out.println(conceptID);
    }

    /**
     * tests one token
     */
    @Test
    public void testNLTextToSemanticText_1() {
        NLText nlText = new NLText("ciao");

        ISemanticText st = SemanticTextFactory.semanticText(nlText);
        assertEquals(st.getText(), nlText.getText());
        assertEquals(st.getLocale(), null);
        IntegrityChecker.checkSemanticText(st);
    }

    /**
     * one token in sentence, multiple meanings
     */
    @Test
    public void testNLTextToSemanticText_2() {

        String text = "hello dear Refine";

        NLText nltext = new NLText(text);
        NLSentence sentence = new NLSentence(text);
        sentence.setProp(NLTextUnit.PFX, "startOffset", 0);
        sentence.setProp(NLTextUnit.PFX, "endOffset", text.length());

        String dearWord = "dear";

        Set<NLMeaning> meanings = new HashSet<NLMeaning>();

        NLSenseMeaning sm1 = new NLSenseMeaning("testLemma1", 5L, "NOUN", TEST_CONCEPT_1_ID, 4, 1, "test description");
        // score must be set manually here, although on server will be computed from senseRank and senseFrequency
        sm1.setScore(1);
        sm1.setProbability((float) (1.0 / 6.0));
        meanings.add(sm1);

        NLSenseMeaning sm2 = new NLSenseMeaning("testLemma2", 6L, "NOUN", TEST_CONCEPT_2_ID, 4, 1, "test description");
        sm2.setScore(5);
        sm2.setProbability((float) (5.0 / 6.0));
        meanings.add(sm2);

        NLToken firstToken = new NLToken(dearWord, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 6);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 10);

        sentence.addToken(firstToken);
        nltext.addSentence(sentence);

        ISemanticText st = SemanticTextFactory.semanticText(nltext);
        IntegrityChecker.checkSemanticText(st);

        assertEquals(st.getText(), text);
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 1);
        IWord word = st.getSentences().get(0).getWords().get(0);
        assertEquals(word.getMeanings().size(), 2);
        assertEquals(WebServiceURLs.conceptIDToURL(TEST_CONCEPT_2_ID), word.getSelectedMeaning().getURL());

    }

    /**
     * one token in sentence with null id for concept
     */
    @Test
    public void testNLTextToSemanticTextNoMeaning() {

        String text = "hello dear Refine";

        NLText nltext = new NLText(text);
        NLSentence sentence = new NLSentence(text);
        sentence.setProp(NLTextUnit.PFX, "startOffset", 0);
        sentence.setProp(NLTextUnit.PFX, "endOffset", text.length());

        String dearWord = "dear";

        Set<NLMeaning> meanings = new HashSet<NLMeaning>();

        NLSenseMeaning sm1 = new NLSenseMeaning("testLemma1", null, "NOUN", null, 4, 1, "test description");
        // score must be set manually here, although on server will be computed from senseRank and senseFrequency
        sm1.setScore(1);
        sm1.setProbability((float) (1.0 / 6.0));
        meanings.add(sm1);

        NLToken firstToken = new NLToken(dearWord, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 6);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 10);

        sentence.addToken(firstToken);
        nltext.addSentence(sentence);

        ISemanticText st = SemanticTextFactory.semanticText(nltext);
        IntegrityChecker.checkSemanticText(st);

        assertEquals(st.getText(), text);
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 1);
        IWord word = st.getSentences().get(0).getWords().get(0);
        assertEquals(1, word.getMeanings().size());
        assertNull(word.getSelectedMeaning());
        IMeaning m = word.getMeanings().get(0);
        assertNull(m.getURL());
        assertEquals(m.getKind(), MeaningKind.CONCEPT);
    }

    /**
     * tests two overlapping tokens
     */
    @Test
    public void testNLTextToSemanticText_3() {

        String text = "abc";

        NLText nltext = new NLText(text);
        NLSentence sentence = new NLSentence(text);
        sentence.setProp(NLTextUnit.PFX, "startOffset", 0);
        sentence.setProp(NLTextUnit.PFX, "endOffset", text.length());

        String text_1 = "ab";
        String text_2 = "bc";

        Set<NLMeaning> meanings = new HashSet<NLMeaning>();

        NLSenseMeaning sm1 = new NLSenseMeaning("testLemma1", 5L, "NOUN", TEST_CONCEPT_1_ID, 4, 1, "test description");
        // score must be set manually here, although on server will be computed from senseRank and senseFrequency
        sm1.setScore(1);
        sm1.setProbability((float) (1.0 / 6.0));
        meanings.add(sm1);

        NLSenseMeaning sm2 = new NLSenseMeaning("testLemma2", 6L, "NOUN", TEST_CONCEPT_2_ID, 4, 1, "test description");
        sm2.setScore(5);
        sm2.setProbability((float) (5.0 / 6.0));
        meanings.add(sm2);

        NLToken firstToken = new NLToken(text_1, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 0);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 2);

        sentence.addToken(firstToken);

        NLToken secondToken = new NLToken(text_2, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 1);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 3);
        sentence.addToken(secondToken);

        nltext.addSentence(sentence);

        ISemanticText st = SemanticTextFactory.semanticText(nltext);
        checkSemanticText(st);

        assertEquals(st.getText(), text);
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 1);
        IWord word = st.getSentences().get(0).getWords().get(0);
        assertEquals(word.getMeanings().size(), 2);
        assertEquals(WebServiceURLs.conceptIDToURL(TEST_CONCEPT_2_ID), word.getSelectedMeaning().getURL());
    }

    /**
     * tests two overlapping multiwords
     */
    @Test
    public void testNLTextToSemanticText_4() {

        String text = "abcd";

        NLText nltext = new NLText(text);
        NLSentence sentence = new NLSentence(text);
        sentence.setProp(NLTextUnit.PFX, "startOffset", 0);
        sentence.setProp(NLTextUnit.PFX, "endOffset", text.length());

        // mw 1 = "abc"
        // mw 2 = "bcd"
        String text_1 = "ab";
        String text_2 = "bc";
        String text_3 = "cd";

        Set<NLMeaning> meanings = new HashSet<NLMeaning>();

        NLSenseMeaning sm1 = new NLSenseMeaning("testLemma1", 5L, "NOUN", TEST_CONCEPT_1_ID, 4, 1, "test description");
        // score must be set manually here, although on server will be computed from senseRank and senseFrequency
        sm1.setScore(1);
        sm1.setProbability((float) (1.0 / 6.0));
        meanings.add(sm1);

        NLSenseMeaning sm2 = new NLSenseMeaning("testLemma2", 6L, "NOUN", TEST_CONCEPT_2_ID, 4, 1, "test description");
        sm2.setScore(5);
        sm2.setProbability((float) (5.0 / 6.0));
        meanings.add(sm2);

        NLToken firstToken = new NLToken(text_1, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 0);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 2);

        sentence.addToken(firstToken);

        NLToken secondToken = new NLToken(text_2, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 1);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 3);
        sentence.addToken(secondToken);

        List<NLToken> toksMw_1 = new ArrayList();
        toksMw_1.add(firstToken);
        toksMw_1.add(secondToken);

        List<String> toksMwString_1 = new ArrayList();
        toksMwString_1.add(text_1);
        toksMwString_1.add(text_2);

        sentence.addMultiWord(new NLMultiWord("abc", toksMw_1, toksMwString_1));

        NLToken thirdToken = new NLToken(text_3, meanings);
        firstToken.setProp(NLTextUnit.PFX, "sentenceStartOffset", 2);
        firstToken.setProp(NLTextUnit.PFX, "sentenceEndOffset", 4);

        List<NLToken> toksMw_2 = new ArrayList();
        toksMw_1.add(secondToken);
        toksMw_1.add(thirdToken);

        List<String> toksMwString_2 = new ArrayList();
        toksMwString_1.add(text_2);
        toksMwString_1.add(text_3);

        sentence.addMultiWord(new NLMultiWord("bcd", toksMw_2, toksMwString_2));
        // todo use NLNamedEntity, discuss with Gabor, Simon why NLNamedEntity 
        // constructor is different than ones for NLMultiWord

        sentence.addToken(thirdToken);

        nltext.addSentence(sentence);

        ISemanticText st = SemanticTextFactory.semanticText(nltext);
        checkSemanticText(st);

        assertEquals(st.getText(), text);
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 1);
        IWord word = st.getSentences().get(0).getWords().get(0);

        assertEquals(word.getMeanings().size(), 2);
        assertEquals(WebServiceURLs.conceptIDToURL(TEST_CONCEPT_2_ID), word.getSelectedMeaning().getURL());
    }

    @Test
    public void testSemanticStringToSemanticText_0() {
        SemanticString ss = new SemanticString();
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(ss.getText(), null);
        assertEquals(st.getText(), "");
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_1() {
        SemanticString ss = new SemanticString("ciao");
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_2() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        ccs.add(new ComplexConcept());
        SemanticString ss = new SemanticString("ciao", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_3() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("ciao", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_4() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();
        sts.add(new SemanticTerm());
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("ciao", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_5() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();
        sts.add(new SemanticTerm("dear", 6));
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("hello dear Refine", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_6() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();

        List<ConceptTerm> concTerms = new ArrayList<ConceptTerm>();
        List<InstanceTerm> entityTerms = new ArrayList<InstanceTerm>();
        List<StringTerm> stringTerms = new ArrayList<StringTerm>();

        sts.add(new SemanticTerm("dear", 6, concTerms, stringTerms, entityTerms));
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("hello dear Refine", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_7() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();

        List<ConceptTerm> concTerms = new ArrayList<ConceptTerm>();
        concTerms.add(new ConceptTerm());

        List<InstanceTerm> entityTerms = new ArrayList<InstanceTerm>();
        entityTerms.add(new InstanceTerm());

        List<StringTerm> stringTerms = new ArrayList<StringTerm>();

        sts.add(new SemanticTerm("dear", 6, concTerms, stringTerms, entityTerms));
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("hello dear Refine", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
        assertEquals(st.getSentences().get(0).getWords().size(), 0);
    }

    @Test
    public void testSemanticStringToSemanticText_8() {
        List<ComplexConcept> ccs = new ArrayList<ComplexConcept>();
        List<SemanticTerm> sts = new ArrayList<SemanticTerm>();

        List<ConceptTerm> concTerms = new ArrayList<ConceptTerm>();
        ConceptTerm ct = new ConceptTerm();
        ct.setValue(1L);
        ct.setWeight(0.1);
        concTerms.add(ct);

        List<InstanceTerm> entityTerms = new ArrayList<InstanceTerm>();
        InstanceTerm it = new InstanceTerm();
        it.setValue(2L);
        ct.setWeight(5.0);
        entityTerms.add(it);

        List<StringTerm> stringTerms = new ArrayList<StringTerm>();

        sts.add(new SemanticTerm("dear", 6, concTerms, stringTerms, entityTerms));
        ccs.add(new ComplexConcept(sts));
        SemanticString ss = new SemanticString("hello dear Refine", ccs);
        ISemanticText st = SemanticTextFactory.semanticText(ss);
        checkSemanticText(st);
        assertEquals(st.getText(), ss.getText());
        assertEquals(st.getSentences().size(), 1);
        assertEquals(st.getSentences().get(0).getWords().size(), 1);
        IWord w = st.getSentences().get(0).getWords().get(0);
        assertEquals(w.getStartOffset(), 6);
        assertEquals(w.getEndOffset(), 10);
        assertEquals(w.getMeanings().size(), 2);
        // assertNotEquals(w.getSelectedMeaning(), null);

    }

    @Test
    public void testSemanticTextToSemanticString_1() {
        ISemanticText st = new SemanticText();
        SemanticString ss = SemanticTextFactory.semanticString(st);
        assertEquals(ss.getText(), "");
        assertEquals(ss.getComplexConcepts().size(), 0);
    }

    @Test
    public void testSemanticTextToSemanticString_2() {
        ISemanticText st = new SemanticText("ciao");
        SemanticString ss = SemanticTextFactory.semanticString(st);
        assertEquals(ss.getText(), "ciao");
        assertEquals(ss.getComplexConcepts().size(), 0);
    }

    @Test
    public void testSemanticTextToSemanticString_3() {
        List<ISentence> sentences = new ArrayList<ISentence>();
        sentences.add(new Sentence(0, 4));
        ISemanticText st = new SemanticText("ciao", Locale.ITALIAN, sentences);
        SemanticString ss = SemanticTextFactory.semanticString(st);
        assertEquals(ss.getText(), "ciao");
        assertEquals(ss.getComplexConcepts().size(), 0);
    }

    @Test
    public void testSemanticTextToSemanticString_4() {
        long concID = 4;
        String text = "hello dear Refine";
        List<ISentence> sentences = new ArrayList<ISentence>();
        List<IWord> words = new ArrayList<IWord>();
        List<IMeaning> meanings = new ArrayList<IMeaning>();
        meanings.add(new Meaning(WebServiceURLs.conceptIDToURL(concID), 0.3, MeaningKind.CONCEPT));

        words.add(new Word(6, 10, MeaningStatus.SELECTED, null, meanings));
        sentences.add(new Sentence(0, text.length(), words));

        ISemanticText st = new SemanticText(text, Locale.ITALIAN, sentences);

        SemanticString ss = SemanticTextFactory.semanticString(st);

        assertEquals(ss.getText(), text);
        assertEquals(ss.getComplexConcepts().size(), 1);
        assertEquals(ss.getComplexConcepts().get(0).getTerms().size(), 1);
        assertEquals(ss.getComplexConcepts().get(0).getTerms().get(0).getConceptTerms().size(), 1);
        assertEquals((long) ss.getComplexConcepts().get(0).getTerms().get(0).getConceptTerms().get(0).getValue(),
                concID);
    }

}
