package eu.trentorise.opendatarise.semantics.services.model;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;

public class IDRes  extends IDResult implements IIDResult {

	IProtocolClient api;
	IEntity entity;
	AssignmentResult asResult;

	public IDRes(IDResult result ){
		super.setEntity(result.getEntity());
		super.setResult(result.getResult());
		super.setEntitiesWithSameSwebID(result.getEntitiesWithSameSwebID());
		super.setSwebID(result.getSwebID());
	}

	public IEntity getResultEntity() {
		if (this.api==null){
			getClientProtocol();
		}
		EntityODR en = new EntityODR( getClientProtocol(), super.getEntity());
		return en;
	}

	public Set<IEntity> getEntities() {
		if (this.api==null){
			getClientProtocol();
		}
		Set<IEntity> entities = new HashSet<IEntity>();
		Set<Entity> ients =super.getEntitiesWithSameSwebID();
		for (Entity en:ients){
			EntityODR e = new EntityODR( this.api, en);
			entities.add(e);
		}
		return entities;
	}

	public AssignmentResult getAssignmentResult() {
		switch (super.getResult()) {
		case ID_NEW:
			return AssignmentResult.NEW;
		case ID_REUSE:
			return AssignmentResult.REUSE;
		default:
			return AssignmentResult.MISSING;
		}
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(new Locale("all"), "opendata.disi.unitn.it", 8080);
		return api;
	}

	public Long getGUID() {
		
		return super.getSwebID();
	}
}
