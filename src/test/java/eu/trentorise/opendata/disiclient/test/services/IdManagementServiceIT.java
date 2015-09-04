package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityService;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.ATTR_DEF_PART_OF_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.CERTIFIED_PRODUCT_ID;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.CERTIFIED_PRODUCT_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.FACILITY_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.GYMNASIUM_CONCEPT_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.LOCATION_URL;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.PALAZZETTO_NAME_IT;
import static eu.trentorise.opendata.disiclient.test.services.EntityServiceIT.PALAZZETTO_URL;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.MinimalEntity;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.AssignmentResult;
import eu.trentorise.opendata.semantics.services.IIDResult;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 *
 */
public class IdManagementServiceIT extends DisiTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(IdManagementServiceIT.class);


    IEntityService enServ;
    IEntityTypeService ets;
    IIdentityService idServ;
    
    @Before
    public void before(){        
        enServ = ekb.getEntityService();
        idServ = ekb.getIdentityService();
        ets = ekb.getEntityTypeService();
    }
    
    @After
    public void after(){
        enServ = null;
        idServ = null;
        ets = null;        
    }
    
    private String entityToString(Entity e) {
        String str = "id:" + e.getId()
                + ", gID:" + e.getGlobalId()
                + ", names:" + e.getNames()
                + ", attributes:" + attributesToString(e.getAttributes());
        return str;
    }

    private String attributesToString(List<Attribute> attributes) {
        String str = "[";
        for (Attribute attr : attributes) {
            str += attributeToString(attr) + "\n";
        }
        return str + "]";
    }

    private String attributeToString(Attribute attr) {
        String str = "attr concept_id:" + attr.getConceptId()
                + ", datatype:" + attr.getDataType() + " values[";
        for (Value v : attr.getValues()) {
            str += v.getValue() + ", ";
        }
        return str + "]";
    }
    
    
    public  IEntity assignNewURL(){
        
        
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();
        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")) {
                //	System.out.println(atr.getName());
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, "50f6e6f516488f6cc81a42fc");
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            }

        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(en);

        List<IEntity> entities = new ArrayList();
        entities.add(ent);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        assertEquals(1, results.size());
        assertEquals(AssignmentResult.NEW, results.get(0).getAssignmentResult());
        assertNotNull(results.get(0).getResultEntity());
                
        return results.get(0).getResultEntity();
    }
    
   @Before
    public void beforeMethod() {
        ConfigLoader.init();
    }    
    
    @Test
    public void idServiceEntityNew() {       
        assignNewURL();
    }

    /**
     *
     * Don't want errors on empty array
     */
    @Test
    public void testIdManagementEmptyArray() {
        
        List res = idServ.assignURL(new ArrayList(), 3);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testIdManagementReuse() {
                
        String name = PALAZZETTO_NAME_IT;
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();
        List<IAttribute> iattr = entity.getStructureAttributes();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                Attribute a = createAttributeNameEntity(name);
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, 11.466894f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, 46.289413f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
                //					
            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                
                IConcept concept = ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            }
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        //en.setGlobalId(10002538L);
        EntityODR ent = new EntityODR(en);
        System.out.println("Name:" + ent.getName());
        System.out.println("Name:" + ent.getDescription());

        List<IEntity> entities = new ArrayList();
        entities.add(ent);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            EntityODR entityODR = (EntityODR) res.getResultEntity();
            System.out.println("result " + res.getAssignmentResult());
            System.out.println("Global ID: " + res.getGUID());
            System.out.println("Local ID: " + entityODR.getLocalID());
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());

        }
    }

    @Test
    public void testFacilityIdMissingClass() {
        
        
        String name = PALAZZETTO_NAME_IT;
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                Attribute a = createAttributeNameEntity(name);
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, 11.466894f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, 46.289413f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
                //					
            }
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        EntityODR ent = new EntityODR(en);
        System.out.println("Name:" + ent.getName());
        System.out.println("Name:" + ent.getDescription());

        List<IEntity> entities = new ArrayList();
        entities.add(ent);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());

        }
    }

    @Test
    public void testMissingClassCertifiedProduct() {
                       
        IEntityType et = ets.readEntityType(CERTIFIED_PRODUCT_URL);

        IAttributeDef certificateTypeAttrDef = et.getAttrDef(EntityServiceIT.ATTR_TYPE_OF_CERTIFICATE_URL);

        assertNotNull(certificateTypeAttrDef);

        IAttribute attr = enServ.createAttribute(certificateTypeAttrDef, "Please work");

        EntityODR en = new EntityODR();
        en.setEntityBaseId(1L);
        en.setTypeId(CERTIFIED_PRODUCT_ID);

        List<IAttribute> attrs = new ArrayList();
        attrs.add(attr);
        en.setStructureAttributes(attrs);

        List<IEntity> entities = new ArrayList();
        entities.add(en);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
        }
    }

    @Test
    public void testRelationalAttribute() {                        

        final EntityODR enodr = new EntityODR();
        
        IEntityType facility = ets.readEntityType(FACILITY_URL);
        enodr.setEtype(facility);
        enodr.setEntityBaseId(1L); 
        LOG.warn("USING FIXED ID FOR ENTITY BASE! TODO FIXME!");
                

        List<IAttribute> attrs = new ArrayList();
        attrs.add(enServ.createAttribute(facility.getAttrDef(facility.getNameAttrDef().getURL()),
                "test entity")); // so doesn't complain about missing name...
        

        attrs.add(enServ.createAttribute(facility.getAttrDef(EntityServiceIT.ATTR_DEF_PART_OF_URL),
            new MinimalEntity(PALAZZETTO_URL,Dict.of(), Dict.of(), "")));
        
        enodr.setStructureAttributes(attrs);
        
        idServ.assignURL(new ArrayList() {
            {
                add(enodr);
            }
        }, 3);
    }
    
 @Test
    public void testNewEntityWithPartOfNewEntity() {

        
        
        // IEntity entityPartOf = new MinimalEntity(RAVAZZONE_URL, new Dict(), new Dict(), null);
        
        // assertNotNull(entityPartOf.getEtypeURL());
                                
        IEntity newEntity = assignNewURL();
      
        
        
        List<IAttribute> structureAttributes = newEntity.getStructureAttributes();
        for (int i = 0; i < structureAttributes.size(); i++){
            IAttribute attr = structureAttributes.get(i);
            if (attr.getAttrDefUrl().equals(ATTR_DEF_PART_OF_URL)){
                IAttribute newAttr = enServ.createAttribute(ets.readAttrDef(attr.getAttrDefUrl()), 
                        new MinimalEntity("http://trial/instances/new/1234567", Dict.of(), Dict.of(), LOCATION_URL));
                structureAttributes.set(i, newAttr);
            }
        }
        
        List<? extends IIDResult> idRes = idServ.assignURL(Arrays.asList(newEntity), 3);
                        
    }    
       
    @Test
    public void idServiceEntityMissing() {
                
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_URL);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList();

        for (Attribute atr : attrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                attrs1.add(atr);
            } //			else if (atr.getName().strs("en").equalsIgnoreCase("Class")){
            //				attrs1.add(atr);
            //			}
            else if (atr.getName().get("en").equalsIgnoreCase("Class")) {                
                IConcept concept =  ekb.getKnowledgeService().readConcept(GYMNASIUM_CONCEPT_URL);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = (AttributeODR) enServ.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);
            }

        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(en);

        List<IEntity> entities = new ArrayList();
        entities.add(ent);

        List<? extends IIDResult> results = idServ.assignURL(entities, 3);
        
        for (IIDResult res : results) {
            EntityODR entityODR = (EntityODR) res.getResultEntity();
            //	System.out.println("result "+res.getAssignmentResult());
            //	System.out.println("Global id: "+res.getGUID());
            //	System.out.println("Local id: "+entityODR.getLocalID());
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
        }
    }


    public Attribute createAttributeNameEntity(String value) {
                
        EntityType etype = (EntityType) ets.readEntityType(FACILITY_URL);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
            if (atd.getName().string(Locale.ENGLISH).equals("Name")) {
                System.out.println(atd.getName());
                AttributeODR attr = ((EntityService) enServ).createNameAttributeODR(atd, value);
                a = attr.convertToAttribute();
                return a;
            }
        }
        return a;
    }

    public Attribute createAttributeEntity(Object value) {
        
        
        IEntityType etype = ets.readEntityType(FACILITY_URL);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
        }

        return a;

    }

}
