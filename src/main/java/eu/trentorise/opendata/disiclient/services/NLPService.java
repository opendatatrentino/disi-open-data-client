package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.nlp.PipelineClient;
import it.unitn.disi.sweb.webapi.model.NLPInput;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.INLPService;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>ì
 *
 */
public class NLPService implements INLPService {


    public List<ISemanticText> disambiguateColumns(ITableResource table,
            IResourceContext context) {
        throw new UnsupportedOperationException("Service is not supported yet.");
        // TODO implementation is required
    }

    public String guessType(List<String> cellList) {
        throw new UnsupportedOperationException("Service is not supported yet.");
        // TODO implementation is required
    }

    //	public ISemanticText runNLP(String nlText) {
    //
    //		PipelineClient pipClient = new PipelineClient(getClientProtocol());
    //		NLPInput input = new NLPInput();
    //		List<String> text = new ArrayList<String>();
    //		text.add(nlText);
    //		input.setText(text);
    //		NLText[] processedText = pipClient.run("FullTextPipeline", input, 1l, "it");
    //		int i =0;
    //		  for (NLText nlext : processedText) {
    //			  i++;
    //              System.out.println(nlext.toString());
    //          }
    //		
    //		return processedText[0];
    //	}
    /**
     * For italian text and 1st knowledge base
     *
     * @param nlText
     * @return
     */
    public NLText runNlpIt(String nlText) {

        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        NLPInput input = new NLPInput();
        List<String> text = new ArrayList<String>();
        text.add(nlText);
        input.setText(text);
        NLText[] processedText = pipClient.run("NamedEntityPipeline", input, 1l);
        //		for (NLText nlext : processedText) {
        //		   System.out.println(nlext.toString());
        //		}
        return processedText[0];
    }

    public List<PipelineDescription> readPipelinesDescription() {
        PipelineClient pipClient = new PipelineClient(getClientProtocol());
        return pipClient.readPipelines();
    }

    private IProtocolClient getClientProtocol() {

        return WebServiceURLs.getClientProtocol();
    }

    public List<ISemanticText> runNLP(List<String> texts) {
        List<ISemanticText> sTextList = new ArrayList<ISemanticText>();
        for (String st : texts) {
            ISemanticText stexts = runNLP(st);
            sTextList.add(stexts);
        }
        return sTextList;
    }

    public ISemanticText runNLP(String text) {
        NLText nltxt = runNlpIt(text);
        ISemanticText sText = SemanticTextFactory.semanticText(nltxt);
        return sText;
    }

    public List<ISemanticText> runNLP(List<String> texts, IConcept parentConcept) {
        //TODO consider the parentConcept parameter as soon as API will be ready
        return runNLP(texts);
    }

    public List<ISemanticText> runNER(List<String> texts) {
        throw new UnsupportedOperationException("To be implemented on the server side");
    }

}
