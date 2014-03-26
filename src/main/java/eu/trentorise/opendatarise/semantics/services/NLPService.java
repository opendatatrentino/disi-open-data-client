package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.core.nlp.model.NLText;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.nlp.PipelineClient;
import it.unitn.disi.sweb.webapi.model.NLPInput;
import it.unitn.disi.sweb.webapi.model.PipelineDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.INLPService;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 21 Mar 2014
 * 
 */
public class NLPService implements INLPService {

	public void namedEntityRecognition(NLText nlText) {
		throw new UnsupportedOperationException("Named Entity Recognition service is not suported on the server yet.");
		// TODO implement NER as soon as it will be ready
	}

	public void namedEntityDisambiguate(NLText nlText) {
		throw new UnsupportedOperationException("Named Entity Disambiguation service is not suported on the server yet.");
		// TODO implement NE disambiguation as soon as it will be ready
	}

	public void wordSenseDisambiguate(NLText nlText, List<IConcept> context) {
		throw new UnsupportedOperationException("Service is not suported yet.");
		// TODO implementation is required

	}

	public List<NLText> disambiguateColumns(ITableResource table,
			IResourceContext context) {
		throw new UnsupportedOperationException("Service is not suported yet.");
		// TODO implementation is required
	}

	public String guessType(List<String> cellList) {
		throw new UnsupportedOperationException("Service is not suported yet.");
		// TODO implementation is required
	}

	public NLText runNLP(String nlText) {

		PipelineClient pipClient = new PipelineClient(getClientProtocol());
		NLPInput input = new NLPInput();
		List<String> text = new ArrayList<String>();
		text.add(nlText);
		input.setText(text);
		NLText[] processedText = pipClient.run("FullTextPipeline", input, 1l, "it");
		int i =0;
		  for (NLText nlext : processedText) {
			  i++;
              System.out.println(nlext.toString());
          }
		
		return processedText[0];
	}
	
	/** For italian text and 1st knowledge base 
	 * @param nlText
	 * @return
	 */
	public NLText runNlpIt(String nlText) {

		PipelineClient pipClient = new PipelineClient(getClientProtocol());
		NLPInput input = new NLPInput();
		List<String> text = new ArrayList<String>();
		text.add(nlText);
		input.setText(text);
		NLText[] processedText = pipClient.run("KeywordTextPipeline", input, 1l, "it");
		
		  for (NLText nlext : processedText) {
           //   System.out.println(nlext.toString());
          }
		
		return processedText[0];
	}

	
	
	public List<PipelineDescription> readPipelinesDesription(){
		PipelineClient pipClient = new PipelineClient(getClientProtocol());
		return pipClient.readPipelines();
	}


	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}


}
