package eu.trentorise.opendata.disiclient.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import static eu.trentorise.opendata.commons.TodUtils.checkNotDirtyUrl;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.test.services.DisiTest;
import eu.trentorise.opendata.disiclient.test.services.EntityServiceIT;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEtypeService;

import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IdResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class IntegrityCheckerTest extends DisiTest {

    String resourceName = "IMPIANTI RISALITA";

    String col1 = "nr";
    String col2 = "Comune";
    String col3 = "Insegna";
    String col4 = "Tipo";
    String col5 = "Frazione";
    String col6 = "Indirizio";
    String col7 = "Civico";

    List<String> cols = new ArrayList() {
        {
            add("nr");
            add("Comune");
            add("Insegna");
            add("Tipo");
            add("Frazione");
            add("Indirizio");
            add("Civico");
        }
    };

    List<List<String>> bodies = new ArrayList() {
        {
            add(new ArrayList() {
                {
                    add("1");
                    add("2");
                    add("3");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("ANDALO");
                    add("ARCO");
                    add("BASELGA DI PINE");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("AL FAGGIO");
                    add("OSTERIA IL RITRATTO");
                    add("AI DUE CAMI");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("Ristorante");
                    add("Ristorante-Bar");
                    add("Albergo-Ristorante-Bar");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("ANDALO");
                    add("ARCO");
                    add("BASELGA DI PINE");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("Via Fovo");
                    add("Via Ferrera");
                    add("Via Pontara");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("11");
                    add("30");
                    add("352");
                }
            });
        }
    };


    private IEtypeService ets;
    private IEntityService es;
    
    @Before
    public void beforeMethod() {
        
        ets = ekb.getEtypeService();
        es = ekb.getEntityService();
    }  
    
    @After
    public void after(){
        ets = null;       
        es = null;
    }
    

    /**
     * TODO REVIEW IGNORED COMMENTED TEST
     */
    @Test
    @Ignore
    public void testCheckEtypesWithAttrDef() {
        
        List<Etype> etypes = ekb.getEtypeService().readAllEtypes();
        for (Etype etype : etypes) {
            checker.checkEtype(etype);
            checkNotDirtyUrl(etype.getId(), "etype url is dirty!");
            Collection<AttrDef> atdefs = etype.getAttrDefs().values();
            for (AttrDef ad : atdefs) {
                checker.checkAttrDef(ad);
                checkNotNull(ad.getName(), "attribute def name is null!");
                //checkNotNull(ad.getConcept().getDescription(), "attribute def concept description is null!");
                //checkNotNull(ad.getConcept().getName(), "attribute def concept name is null!");
                checkNotDirtyUrl(ad.getId(), "attr def url is dirty!");                
                if (ad.getType().getDatatype().equals(DataTypes.STRUCTURE)) {
                    checkNotDirtyUrl(ad.getType().getEtypeId(), "attr def range etype url is dirty!");
                }
            }
        }
        assertNotNull(etypes.get(0));
    }

    // TODO REVIEW COMMENTED TEST
    @Test
    @Ignore
    public void testCheckEntity() {
        
        Entity entity = ekb.getEntityService().readEntity(RAVAZZONE_URL);
        checker.checkEntity(entity);
        Collection<Attr> attributes = entity.getAttrs().values();

        for (Attr attr : attributes) {
            checker.checkValue(attr.getValues().get(0), ets.readAttrDef(attr.getAttrDefId()));
        }

    }

    @Test
    public void testCheckIDResults() {
        IIdentityService idServ = ekb.getIdentityService();

        Entity entity1 = entityForReuseResults();
        Entity entity2 = entityForNewResults();
        Entity entity3 = entityForMissingResults();

        List<Entity> entities = new ArrayList();
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<IdResult> results = idServ.assignURL(entities, 3);
        for (IdResult res : results) {
            System.out.println(res.getAssignmentResult().toString());

            checker.checkIDResult(res);

        }
    }

    private Entity entityForReuseResults() {
        

        Entity entity = (Entity) es.readEntity(EntityServiceIT.PALAZZETTO_URL);
        Etype etype = ets.readEtype(entity.getEtypeId());
        Map<String, Attr> attrs = entity.getAttrs();
        
        Entity.Builder enB = Entity.builder();
        for (String attrDefId : attrs.keySet()) {
           AttrDef ad =  etype.attrDefById(attrDefId);
           String name = ad.getName().str(Locale.ENGLISH);
            if (name.equalsIgnoreCase("Latitude")
        	    || name.equalsIgnoreCase("Longitude")
        	    || name.equalsIgnoreCase("Class")) {
                enB.putAttrs(attrDefId, attrs.get(attrDefId));
            }
        }
                
        enB.setEtypeId(etype.getId());              
        return enB.build();
    }

    private Entity entityForNewResults() {
        Entity entity = (Entity) es.readEntity(EntityServiceIT.PALAZZETTO_URL);
        Etype etype = ets.readEtype(entity.getEtypeId());
        Map<String, Attr> attrs = entity.getAttrs();
        
        Entity.Builder enB = Entity.builder();
        for (String attrDefId : attrs.keySet()) {
           AttrDef ad =  etype.attrDefById(attrDefId);
           String name = ad.getName().str(Locale.ENGLISH);

            if (name.equalsIgnoreCase("Latitude")) {                
                Attr attr = Attr.ofObject(ad, 12.123F);                
                enB.putAttrs(attrDefId, attr);

            } 
            if (name.equalsIgnoreCase("Longitude")
        	    || name.equalsIgnoreCase("Class")) {
                enB.putAttrs(attrDefId, attrs.get(attrDefId));
            }
            
        }
        
        enB.setEtypeId(etype.getId());               
        return enB.build();
    }

    private Entity entityForMissingResults() {
        Entity entity = (Entity) es.readEntity(EntityServiceIT.PALAZZETTO_URL);
        Etype etype = ets.readEtype(entity.getEtypeId());
        Map<String, Attr> attrs = entity.getAttrs();
        
        Entity.Builder enB = Entity.builder();
        for (String attrDefId : attrs.keySet()) {
           AttrDef ad =  etype.attrDefById(attrDefId);
           String name = ad.getName().str(Locale.ENGLISH);
            if (name.equalsIgnoreCase("Longitude")
        	    || name.equalsIgnoreCase("Latitude")) {
                enB.putAttrs(attrDefId, attrs.get(attrDefId));
            }
            
        }
        
        enB.setEtypeId(etype.getId());               
        return enB.build();

    }
/*
    private Attribute createAttributeEntity(Object value) {
        IEntityService es = ekb.getEntityService();
        
        Etype etype = ets.readEtype(FACILITY_URL);

        Collection<AttrDef> attrDefList = etype.getAttrDefs().values();
        List<Attr> attrs = new ArrayList();

        Attribute a = null;
        for (AttrDef atd : attrDefList) {
            if (atd.getName().string(Locale.ENGLISH).equals("Foursquare ID")) {
                Attr attr = Attr.ofObject(atd, (String) value);
                
                attrs.(a);
            }
        }
        return a;
    }*/

    
    @Test
    public void testCheckEKB(){
	IEkb ekb = new DisiEkb(); 
	checker.checkEkbQuick(ekb);
    }
}
