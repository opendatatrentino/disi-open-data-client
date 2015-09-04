package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.disiclient.DisiClients;
import static eu.trentorise.opendata.disiclient.model.entity.EntityODR.disify;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.services.model.IDRes;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IIDResult;

public class IdentityService implements IIdentityService {

    Logger logger = LoggerFactory.getLogger(IdentityService.class);

    IdentityService(){}
    
    private EntityODR convertNameAttr(EntityODR ent) {
        List<Attribute> attrs = ent.getAttributes();
        EntityService enServ = DisiClients.getSingleton().getEntityService();

        for (Attribute atr : attrs) {

            if (atr.getDefinitionId() == 64) {
                List<Value> vals = atr.getValues();
                Object val = vals.iterator().next().getValue();
                String nameSt = null;
                // david: quick hack... so it accepts String in values  todo review 
                if (val instanceof Name) {
                    Name nm = (Name) val;
                    nameSt = (String) nm.getAttributes().iterator().next().getValues().iterator().next().getValue();
                } else if (val instanceof String) {
                    nameSt = (String) val;
                } else if (val instanceof SemText) {
                    nameSt = ((SemText) val).getText();
                } else {
                    throw new IllegalArgumentException("Found unhandled class! Value class is " + val.getClass().getSimpleName());
                }

                Search search = new Search();
                List<Name> foundNames;
                if (nameSt.equals("")) {
                    foundNames = new ArrayList();
                } else {
                    foundNames = search.nameSearch(nameSt);
                }
                if (foundNames.size() > 0) {
                    IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                    AttributeODR attr = enServ.createNameAttribute(atDef, foundNames.iterator().next());
                    Attribute a = attr.convertToAttribute();
                    attrs.remove(atr);

                    attrs.add(a);
                    break;
                } else {
                    break;
                }

            }
        }

        return ent;
    }

    @Override
    public List<IIDResult> assignURL(List<? extends IEntity> iEntities, int numCandidates) {

        if (iEntities == null) {
            List<IIDResult> idResults = new ArrayList();
            return idResults;
        }
        if (iEntities.isEmpty()) {
            List<IIDResult> idResults = new ArrayList();
            return idResults;
        } else {

            List<EntityODR> entities = new ArrayList();

            for (IEntity ie : iEntities) {
                entities.add(disify(ie, true));
            }
                       
            /*
            IDManagementClient idManCl = new IDManagementClient(SwebConfiguration.getClientProtocol());
            List<Entity> resEntities = new ArrayList<Entity>();
            for (IEntity en : entities) {
                EntityODR ent = (EntityODR) en;
                EntityODR enODRwClass = checkClassAttr(ent);

                EntityODR entODR = convertNameAttr(enODRwClass);
                Entity entity = entODR.convertToEntity();
                resEntities.add(entity);
            }
            List<IDResult> results = idManCl.assignIdentifier(resEntities, 0);
                    */
            
            List<IIDResult> idResults = new ArrayList();
            for (EntityODR en : entities) {
                
                IDRes idRes = new IDRes(en);
                idResults.add(idRes);
            }            
            return idResults;
        }
    }

    /* NOT USED FOR NOW SO NOT A PROBLEM
    private EntityODR checkClassAttr(EntityODR ent) {
        EntityTypeService es = DisiClients.getClient().getEntityTypeService();
        EntityService enServ = DisiClients.getClient().getEntityService();

        EntityType etype = es.readEntityType(ent.getTypeId());

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        Long attrDefClassAtrId = null;
        for (IAttributeDef adef : attrDefs) {

            if (adef.getName().string(Locale.ENGLISH).equalsIgnoreCase("class")) {
                attrDefClassAtrId = adef.getGUID();
                break;
            }
        }

        boolean isExistAttrClass = false;

        for (Attribute a : ent.getAttributes()) {

            if (a.getDefinitionId() == attrDefClassAtrId) {
                isExistAttrClass = true;
                break;
            }
        }

        if (!isExistAttrClass) {
            Attribute a = enServ.createClassAttribute(attrDefClassAtrId, etype.getConceptID());
            ent.getAttributes().add(a);
            logger.warn("No class attribute is assigned for the entity. Default class attribute is assigned");
        }
        return ent;
    }
    */
}
