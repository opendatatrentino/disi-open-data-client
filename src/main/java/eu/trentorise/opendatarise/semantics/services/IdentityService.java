package eu.trentorise.opendatarise.semantics.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import eu.trentorise.opendatarise.semantics.services.model.IDRes;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
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
				EntityODR ent = (EntityODR) en;
				EntityODR entODR = convertNameAttr(ent);
				Entity entity = entODR.convertToEntity();
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

	private EntityODR convertNameAttr(EntityODR ent) {
		List<Attribute> attrs = ent.getAttributes();
		EntityService enServ= new EntityService();

		for (Attribute atr : attrs){
			if (atr.getDefinitionId()==64){
				
				attrs.remove(atr);
				Name nm =(Name) atr.getValues().get(0).getValue();
				String nameSt = (String) nm.getAttributes().get(0).getValues().get(0).getValue();
				//String nameSt = nm.getNames().get("it").get(0);
				
				Search search = new Search(WebServiceURLs.getClientProtocol());
				List<Name> foundNames = search.nameSearch(nameSt);
				System.out.println("Found Names:"+foundNames.size());
				IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
				AttributeODR attr =enServ.createNameAttribute(atDef, foundNames.get(0));
				Attribute a=attr.convertToAttribute();
				attrs.add(a);
				break;
			}
		}
		
//		
//		
//		for (Attribute atr : attrs){
//			if (atr.getName().get("en").equalsIgnoreCase("Name")){
//				
//				attrs.remove(atr);
//				IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
//				AttributeODR attr =enServ.createNameAttribute(atDef, foundNames.get(0));
//				Attribute a=attr.convertToAttribute();
//				attrs.add(a);
//				break;
//			}
//		}
		
		return ent;
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
