package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.ISearchService;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;

public class Search implements ISearchService {

	IProtocolClient api;
	InstanceClient client;

	public Search(IProtocolClient api) {
		this.api = api;
		client = new InstanceClient(api);
	}

	public String[][] searchEQL(String eqlQuery) {
        throw new UnsupportedOperationException("todo to implement");
	}

	public List<List<IEntity>> search(IEntityType entityType,
			int numCandidates, List<List<IAttribute>> attributes) {

		//		InstanceClient client = new InstanceClient(api);
		//		Query query = new Query();
		//		
		//		AttributeQuery aQuery = new AttributeQuery();
		//		aQuery.setConceptId(conceptId);
		//		
		//		QueryNode queryNode = new QueryNode();
		//		queryNode.setAttributeQueries(attributeQueries);
		//		query.setQueryNode(queryNode);

		//InstanceSearchResult result = client.searchInstances(query, 1L, entityType.getGUID(), null, null, null);

		return null;
	}

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
		
		for(Instance instance: instances ){
			if(instance.getTypeId()==10){  //TODO WARNING HARDCODING entity type NAME!!!!!!!!!!!!
			Name name =  (Name) instance;
			names.add(name);
			}
		}
		
		return names;
	}
	

	public List<IEntity> conceptSearch(String conceptSearchQuery) {
		InstanceClient client = new InstanceClient(api);
		InstanceSearchResult result = client.searchInstances(conceptSearchQuery, 1, null, null, null, null);
		List<Instance> resInstances = result.getResults();
		List<IEntity> resEntities  = convertInstancesToEntities(resInstances);
		return resEntities;
	}

	/** Method converts list of SWEB instances to ODR entities 
	 * @param instances list of instances from the server
	 * @return list of entities 
	 */
	private List<IEntity> convertInstancesToEntities( List<Instance> instances){
		List<IEntity> entities = new ArrayList<IEntity>();
		for(Instance instance: instances ){
			if(instance.getTypeId()!=10){
				System.out.println(instance.getTypeId());
				Entity entity =  (Entity) instance;
				EntityODR entityODR = new EntityODR(api, entity);
				entities.add(entityODR);}
			else 
			{Name name =  (Name) instance;
			System.out.println(name.getId());
			name.getId();
			}
		}
		return entities;
	}

	public void getClientProtocol(){
		this.api =  WebServiceURLs.getClientProtocol();
	}





}
