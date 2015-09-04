package eu.trentorise.opendata.disiclient.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import static eu.trentorise.opendata.commons.OdtUtils.checkNotDirtyUrl;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.IdentityService;

import eu.trentorise.opendata.disiclient.test.services.TestEntityService;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.RAVAZZONE_URL;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.IIDResult;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class IntegrityCheckerTest {

    String resourceName = "IMPIANTI RISALITA";

    String col1 = "nr";
    String col2 = "Comune";
    String col3 = "Insegna";
    String col4 = "Tipo";
    String col5 = "Frazione";
    String col6 = "Indirizio";
    String col7 = "Civico";

    List<String> cols = new ArrayList<String>() {
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

    List<List<String>> bodies = new ArrayList<List<String>>() {
        {
            add(new ArrayList<String>() {
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

    private IEkb ekb;
    private Checker checker;
    private IEntityTypeService ets;
    
    @Before
    public void beforeMethod() {
        
        ekb = ConfigLoader.init();
        ets = ekb.getEntityTypeService();
        checker = Checker.of(ekb);
    }  
    
    @After
    public void after(){
        ets = null;
        ekb = null;
        checker = null;
        
    }
    

    /**
     * TODO REVIEW IGNORED COMMENTED TEST
     */
    @Test
    @Ignore
    public void testCheckEtypesWithAttrDef() {
        
        List<IEntityType> etypes = ekb.getEntityTypeService().readAllEntityTypes();
        for (IEntityType etype : etypes) {
            checker.checkEntityType(etype);
            checkNotDirtyUrl(etype.getURL(), "etype url is dirty!");
            List<IAttributeDef> atdefs = etype.getAttributeDefs();
            for (IAttributeDef ad : atdefs) {
                checker.checkAttributeDef(ad);
                checkNotNull(ad.getName(), "attribute def name is null!");
                //checkNotNull(ad.getConcept().getDescription(), "attribute def concept description is null!");
                //checkNotNull(ad.getConcept().getName(), "attribute def concept name is null!");
                checkNotDirtyUrl(ad.getURL(), "attr def url is dirty!");
                checkNotDirtyUrl(ad.getEtypeURL(), "attr def etype url is dirty!");
                if (ad.getDatatype().equals(DataTypes.STRUCTURE)) {
                    checkNotDirtyUrl(ad.getRangeEtypeURL(), "attr def range etype url is dirty!");
                }
            }
        }
        assertNotNull(etypes.get(0));
    }

    // TODO REVIEW COMMENTED TEST
    @Test
    @Ignore
    public void testCheckEntity() {
        
        IEntity entity = ekb.getEntityService().readEntity(RAVAZZONE_URL);
        checker.checkEntity(entity);
        List<IAttribute> attributes = entity.getStructureAttributes();

        for (IAttribute attr : attributes) {
            checker.checkValue(attr.getFirstValue(), ets.readAttrDef(attr.getAttrDefUrl()));
        }

    }

    @Test
    public void testCheckIDResults() {
        IIdentityService idServ = ekb.getIdentityService();

        IEntity entity1 = entityForReuseResults();
        IEntity entity2 = entityForNewResults();
        IEntity entity3 = entityForMissingResults();

        List<IEntity> entities = new ArrayList();
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            System.out.println(res.getAssignmentResult().toString());

            checker.checkIDResult(res);

        }
    }

    private IEntity entityForReuseResults() {
        IEntityService enServ = ekb.getEntityService();

        EntityODR entity = (EntityODR) enServ.readEntity(TestEntityService.PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();
        for (Attribute atr : attrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                attrs1.add(atr);
            }
        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(en);
        return ent;
    }

    private IEntity entityForNewResults() {
        IEntityService enServ = ekb.getEntityService();

        EntityODR entity = (EntityODR) enServ.readEntity(TestEntityService.PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();
        for (Attribute atr : attrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                AttributeDef ad = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(ad, 12.123F);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);

            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                attrs1.add(atr);
            }
        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(en);
        return ent;
    }

    private IEntity entityForMissingResults() {

        IEntityService enServ = ekb.getEntityService();
        EntityODR entity = (EntityODR) enServ.readEntity(TestEntityService.PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();

        for (Attribute atr : attrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                attrs1.add(atr);
            }
//			else 
//				if (atr.getName().strs("en").equalsIgnoreCase("Class")){
//					attrs1.add(atr);
//				}
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(en);
        return ent;
    }

    private Attribute createAttributeEntity(Object value) {
        IEntityService es = ekb.getEntityService();
        
        IEntityType etype = ets.readEntityType(FACILITY_URL);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
            if (atd.getName().string(Locale.ENGLISH).equals("Foursquare ID")) {
                AttributeODR attr = (AttributeODR) es.createAttribute(atd, (String) value);
                a = attr.convertToAttribute();
                attrs.add(a);
            }
        }
        return a;
    }

    // TODO REVIEW COMMENTED TEST
    //@Test
    public void testCheckConcepts() {

        ConceptODR concept = new ConceptODR();
        //concept = concept.readConcept(1L);
        //checker.checkConcept(concept);

    }

    // TODO REVIEW COMMENTED TEST
    //	@Test
    //	public void testCheckEKB(){
    //		IEkb ekb = new Ekb(); 
    //	Checker.checkEkbQuick(ekb);
    //	}
}
