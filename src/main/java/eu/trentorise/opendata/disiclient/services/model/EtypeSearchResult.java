package eu.trentorise.opendata.disiclient.services.model;

import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.services.model.IEtypeSearchResult;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class EtypeSearchResult implements  IEtypeSearchResult {

	private Long id;
	private Map<String, String> name;

	public EtypeSearchResult(ComplexType cType){
		this.id=cType.getId();
		this.name=cType.getName();
	}


	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/types/"+this.id;
		return url;
	}

	public IDict getName() {
		Dict dict = new Dict();
		Iterator it = this.name.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = NLPService.languageTagToLocale((String) pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());
		}
		return dict;
	}

}
