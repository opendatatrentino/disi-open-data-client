package eu.trentorise.opendata.disiclient.services.model;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;

public class IDRes  extends IDResult implements IIDResult {

	IProtocolClient api;
	IEntity entity;
	AssignmentResult asResult;
        
        private Logger logger = LoggerFactory.getLogger(this.getClass());

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


			//IEntity ent = 	entityForNewResults();
			//this.entity = ent;
			return this.entity;
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
			return AssignmentResult.INVALID;
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
		String url  = fullUrl+"/instances/new/"+super.getSwebID();
		return url;	}



//	private IEntity entityForNewResults(){
//		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
//
//                logger.warn("TODO REVIEW WHY IT IS READING PALAZZETTO HERE??"); 
//		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
//		List<Attribute> attrs = entity.getAttributes();
//		List<Attribute> attrs1 = new ArrayList<Attribute>();
//		for (Attribute atr : attrs){
//
//			if (atr.getName().get("en").equalsIgnoreCase("Name")){
//				attrs1.add(atr);
//
//			}
//
//			if (atr.getName().get("en").equalsIgnoreCase("Description")){
//				attrs1.add(atr);
//
//			}
//
//			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
//				AttributeDef ad = new AttributeDef(atr.getDefinitionId());
//				AttributeODR attr = enServ.createAttribute(ad, createRandomFloat());
//				Attribute a=attr.convertToAttribute();
//				attrs1.add(a);
//
//			}
//			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
//				attrs1.add(atr);
//			} 
//			else 
//				if (atr.getName().get("en").equalsIgnoreCase("Class")){
//					attrs1.add(atr);
//				}
//		}
//		Entity en = new Entity();
//		en.setEntityBaseId(1L);
//                logger.warn("HARD CODED TYPE ID");
//		en.setTypeId(12L);
//		en.setAttributes(attrs1);
//		en.setId(1L);
//		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
//		return ent;
//	}
//	private float createRandomFloat()
//	{
//		float minX = 50.0f;
//		float maxX = 100.0f;
//
//		Random rand = new Random();
//
//		float finalX = rand.nextFloat() * (maxX - minX) + minX;
//		return finalX;
//	}
}

