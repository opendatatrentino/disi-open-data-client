package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.services.AssignmentResult;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IdResult;
import java.util.Random;

public class IdentityService implements IIdentityService {

    Logger logger = LoggerFactory.getLogger(IdentityService.class);

    private DisiEkb ekb;
    
    IdentityService(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
    }

    private static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public List<IdResult> assignURL(List<Entity> iEntities, int numCandidates) {

        if (iEntities == null) {
            List<IdResult> idResults = new ArrayList();
            return idResults;
        }
        if (iEntities.isEmpty()) {
            List<IdResult> idResults = new ArrayList();
            return idResults;
        } else {

            List<Entity> oeEntities = new ArrayList();

            for (Entity oeEntity : iEntities) {
                oeEntities.add(oeEntity);
            }

            /*
             IDManagementClient idManCl = new IDManagementClient(SwebConfiguration.getClientProtocol());
             List<Entity> resEntities = new ArrayList<Entity>();
             for (Entity en : entities) {
             EntityODR ent = (EntityODR) en;
             EntityODR enODRwClass = checkClassAttr(ent);

             EntityODR entODR = convertNameAttr(enODRwClass);
             Entity entity = entODR.convertToEntity();
             resEntities.add(entity);
             }
             List<IDResult> results = idManCl.assignIdentifier(resEntities, 0);
             */
            List<IdResult> idResults = new ArrayList();

            for (Entity en : oeEntities) {
                IdResult.Builder b = IdResult.builder();
                String url = SwebConfiguration.getUrlMapper().entityNewIdToUrl((long) randInt(0, 10000000));
                Entity newEnt = en.withId(url);
                b.addEntities(newEnt);
                b.setAssignmentResult(AssignmentResult.NEW);
                b.setResultEntity(newEnt);                
                idResults.add(b.build());
            }
            return idResults;
        }
    }

}
