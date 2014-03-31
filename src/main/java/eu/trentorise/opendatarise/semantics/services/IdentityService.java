package eu.trentorise.opendatarise.semantics.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.model.IDRes;

public class IdentityService implements IIdentityService {


	public List<IIDResult> assignGUID(List<IEntity> ientities) {
		if(ientities==null){
			return null;
		} else {
			IDManagementClient idManCl = new IDManagementClient(getClientProtocol());
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

	public List<IIDResult> assignGUID(List<IEntity> entities, int numCandidates) {
		// TODO Auto-generated method stub
		return null;
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}
}
