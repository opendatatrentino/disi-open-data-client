package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import static eu.trentorise.opendata.disiclient.model.entity.EntityODR.disify;
import eu.trentorise.opendata.disiclient.services.model.IDRes;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;
import java.util.ArrayList;
import java.util.List;


public class IdentityService implements IIdentityService {	

	private EntityODR convertNameAttr(EntityODR ent) {
		List<Attribute> attrs = ent.getAttributes();
		EntityService enServ= new EntityService();

		for (Attribute atr : attrs){
			if (atr.getDefinitionId()==64){

				Object val = atr.getValues().get(0).getValue();
				String nameSt = null;

				// david: quick hack... so it accepts String in values  todo review 
				if (val instanceof Name) {                                     
					Name nm =(Name) val;
					nameSt = (String) nm.getAttributes().get(0).getValues().get(0).getValue();
				} else if (val instanceof String){
					nameSt = (String) val;
				} else if (val instanceof ISemanticText){
					nameSt = ((ISemanticText) val).getText();
				} else {
					throw new IllegalArgumentException("Found unhandled class! Value class is " + val.getClass().getSimpleName());
				}

				//String nameSt = nm.getNames().get("it").get(0);
				Search search = new Search(WebServiceURLs.getClientProtocol());
				List<Name> foundNames;
				if(nameSt.equals("")){
					foundNames = new ArrayList<Name>();
				}
				else{
					foundNames = search.nameSearch(nameSt);
				}
				//	System.out.println("Found Names:"+foundNames.size());
				if(foundNames.size()>0)
				{
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr =enServ.createNameAttribute(atDef, foundNames.get(0));
					Attribute a=attr.convertToAttribute();


					attrs.remove(atr);

					attrs.add(a);
					break;}
				else {
					break;
				}
			}
		}


		return ent;
	}

	public List<IIDResult> assignURL(List<IEntity> iEntities, int numCandidates) {            

		if (iEntities == null) {
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		}
		if (iEntities.size() == 0) {
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			return idResults;
		} else {

			List<EntityODR> entities = new ArrayList();

			for (IEntity ie : iEntities){				
				                             if (ie instanceof EntityODR){
				                                 entities.add((EntityODR) ie);
				                             } else {
				                            	 entities.add(disify(ie, true));
				                             }
			}


			IDManagementClient idManCl = new IDManagementClient(WebServiceURLs.getClientProtocol());
			List<Entity> resEntities = new ArrayList<Entity>();
			for (IEntity en : entities) {
				EntityODR ent = (EntityODR) en;
				EntityODR entODR = convertNameAttr(ent);
				Entity entity = entODR.convertToEntity();
				checkPartOF(entity);
				resEntities.add(entity);

			}
			List<IDResult> results = idManCl.assignIdentifier(resEntities, 0);
			List<IIDResult> idResults = new ArrayList<IIDResult>();
			for (IDResult res : results) {
				IDRes idRes = new IDRes(res);
				idResults.add(idRes);
			}
			return idResults;
		}
	}


	private Entity checkPartOF(Entity entity) {
		List<Attribute> atrs = entity.getAttributes();
		for(Attribute a: atrs){
			if(a.getValues().get(0).getValue() instanceof EntityODR){

				AttributeODR at = createRelationalAttr (a.getDefinitionId() ,a.getValues());
				atrs.remove(a);
				atrs.add(at.convertToAttribute());
				return entity;
			}
		}

		return entity;
	}


	private AttributeODR createRelationalAttr(Long definitionId, List<Value> values) {
		Attribute a = new Attribute();
		a.setDefinitionId(definitionId);
		EntityODR e = (EntityODR) values.get(0).getValue();
		System.out.println(e);

		Entity relEn = new Entity();
		relEn.setEntityBaseId(1L);
		relEn.setId(e.getId());
		relEn.setTypeId(e.getTypeId());

		EntityService enServ= new EntityService();
		IAttributeDef ad = new AttributeDef(definitionId);
		AttributeODR at = enServ.createAttribute(ad, relEn);            
		return at;

	}

}
