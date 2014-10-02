package eu.trentorise.opendata.disiclient.services.model;

import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Todo this currently makes sense only for etypes. According to openentity API 0.21.0
 * should also work for entity and concept search results.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 */
public class SearchResult implements ISearchResult {

	private Long id;
	private Map<String, String> name;
	private IDict dict;
	private String url;


	public SearchResult(ComplexType cType) {
		this.id = cType.getId();
		this.name = cType.getName();
		this.url=WebServiceURLs.etypeIDToURL(cType.getId());
	}
	public SearchResult(ConceptODR codr) {
		this.id = codr.getId(); 
		this.dict = codr.getName();
		this.url=WebServiceURLs.conceptIDToURL(codr.getId());

	}

	public SearchResult(Entity instance) {
		this.id = instance.getId();
		Map<String,List<String>> names = instance.getNames().iterator().next().getNames();
		Dict dict = new Dict();
		Iterator<?> it = names.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());
			for (String s: (List<String>)pairs.getValue()){
				dict = dict.putTranslation(l, s);
			}
		}
		this.dict=dict;
		this.url = WebServiceURLs.entityIDToURL(instance.getId());
	}


	public SearchResult() {
	}

	public String getURL() {
		return this.url;
	}

	public IDict getName() {
		if (this.dict!=null){
			return dict;
		} else{

			Dict dict = new Dict();
			Iterator<?> it = this.name.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				Locale l = TraceProvUtils.languageTagToLocale((String) pairs.getKey());
				dict = dict.putTranslation(l, (String) pairs.getValue());
			}
			return dict;}
	}

}