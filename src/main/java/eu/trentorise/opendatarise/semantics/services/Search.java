package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.ISearch;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;

public class Search implements ISearch {

	IProtocolClient api;
	public Search(IProtocolClient api){
		this.api=api;
	}
	public List<IEntity> searchEQL(String eqlQuery) {
		// TODO Auto-generated method stub
		return null;
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
			EntityODR entity = new EntityODR(this.api, instance);
			entities.add(entity);
		}
		return entities;
	}
}
