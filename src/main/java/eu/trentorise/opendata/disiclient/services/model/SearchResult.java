package eu.trentorise.opendata.disiclient.services.model;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DictFactory;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Todo this currently makes sense only for etypes. According to openentity API
 * 0.21.0 should also work for entity and concept search results.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 */
public class SearchResult implements ISearchResult {

    private Long id;
    private Map<String, String> name;
    private Dict dict;
    private String url;

    public SearchResult(ComplexType cType) {
        this.id = cType.getId();
        this.name = cType.getName();
        this.url = WebServiceURLs.etypeIDToURL(cType.getId());
    }

    public SearchResult(ConceptODR codr) {
        this.id = codr.getId();
        this.dict = codr.getName();
        this.url = WebServiceURLs.conceptIDToURL(codr.getId());

    }

    public SearchResult(Entity instance) {
        this.id = instance.getId();
        Map<String, List<String>> names = instance.getNames().iterator().next().getNames();
        this.dict = DictFactory.multimapToDict(names);
        this.url = WebServiceURLs.entityIDToURL(instance.getId());
    }

    public SearchResult() {
    }

    public String getURL() {
        return this.url;
    }

    public Dict getName() {
        if (dict != null) {
            return dict;
        } else {
            dict = DictFactory.mapToDict(this.name);
            return dict;
        }
    }
    
}
