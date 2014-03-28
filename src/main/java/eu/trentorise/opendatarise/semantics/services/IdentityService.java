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

public class IdentityService implements IIdentityService {


	public List<IDResult> assignID(List<IEntity> ientities) {
		IDManagementClient idManCl = new IDManagementClient(getClientProtocol());

		List<Entity> entities = new ArrayList<Entity>();

		for(IEntity en: ientities){
			EntityODR ent= (EntityODR) en;
			Entity entity = ent.convertToEntity();
			entities.add(entity);	
			
		}

		List<IDResult> results = idManCl.assignIdentifier(entities, 0);

		return results;
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

	public Long createGUID(IEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IIDResult> assignGUID(List<IEntity> entities, int numCandidates) {
		// TODO Auto-generated method stub
		return null;
	}

}
