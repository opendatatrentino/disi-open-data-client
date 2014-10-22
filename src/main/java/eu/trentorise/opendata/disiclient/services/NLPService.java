package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.model.IWordSearchResult;
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
    
    public List<ISemanticText> runNLP(List<String> texts, @Nullable String domainURL) {
        if (WebServiceURLs.isConceptURL(domainURL)){
            
        }
        if (WebServiceURLs.isEtypeURL(domainURL)){
            
        }
        if (domainURL == null){
            List<NLText> nlTexts = runNlpIt(texts);
            List<ISemanticText> ret = new ArrayList();
            for (NLText nlText : nlTexts){
                ret.add(SemanticTextFactory.semanticText(nlText));
            }
            return ret;
        }
                
        throw new UnsupportedOperationException("domain " + domainURL  + " is not supported yet."); 
    }

    public List<IWordSearchResult> freeSearch(String partialName, Locale locale) {
        logger.warn("TODO FREESEARCH NOT IMPLEMENTED, RETURNING EMPTY ARRAY!");
        return new ArrayList();
    }

}
