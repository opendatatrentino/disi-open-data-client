package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;


public class Search {

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
		EntityTypeService ets = new EntityTypeService();

		for(Instance instance: instances ){
			EntityType etype =ets.getEntityType(instance.getTypeId());
			if(etype.getName().getString(TraceProvUtils.languageTagToLocale("en")).equals("Name"))
			{		
				Name name =  (Name) instance;
				names.add(name);
			}
		}

		return names;
	}


	public List<IEntity> conceptSearch(String conceptSearchQuery) {
		InstanceClient client = new InstanceClient(api);
		ConceptClient cclient = new ConceptClient(api);
		//cclient.
		
		InstanceSearchResult result = client.searchInstances(conceptSearchQuery, 1, null, null, null, null);
		List<Instance> resInstances = result.getResults();
		List<IEntity> resEntities  = convertInstancesToEntities(resInstances);
		return resEntities;
	}
	
//	public List<ISearchResult> conceptSearch(String conceptSearchQuery) {
//		InstanceClient client = new InstanceClient(api);
//		InstanceSearchResult result = client.searchInstances(conceptSearchQuery, 1, null, null, null, null);
//		List<Instance> resInstances = result.getResults();
//		List<IEntity> resEntities  = convertInstancesToEntities(resInstances);
//		return resEntities;
//	}

	/** Method converts list of SWEB instances to ODR entities 
	 * @param instances list of instances from the server
	 * @return list of entities 
	 */
	private List<IEntity> convertInstancesToEntities( List<Instance> instances){
		List<IEntity> entities = new ArrayList<IEntity>();
		EntityTypeService ets = new EntityTypeService();
		for(Instance instance: instances ){
			EntityType etype =ets.getEntityType(instance.getTypeId());
			if(etype.getName().getString(TraceProvUtils.languageTagToLocale("en")).equals("Name")){
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