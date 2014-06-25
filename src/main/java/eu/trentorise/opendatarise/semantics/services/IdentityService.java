package eu.trentorise.opendatarise.semantics.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendatarise.semantics.services.model.IDRes;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;

public class IdentityService implements IIdentityService {


	public List<IIDResult> assignGUID(List<IEntity> ientities) {
		if (ientities.size()==0){
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		}
		if(ientities==null){
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		} else {
			IDManagementClient idManCl = new IDManagementClient(WebServiceURLs.getClientProtocol());
			List<Entity> entities = new ArrayList<Entity>();
			for(IEntity en: ientities){
				EntityODR ent= (EntityODR) en;
				Entity entity = ent.convertToEntity();
				entities.add(entity);	

			}
			List<IDResult> results = idManCl.assignIdentifier(entities, 0);
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			for(IDResult res: results){
				IDRes idRes =new IDRes(res);
				idResults.add(idRes);
			}
			return idResults;
		}
	}

	public List<IIDResult> assignURL(List<IEntity> entities, int numCandidates) {
		if (entities.size()==0){
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		}
		if(entities==null){
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		} else {
			IDManagementClient idManCl = new IDManagementClient(WebServiceURLs.getClientProtocol());
			List<Entity> resEntities = new ArrayList<Entity>();
			for(IEntity en: entities){
				EntityODR ent= (EntityODR) en;
				Entity entity = ent.convertToEntity();
				resEntities.add(entity);	

			}
			List<IDResult> results = idManCl.assignIdentifier(resEntities, 0);
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			for(IDResult res: results){
				IDRes idRes =new IDRes(res);
				idResults.add(idRes);
			}
			return idResults;
		}
	}

}
