package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.IdentityService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.ATTR_DEF_PART_OF_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.CERTIFIED_PRODUCT_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.LOCATION_URL;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_NAME_IT;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_URL;
import eu.trentorise.opendata.semantics.impl.model.entity.MinimalEntity;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.services.model.AssignmentResult;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Mar 2014
 *
 */
public class TestIDManagementService {

    public static final long GYMNASIUM_CONCEPT_ID = 18565L;
    
    private static final Logger logger = LoggerFactory.getLogger(TestIDManagementService.class);

    
    @Before
    public void beforeMethod(){
        ConfigLoader.init();
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

    public static IEntity assignNewURL(){
        IdentityService idServ = new IdentityService();
        EntityService enServ = new EntityService(getClientProtocol());
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_ID);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();
        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Foursquare ID")) {
                //	System.out.println(atr.getName());
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, "50f6e6f516488f6cc81a42fc");
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            }

        }
        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);

        List<IEntity> entities = new ArrayList<IEntity>();
        entities.add(ent);

        List<IIDResult> results = idServ.assignURL(entities, 3);
        assertEquals(1, results.size());
        assertEquals(AssignmentResult.NEW, results.get(0).getAssignmentResult());
        assertNotNull(results.get(0).getResultEntity());
                
        return results.get(0).getResultEntity();
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
        IdentityService idServ = new IdentityService();
        List res = idServ.assignURL(new ArrayList(), 3);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testIdManagementReuse() {
        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
        IdentityService idServ = new IdentityService();
        String name = PALAZZETTO_NAME_IT;
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_ID);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();
        List<IAttribute> iattr = entity.getStructureAttributes();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                Attribute a = createAttributeNameEntity(name);
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 11.466894f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 46.289413f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
                //					
            } else if (atr.getName().get("en").equalsIgnoreCase("Class")) {
                
                ConceptODR concept = new KnowledgeService().readConcept(GYMNASIUM_CONCEPT_ID);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            }
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        //en.setGlobalId(10002538L);
        EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);
        System.out.println("Name:" + ent.getName());
        System.out.println("Name:" + ent.getDescription());

        List<IEntity> entities = new ArrayList<IEntity>();
        entities.add(ent);

        List<IIDResult> results = idServ.assignURL(entities, 3);
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
        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
        IdentityService idServ = new IdentityService();
        String name = PALAZZETTO_NAME_IT;
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_ID);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();

        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                Attribute a = createAttributeNameEntity(name);
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 11.466894f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 46.289413f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
                //					
            }
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);
        System.out.println("Name:" + ent.getName());
        System.out.println("Name:" + ent.getDescription());

        List<IEntity> entities = new ArrayList<IEntity>();
        entities.add(ent);

        List<IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());

        }
    }

    @Test
    public void testMissingClassCertifiedProduct() {
        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
        IdentityService idServ = new IdentityService();

        EntityTypeService ets = new EntityTypeService();
        IEntityType et = ets.readEntityType(CERTIFIED_PRODUCT_URL);

        IAttributeDef certificateTypeAttrDef = et.getAttrDef(TestEntityService.ATTR_TYPE_OF_CERTIFICATE_URL);

        assertNotNull(certificateTypeAttrDef);

        IAttribute attr = enServ.createAttribute(certificateTypeAttrDef, "Please work");

        EntityODR en = new EntityODR();
        en.setEntityBaseId(1L);
        en.setTypeId(CERTIFIED_PRODUCT_ID);

        List<IAttribute> attrs = new ArrayList();
        attrs.add(attr);
        en.setStructureAttributes(attrs);

        List<IEntity> entities = new ArrayList<IEntity>();
        entities.add(en);

        List<IIDResult> results = idServ.assignURL(entities, 3);
        for (IIDResult res : results) {
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
        }
    }

    @Test
    public void testRelationalAttribute() {
        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
        EntityTypeService etypeServ = new EntityTypeService();
        IdentityService idServ = new IdentityService();

        final EntityODR enodr = new EntityODR();
        
        IEntityType facility = etypeServ.readEntityType(FACILITY_URL);
        enodr.setEtype(facility);
        enodr.setEntityBaseId(1L); 
        logger.warn("USING FIXED ID FOR ENTITY BASE! TODO FIXME!");
                

        List<IAttribute> attrs = new ArrayList();
        attrs.add(enServ.createAttribute(facility.getAttrDef(facility.getNameAttrDef().getURL()),
                "test entity")); // so doesn't complain about missing name...
        

        attrs.add(enServ.createAttribute(facility.getAttrDef(TestEntityService.ATTR_DEF_PART_OF_URL),
            new MinimalEntity(PALAZZETTO_URL,new Dict(), new Dict(), null)));
        
        enodr.setStructureAttributes(attrs);
        
        idServ.assignURL(new ArrayList() {
            {
                add(enodr);
            }
        }, 3);
    }
    
 @Test
    public void testNewEntityWithPartOfNewEntity() {

        
        EntityService enServ = new EntityService();        
        
        // IEntity entityPartOf = new MinimalEntity(RAVAZZONE_URL, new Dict(), new Dict(), null);
        
        // assertNotNull(entityPartOf.getEtypeURL());
                                
        IEntity newEntity = assignNewURL();
      
        
        
        List<IAttribute> structureAttributes = newEntity.getStructureAttributes();
        for (int i = 0; i < structureAttributes.size(); i++){
            IAttribute attr = structureAttributes.get(i);
            if (attr.getAttrDef().getURL().equals(ATTR_DEF_PART_OF_URL)){
                AttributeODR newAttr = enServ.createAttribute(attr.getAttrDef(), 
                        new MinimalEntity("http://trial/instances/new/1234567", new Dict(),new Dict(), LOCATION_URL));
                structureAttributes.set(i, newAttr);
            }
        }
        
        List<IIDResult> idRes = new IdentityService().assignURL(Arrays.asList(newEntity), 3);
                        
    }    
    
    
       
    @Test
    public void idServiceEntityMissing() {

        IdentityService idServ = new IdentityService();
        EntityService enServ = new EntityService(getClientProtocol());
        EntityODR entity = (EntityODR) enServ.readEntity(64000L);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();

        for (Attribute atr : attrs) {

            if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                attrs1.add(atr);
            } else if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                attrs1.add(atr);
            } //			else if (atr.getName().get("en").equalsIgnoreCase("Class")){
            //				attrs1.add(atr);
            //			}
            else if (atr.getName().get("en").equalsIgnoreCase("Class")) {                
                ConceptODR concept = new KnowledgeService().readConcept(GYMNASIUM_CONCEPT_ID);
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, concept);
                Attribute a = attr.convertToAttribute();
                attrs1.add(atr);
            }

        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(12L);
        en.setAttributes(attrs1);

        IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);

        List<IEntity> entities = new ArrayList<IEntity>();
        entities.add(ent);

        List<IIDResult> results = idServ.assignURL(entities, 3);
        
        for (IIDResult res : results) {
            EntityODR entityODR = (EntityODR) res.getResultEntity();
            //	System.out.println("result "+res.getAssignmentResult());
            //	System.out.println("Global id: "+res.getGUID());
            //	System.out.println("Local id: "+entityODR.getLocalID());
            assertEquals(AssignmentResult.NEW, res.getAssignmentResult());
        }
    }

    private static IProtocolClient getClientProtocol() {
        return WebServiceURLs.getClientProtocol();
    }

    public Attribute createAttributeNameEntity(String value) {
        EntityService es = new EntityService(getClientProtocol());
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = ets.getEntityType(12L);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList<Attribute>();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
            if (atd.getName().getString(Locale.ENGLISH).equals("Name")) {
                System.out.println(atd.getName());
                AttributeODR attr = es.createNameAttributeODR(atd, value);
                a = attr.convertToAttribute();
                return a;
            }
        }
        return a;
    }

    public Attribute createAttributeEntity(Object value) {
        EntityService es = new EntityService(getClientProtocol());
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = ets.getEntityType(12L);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList<Attribute>();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
        }

        return a;

    }

}
