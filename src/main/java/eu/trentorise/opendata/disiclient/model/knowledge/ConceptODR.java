package eu.trentorise.opendata.disiclient.model.knowledge;

import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
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

    public IDict getDescription() {
        Dict dict = new Dict();
        Iterator<?> it = this.description.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());
            dict = dict.putTranslation(l, (String) pairs.getValue());

        }
        return dict;
    }

    public IDict getName() {
        Dict dict = new Dict();
        Iterator<?> it = this.name.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());
            dict = dict.putTranslation(l, (String) pairs.getValue());

        }
        return dict;
    }

}
