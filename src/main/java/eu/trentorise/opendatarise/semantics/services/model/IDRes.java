package eu.trentorise.opendatarise.semantics.services.model;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;

public class IDRes  extends IDResult implements IIDResult {

	IProtocolClient api;
	IEntity entity;
	AssignmentResult asResult;

	public IDRes(IDResult result ){

		super.setResult(result.getResult());
		super.setEntitiesWithSameSwebID(result.getEntitiesWithSameSwebID());
		super.setSwebID(result.getSwebID());



	}

	public IEntity getResultEntity() {
		if (this.api==null){
			this.api = WebServiceURLs.getClientProtocol();
		}

		if(getAssignmentResult()==AssignmentResult.REUSE)
		{
			if(this.entity==null){
			EntityService es = new EntityService(this.api);
			IEntity en = es.readEntityByGUID(getGUID());
			
			this.entity=en;
			
			return en;}
			else return this.entity;
		}

		if(getAssignmentResult()==AssignmentResult.NEW)
		{
			//	EntityService es = new EntityService(this.api);


			IEntity ent = 	entityForNewResults();
			this.entity = ent;
			return ent;
		}else{

			EntityODR e = new EntityODR();
			return e;
		}
	}

	public Set<IEntity> getEntities() { 
		if (this.api==null){
			this.api=WebServiceURLs.getClientProtocol();
		}

		Set<IEntity> entities = new HashSet<IEntity>();
		if(getAssignmentResult()==AssignmentResult.REUSE){
			entities.add(getResultEntity());

		}
		//		if (super.getEntitiesWithSameSwebID()==null)
		//		{entities.add(getResultEntity());
		//		return entities; }
		//		Set<Entity> ients =super.getEntitiesWithSameSwebID();
		//		for (Entity en:ients){
		//			EntityODR e = new EntityODR( this.api, en);
		//			entities.add(e);
		//		}
		return entities;
	}

	public AssignmentResult getAssignmentResult() {
		switch (super.getResult()) {
		case ID_NEW:
			return AssignmentResult.NEW;
		case ID_REUSE:
			return AssignmentResult.REUSE;
		case ID_KEEP:
			return AssignmentResult.REUSE;
		default:
			return AssignmentResult.MISSING;
		}
	}

	public Long getGUID() {
		return super.getSwebID();
	}

	public void setEntity(IEntity entity){

		this.entity= entity;
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/instances/new/"+super.getSwebID()+
				"?locale="+(WebServiceURLs.getClientProtocol()).getLocale();
		return url;	}


	private IEntity entityForNewResults(){
		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();
		for (Attribute atr : attrs){

			if (atr.getName().get("en").equalsIgnoreCase("Name")){
				attrs1.add(atr);

			}

			if (atr.getName().get("en").equalsIgnoreCase("Description")){
				attrs1.add(atr);

			}

			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
				AttributeDef ad = new AttributeDef(atr.getDefinitionId());
				AttributeODR attr = enServ.createAttribute(ad, createRandomFloat());
				Attribute a=attr.convertToAttribute();
				attrs1.add(a);

			}
			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				attrs1.add(atr);
			} 
			else 
				if (atr.getName().get("en").equalsIgnoreCase("Class")){
					attrs1.add(atr);
				}
		}
		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);
		en.setId(1L);
		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		long id = enServ.createEntity(ent);
		IEntity finalEn = enServ.readEntity(id);
		return ent;
	}
	private float createRandomFloat()
	{
		float minX = 50.0f;
		float maxX = 100.0f;

		Random rand = new Random();

		float finalX = rand.nextFloat() * (maxX - minX) + minX;
		return finalX;
	}
}

