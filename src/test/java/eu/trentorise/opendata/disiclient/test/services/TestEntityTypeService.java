package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.NAME_URL;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import static eu.trentorise.opendata.disiclient.test.services.TestKnowledgeService.cleanCreatedConcepts;
import static eu.trentorise.opendata.disiclient.test.services.TestKnowledgeService.makeName;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static eu.trentorise.opendata.disiclient.test.services.TestKnowledgeService.createConcept;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class TestEntityTypeService {

    private static Logger logger = LoggerFactory.getLogger(TestEntityTypeService.class);

    private IEkb disiEkb;

    /**
     * @since 0.11.1
     */
    private static Set<Long> createdEtypeIds = new HashSet();


    /**
     * Tries to clean previously created etypes, if fails at least empties
     * internal list.
     * @since 0.11.1
     */
    public static void resetCreatedEtypes() {
        try {
            cleanCreatedEtypes();
        }
        catch (Exception ex) {
            logger.error("Failed to clean created etypes. Current list is : " + createdEtypeIds);
            logger.error("Resetting internal list.", ex);
            createdEtypeIds = new HashSet();
        }
    }

    @Before
    public void beforeMethod() {
        disiEkb = ConfigLoader.init();
        resetCreatedEtypes();
        TestKnowledgeService.resetCreatedConcepts();
    }
    
    
    @After
    public void afterMethod() {

        cleanCreatedEtypes();
        TestKnowledgeService.cleanCreatedConcepts();
        disiEkb = null;
    }

    @Test
    public void testGetEntityTypeByID() {
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = (EntityType) ets.getEntityType(12L);
        List<IAttributeDef> atdefs = etype.getAttributeDefs();
        //for (IAttributeDef ad:atdefs){
//			System.out.println(ad.getName());
//			System.out.println(ad.getDataType());
//			System.out.println("AttributeDef Etype URL:"+ad.getEtypeURL());
        //	} 
        //		System.out.println("URL:"+etype.getURL());
        //		System.out.println(etype.getName1());
        assertEquals(etype.getName1().get("it"), "Infrastruttura");
    }

    @Test
    public void testGetEntityTypesofStructure() {
        EntityTypeService ets = new EntityTypeService();
        // EntityType etype = (EntityType) ets.getEntityType(12L);
        EntityType etype = (EntityType) ets.readEntityType(NAME_URL);
        System.out.println("Etype:" + etype);
        etype.getAttributeDefs();
    }

    @Test
    public void testGetAllEntityTypes() {
        long timeStart = System.currentTimeMillis();
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        for (IEntityType etype : etypes) {

            List<IAttributeDef> atdefs = etype.getAttributeDefs();
            //System.out.println("AttributeDef ETYPE Name:"+etype.getName().getString(Locale.ENGLISH));
            //	System.out.println("AttributeDefs:"+etype.getAttributeDefs());
            //	System.out.println("AttributeDef Name:"+etype.getNameAttrDef());
            //	System.out.println("AttributeDef Description:"+etype.getDescriptionAttrDef());
            for (IAttributeDef ad : atdefs) {
                System.out.println("AttributeDef URL:" + ad.getURL());
                System.out.println("AttributeDef  DataType:" + ad.getEType());
            }
            IntegrityChecker.checkEntityType(etype);
        }
        long timeEnd = System.currentTimeMillis();
        long finalTime = timeEnd - timeStart;
        System.out.println(finalTime);
        assertNotNull(etypes.get(0));

    }

    @Test
    public void testGetRootsTypes() {
        EntityTypeService ets = new EntityTypeService();
        assertEquals("Entity", ets.getRootEtype().getName().getString(Locale.ENGLISH));
        assertEquals("Structure", ets.getRootStructure().getName().getString(Locale.ENGLISH));

    }

    @Test
    public void testGetEntityTypeByURL() {
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        for (IEntityType etype : etypes) {

            //System.out.println(etype.getName());
            //System.out.println(etype.getURL());
            IEntityType et = ets.getEntityType(etype.getURL());
            //	System.out.println(et.getName());
            assertNotNull(et);
        }
    }

    @Test
    public void testGetAttributeDefbyUrl() {
        EntityTypeService ets = new EntityTypeService();
        List<IEntityType> etypes = ets.getAllEntityTypes();
        List<IAttributeDef> attrDefs = etypes.get(0).getAttributeDefs();
        String attrDefUrl = attrDefs.get(0).getURL();
        IAttributeDef attrDef = etypes.get(0).getAttrDef(attrDefUrl);
        assertNotNull(attrDef);
    }

    @Test
    public void testReadNonExistingEntityType() {
        assertEquals(null, disiEkb.getEntityTypeService().readEntityType("http://blabla.com"));

    }

    @Test
    public void testRefreshEtypes() {
        ComplexTypeClient ctc = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
        ComplexType swebEtype = new ComplexType();

        long concId = createConcept();
        swebEtype.setConceptId(concId);

        String enName1 = "Disi client test etype 1";

        swebEtype.setName(makeName(enName1, "Etype di test dal disi client"));
        swebEtype.setDescription(makeName("a", "b"));

        logger.warn("TODO USING USUAL HARDCODED KNOWLEDGE BASE 1 ...");
        swebEtype.setKnowledgeBaseId(1L);

        long id = ctc.create(swebEtype);

        createdEtypeIds.add(id);

        EntityTypeService ets = new EntityTypeService();

        IEntityType readEtype = ets.readEntityType(WebServiceURLs.etypeIDToURL(id));

        assertEquals(enName1, readEtype.getName().getString(Locale.ENGLISH));

        ComplexType readSwebEtype = ctc.readComplexType(id, null);

        String enName2 = "Disi client test etype 2";
        readSwebEtype.setName(makeName(enName2, "Etype di test dal disi client"));

        ctc.update(readSwebEtype);

        ComplexType readSwebEtype2 = ctc.readComplexType(id, null);
        assertEquals(enName2, readSwebEtype2.getName().get("en"));

        IEntityType readEtype2 = ets.readEntityType(WebServiceURLs.etypeIDToURL(id));
        assertEquals(enName1, readEtype2.getName().getString(Locale.ENGLISH));

        ets.refreshEtypes();

        IEntityType readEtype3 = ets.readEntityType(WebServiceURLs.etypeIDToURL(id));
        assertEquals(enName2, readEtype3.getName().getString(Locale.ENGLISH));

    }

    @Test
    public void testFuzzySearchEtype() {
        EntityTypeService ets = new EntityTypeService();
        Locale locale = TraceProvUtils.languageTagToLocale("en");
        List<ISearchResult> searchEtypes = ets.searchEntityTypes("Product", locale);
        assertEquals("Product", searchEtypes.get(0).getName().getString(Locale.ENGLISH));

    }

    public static void cleanCreatedEtypes() {
        logger.info("Cleaning previously created etypes...");
        for (Long etypeId : createdEtypeIds) {
            ComplexTypeClient ctc = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
            ComplexType swebType = ctc.readComplexType(etypeId, null);
            if (swebType != null) {
                logger.info("Cleaning etype " + etypeId + " ...");
                ctc.delete(swebType);
            }
        }
        createdEtypeIds = new HashSet();
    }
}
