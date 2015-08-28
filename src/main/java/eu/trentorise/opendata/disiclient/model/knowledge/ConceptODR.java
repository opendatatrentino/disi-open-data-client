package eu.trentorise.opendata.disiclient.model.knowledge;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DictFactory;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptODR implements IConcept {

    private static final Logger logger = LoggerFactory.getLogger(ConceptODR.class.getName());

    private long id;
    private String label;
    private long globalID;
    private Map<String, String> name;
    private Map<String, String> description;
    private IProtocolClient api;

    public ConceptODR() {
    }

    public ConceptODR(Concept con) {
        this.label = con.getLabel();
        this.id = con.getId();
        this.globalID = con.getGlobalId();
        this.name = con.getName();
        this.description = con.getDescription();
    }

    public ConceptODR readConcept(long conceptId) {

        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
        Concept conc = client.readConcept(conceptId, false);

        ConceptODR conceptODR = new ConceptODR(conc);
        return conceptODR;
    }

    /**
     * Also known as readConceptGUID

     */
    public ConceptODR readConceptGlobalID(long glId) {

        ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
        logger.warn("Entity Base is 1");
        List<Concept> concepts = client.readConcepts(1L, glId, null, null, null, null);
        ConceptODR conceptODR = new ConceptODR(concepts.get(0));
        logger.warn("Only the first concept is returned. The number of returned concepts is: " + concepts.size());
        return conceptODR;
    }


    //	private List<ConceptODR> readConcepts(String label){
    //		ConceptClient client = new ConceptClient(WebServiceURLs.getClientProtocol());
    //		List <ConceptODR> conOdrList = new ArrayList<ConceptODR>();
    //		Pagination page = new Pagination();
    //		List<Concept> concList = client.readConcepts(1L, null, null, label, null, null);
    //		for (Concept con: concList){
    //		
    //			ConceptODR conceptODR = new ConceptODR(con);	
    //			conOdrList.add(conceptODR);
    //		}
    //		return conOdrList;
    //	}
    public String getLabel() {
        return label;
    }

    public IProtocolClient getApi() {
        return api;
    }

    public Long getId() {
        return this.id;
    }

    public String getURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/concepts/" + this.id;
        return url;
    }

    public Long getGUID() {
        return globalID;
    }

    public Dict getName() {
        return DictFactory.mapToDict(this.name);
    }
    
    public Dict getDescription() {
        return DictFactory.mapToDict(this.description);
    }


}
