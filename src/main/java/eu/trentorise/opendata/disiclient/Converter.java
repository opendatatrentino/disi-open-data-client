
package eu.trentorise.opendata.disiclient;

import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.impl.model.SearchResult;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 0.11.1
 */
public class Converter {
    
    
    private final static Logger LOG = LoggerFactory.getLogger(Converter.class);
    
    /**
     * @since 0.11.1
     */
     public static Dict mapToDict(Map<String, String> map) {
	Dict dictBuilder = new Dict();
	Iterator<?> it = map.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry pairs = (Map.Entry) it.next();
	    Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());
	    dictBuilder = dictBuilder.putTranslation(l, (String) pairs.getValue());
	}
	return dictBuilder;
    }

    /**
     * @since 0.11.1
     */
    public static Dict multimapToDict(Map<String, List<String>> map) {
	Dict dictBuilder = new Dict();
	Iterator<?> it = map.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry pairs = (Map.Entry) it.next();
	    Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());

	    List<String> vals = (List<String>) pairs.getValue();

	    dictBuilder = dictBuilder.putTranslation(l, vals);

	}
	return dictBuilder;
    }

    
    /**
     * @since 0.11.1
     */
    public static SearchResult makeSearchResult(it.unitn.disi.sweb.webapi.model.kb.concepts.Concept codr) {

	Dict name;

	if (codr.getName() == null) {
	    LOG.warn("Found null name in concept with id ", codr.getId(), " making search result with empty name");
	    name = new Dict();
	} else {
	    name = mapToDict(codr.getName());
	}

	String url = WebServiceURLs.conceptIDToURL(codr.getId());

	return new SearchResult(url, name);
    }

    /**
     * @since 0.11.1
     */
    public static Dict swebNamesToDict(@Nullable List<Name> names) {
	if (names == null) {
	    return new Dict();
	} else {
	    Dict dictBuilder = new Dict();
	    for (Name name : names) {
		dictBuilder = dictBuilder.merge(Converter.multimapToDict(name.getNames()));
	    }
	    return dictBuilder;
	}
    }
     
    /**
     * @since 0.11.1
     */
    public static SearchResult makeSearchResult(ComplexType cType) {
	Dict name = Converter.mapToDict(cType.getName());

	String url = WebServiceURLs.etypeIDToURL(cType.getId());

	return new SearchResult(url, name);
    }

    /**
     * @since 0.11.1
     */
    public static SearchResult makeSearchResult(it.unitn.disi.sweb.webapi.model.eb.Entity swebInstance) {

	Dict name = swebNamesToDict(swebInstance.getNames());
	String url = WebServiceURLs.entityIDToURL(swebInstance.getId());

	return new SearchResult(url, name);
    }
    
     /**
     * Converts sweb {@code instance}(s) to open entity {@code Entity}, skipping
     * sweb {@code Structure}(s)
     * 
     * @since 0.11.1
     */
    public static List<it.unitn.disi.sweb.webapi.model.eb.Entity> swebInstancesToSwebEntities(Iterable<Instance> instances) {
	List<it.unitn.disi.sweb.webapi.model.eb.Entity> entities = new ArrayList();

	for (Instance instance : instances) {
	    if (instance instanceof it.unitn.disi.sweb.webapi.model.eb.Entity) {
		it.unitn.disi.sweb.webapi.model.eb.Entity name = (it.unitn.disi.sweb.webapi.model.eb.Entity) instance;
		entities.add(name);
	    }
	}

	return entities;
    }
}
