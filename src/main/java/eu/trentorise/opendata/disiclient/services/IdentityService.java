package eu.trentorise.opendata.disiclient.services;

import static eu.trentorise.opendata.disiclient.model.entity.EntityODR.disify;
import it.unitn.disi.sweb.webapi.client.eb.IDManagementClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.odt.IDResult;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.services.model.IDRes;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.knowledge.ISemanticText;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.model.IIDResult;


public class IdentityService implements IIdentityService {	

	private EntityODR convertNameAttr(EntityODR ent) {
		List<Attribute> attrs = ent.getAttributes();
		EntityService enServ= new EntityService();

		for (Attribute atr : attrs){
			if (atr.getDefinitionId()==64){
				List<Value> vals = atr.getValues();
				Object val = vals.iterator().next().getValue();
				String nameSt = null;
				// david: quick hack... so it accepts String in values  todo review 
				if (val instanceof Name) {                                     
					Name nm =(Name) val;
					nameSt = (String) nm.getAttributes().iterator().next().getValues().iterator().next().getValue();
				} else if (val instanceof String){
					nameSt = (String) val;
				} else if (val instanceof ISemanticText){
					nameSt = ((ISemanticText) val).getText();
				} else {
					throw new IllegalArgumentException("Found unhandled class! Value class is " + val.getClass().getSimpleName());
				}

				Search search = new Search(WebServiceURLs.getClientProtocol());
				List<Name> foundNames;
				if(nameSt.equals("")){
					foundNames = new ArrayList<Name>();
				}
				else{
					foundNames = search.nameSearch(nameSt);
				}
 				if(foundNames.size()>0)
				{
					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
					AttributeODR attr =enServ.createNameAttribute(atDef, foundNames.iterator().next());
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

			List<EntityODR> entities = new ArrayList<EntityODR>();

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

}
