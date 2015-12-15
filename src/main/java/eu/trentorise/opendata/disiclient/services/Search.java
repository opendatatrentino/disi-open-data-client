package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.model.SearchResult;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;

public class Search {

    Logger logger = LoggerFactory.getLogger(Search.class);

    IProtocolClient api;
    InstanceClient client;

    public Search(IProtocolClient api) {
        this.api = api;
        client = new InstanceClient(api);
    }
    
    public Search(DisiEkb disiEkb) {
        this.api = WebServiceURLs.getClientProtocol();
        client = new InstanceClient(api);
    }

    public String[][] searchEQL(String eqlQuery) {
        throw new UnsupportedOperationException("todo to implement");
    }

    //	public List<List<IEntity>> search(IEntityType entityType,
    //			int numCandidates, List<List<IAttribute>> attributes) {
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
        InstanceClient client = new InstanceClient(api);
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(Locale.ITALIAN);
        InstanceSearchResult result = client.searchInstances(conceptSearchQuery, 1, null, null, srf, null);
        List<Instance> resInstances = result.getResults();
        List<Name> names = getNames(resInstances);
        return names;
    }

    private List<Name> getNames(List<Instance> instances) {
        List<Name> names = new ArrayList<Name>();
        EntityTypeService ets = new EntityTypeService();

        for (Instance instance : instances) {
            EntityType etype = ets.getEntityType(instance.getTypeId());
            if (etype.getName().getString(TraceProvUtils.languageTagToLocale("en")).equals("Name")) {
                Name name = (Name) instance;
                names.add(name);
            }
        }

        return names;
    }

    private List<Entity> getEntities(List<Instance> instances) {
        List<Entity> entities = new ArrayList<Entity>();
        EntityTypeService ets = new EntityTypeService();

        for (Instance instance : instances) {
            if (instance instanceof Entity) {
                Entity name = (Entity) instance;
                entities.add(name);
            }
        }

        return entities;
    }

    public List<IEntity> conceptSearch(String conceptSearchQuery) {
        InstanceClient client = new InstanceClient(api);
        SearchResultFilter srf = new SearchResultFilter();
        srf.setLocale(Locale.ITALIAN);
        //srf.setIncludeAttributesAsProperties(true);
        srf.setIncludeAttributes(true);
        InstanceSearchResult result = client.searchInstances(conceptSearchQuery, 1, null, null, srf, null);
        List<Instance> resInstances = result.getResults();
        List<IEntity> resEntities = convertInstancesToEntities(resInstances);
        return resEntities;
    }

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
//		//			List<IEntity> resEntities  = convertInstancesToEntities(resInstances);
//
//	}
    /**
     * Method converts list of SWEB instances to ODR entities
     *
     * @param instances list of instances from the server
     * @return list of entities
     */
    private List<IEntity> convertInstancesToEntities(List<Instance> instances) {
        List<IEntity> entities = new ArrayList<IEntity>();
        EntityTypeService ets = new EntityTypeService();
        for (Instance instance : instances) {
            EntityType etype = ets.getEntityType(instance.getTypeId());
            if (instance instanceof Entity) {
                System.out.println(instance.getTypeId());
                Entity entity = (Entity) instance;
                EntityODR entityODR = new EntityODR(api, entity);
                entities.add(entityODR);
            } else {
                Name name = (Name) instance;
                System.out.println(name.getId());
                name.getId();
            }
        }
        return entities;
    }

    public void getClientProtocol() {
        this.api = WebServiceURLs.getClientProtocol();
    }

}
