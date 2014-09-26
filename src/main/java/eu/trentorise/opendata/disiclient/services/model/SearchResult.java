package eu.trentorise.opendata.disiclient.services.model;

import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;

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
    private IDict dict;

    public SearchResult(ComplexType cType) {
        this.id = cType.getId();
        this.name = cType.getName();
    }

    public SearchResult(Entity instance) {
        this.id = instance.getId();
        Map<String, List<String>> names = instance.getNames().iterator().next().getNames();
        Dict dict = new Dict();
        Iterator<?> it = names.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = NLPService.languageTagToLocale((String) pairs.getKey());
            for (String s : (List<String>) pairs.getValue()) {
                dict = dict.putTranslation(l, s);
            }
        }
        this.dict = dict;

    }

    public SearchResult() {
    }

    public String getURL() {
        return WebServiceURLs.etypeIDToURL(this.id);
    }

    public IDict getName() {
        if (this.dict != null) {
            return dict;
        } else {

            Dict dict = new Dict();
            Iterator<?> it = this.name.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Locale l = NLPService.languageTagToLocale((String) pairs.getKey());
                dict = dict.putTranslation(l, (String) pairs.getValue());
            }
            return dict;
        }
    }

}
