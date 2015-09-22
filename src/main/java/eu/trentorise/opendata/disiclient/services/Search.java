package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;

import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.disiclient.UrlMapper;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import javax.annotation.Nullable;

public class Search {

    Logger logger = LoggerFactory.getLogger(Search.class);
    
    @Nullable
    private InstanceClient instanceClient;

    private DisiEkb ekb;
    private UrlMapper um;

    private InstanceClient getInstanceClient() {
        if (instanceClient == null) {
            instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
        }
        return instanceClient;
    }
    
    
    Search(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
        this.um = SwebConfiguration.getUrlMapper();
    }

    public String[][] searchEQL(String eqlQuery) {
        throw new UnsupportedOperationException("todo to implement");
    }

    //	public List<List<Entity>> search(Etype entityType,
    //			int numCandidates, List<List<Attr>> attributes) {
    //
    //		InstanceClient client = new InstanceClient(api);
    //		Query query = new Query();
    //		AttributeQuery aQuery = new AttributeQuery();
    //		aQuery.setConceptId(conceptId);
    //		QueryNode queryNode = new QueryNode();
    //		queryNode.setAttributeQueries(attributeQueries);
    //		query.setQueryNode(queryNode);
    //		InstanceSearchResult result = client.searchInstances(query, 1L, entityType.getGUID(), null, null, null);
    //
    //		return null;
    //	}
    public List<Name> nameSearch(String conceptSearchQuery) {        
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(Locale.ITALIAN);
        InstanceSearchResult result = getInstanceClient().searchInstances(conceptSearchQuery, 1, null, null, srf, null);
        List<Instance> resInstances = result.getResults();
        List<Name> names = getNames(resInstances);
        return names;
    }

    private List<Name> getNames(List<Instance> instances) {
        List<Name> names = new ArrayList();
        EtypeService ets = ekb.getEtypeService();

        for (Instance instance : instances) {
            ComplexType etype = ets.readSwebComplexType(instance.getTypeId());
            if (KnowledgeService.NAME_CONCEPT_ID == etype.getConceptId()) {
                Name name = (Name) instance;
                names.add(name);
            }
        }

        return names;
    }

    /**
     * Structures are skipped
     */
    private List<it.unitn.disi.sweb.webapi.model.eb.Entity> swebInstancesToSwebEntities(Iterable<Instance> instances) {
        List<it.unitn.disi.sweb.webapi.model.eb.Entity> entities = new ArrayList();        

        for (Instance instance : instances) {            
            if (instance instanceof it.unitn.disi.sweb.webapi.model.eb.Entity) {
                it.unitn.disi.sweb.webapi.model.eb.Entity name = (it.unitn.disi.sweb.webapi.model.eb.Entity) instance;
                entities.add(name);
            }
        }

        return entities;
    }

    /* dav 0.12 seems nobody calls this... 
    public List<Entity> conceptSearch(String conceptSearchQuery) {        
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(Locale.ITALIAN);
        //srf.setIncludeAttributesAsProperties(true);
        srf.setIncludeAttributes(true);
        InstanceSearchResult result = getInstanceClient().searchInstances(conceptSearchQuery, 1, null, null, srf, null);
        List<Instance> resInstances = result.getResults();
        List<Entity> resEntities = swebInstancesToSwebEntities(resInstances);
        return resEntities;
    }
    */

//	public void conceptEntitySearch(String searchQuery) {
//		InstanceClient client = new InstanceClient(api);
//		InstanceSearchResult result = client.searchInstances(searchQuery, 1, null, null, null, null);
//		List<Instance> resInstances = result.getResults();
//		Map<Long, List<ResultExplanation>>  resExpl = result.getExplanations();
//
//		for (Map.Entry<Long, List<ResultExplanation>> entry : resExpl.entrySet()){
//			System.out.println(entry);
//			System.out.println(entry.getValue().iterator().next().getResultValue());
//		}
//
//		//			List<Entity> resEntities  = convertInstancesToEntities(resInstances);
//
//	}

    public List<SearchResult> searchEntities(String partialName, String etypeUrl, Locale locale) {
        List<SearchResult> entities = new ArrayList();
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(locale);
        srf.setIncludeAttributesAsProperties(true);
        Long etype=null;
        if(etypeUrl!=null){
         etype = SwebConfiguration.getUrlMapper().etypeUrlToId(etypeUrl);
        }
        InstanceSearchResult result = getInstanceClient().searchInstances(partialName, 1, etype, null, srf, null);
        List<Instance> resInstances = result.getResults();

        List<it.unitn.disi.sweb.webapi.model.eb.Entity> ents = swebInstancesToSwebEntities(resInstances);
        for (it.unitn.disi.sweb.webapi.model.eb.Entity e : ents) {
            SearchResult res = ekb.getConverter().makeSearchResult(e);
            entities.add(res);
        }

        return entities;
    }

}
