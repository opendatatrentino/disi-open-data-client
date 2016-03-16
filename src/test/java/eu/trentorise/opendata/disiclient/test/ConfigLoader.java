package eu.trentorise.opendata.disiclient.test;

import eu.trentorise.opendata.disiclient.services.DisiConfiguration;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.disiclient.test.services.EntityExportServiceTest;
import eu.trentorise.opendata.disiclient.test.services.TestEntityService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads configuration for tests
 *
 * @author David Leoni
 */
public class ConfigLoader {

    /**
     * Loads configuration from conf/sweb-webapi-model-override.properties file
     * and returns a new configured DisiEkb.
     */
    public static DisiEkb init() {
        File file = new File("conf/sweb-webapi-model-override.properties");
        try {
            Properties props = new Properties();
            InputStream in = new FileInputStream(file);
            //        props.lo
            props.load(in);
            DisiConfiguration.init((Map) props);
            DisiEkb ret = new DisiEkb();
            ret.setProperties(new HashMap());
            initUrls();
            return ret;
        }
        catch (Exception ex) {
            throw new RuntimeException("Error while loading " + file.getAbsolutePath(), ex);
        }
    }

    /**
     * Initializes convenience urls in other test classes.
     *
     * To be called after config properties have been set.
     * 
     * @since 0.11.1
     */
    public static void initUrls() {
        DisiConfiguration.checkInitialized();
        
        TestEntityService.OPENING_HOURS_URL = WebServiceURLs.etypeIDToURL(TestEntityService.OPENING_HOURS);

        TestEntityService.ATTR_DEF_FACILITY_OPENING_HOURS_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_FACILITY_OPENING_HOURS);

        TestEntityService.ATTR_DEF_HOURS_OPENING_HOUR_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_HOURS_OPENING_HOUR);

        TestEntityService.ATTR_DEF_HOURS_CLOSING_HOUR_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_HOURS_CLOSING_HOUR);

        TestEntityService.PALAZZETTO_URL = WebServiceURLs.entityIDToURL(TestEntityService.PALAZZETTO_ID);

        TestEntityService.GYMNASIUM_CONCEPT_URL = WebServiceURLs.conceptIDToURL(TestEntityService.GYMNASIUM_CONCEPT_ID);

        TestEntityService.RAVAZZONE_URL = WebServiceURLs.entityIDToURL(TestEntityService.RAVAZZONE_ID);

        TestEntityService.ADMIN_DISTRICT_CONCEPT_URL = WebServiceURLs.conceptIDToURL(TestEntityService.ADMINISTRATIVE_DISTRICT_CONCEPT_ID);

        TestEntityService.RESIDENCE_DES_ALPES_URL = WebServiceURLs.entityIDToURL(TestEntityService.RESIDENCE_DES_ALPES_ID);

        TestEntityService.POVO_URL = WebServiceURLs.entityIDToURL(TestEntityService.POVO_ID);

        TestEntityService.CAMPANIL_PARTENZA_URL = WebServiceURLs.entityIDToURL(TestEntityService.CAMPANIL_PARTENZA_ID);

        TestEntityService.DETACHABLE_CHAIRLIFT_CONCEPT_URL = WebServiceURLs.conceptIDToURL(TestEntityService.DETACHABLE_CHAIRLIFT_CONCEPT_ID);

        TestEntityService.ANDALO_URL = WebServiceURLs.entityIDToURL(TestEntityService.ANDALO_ID);

        TestEntityService.FARMACIA_SILVESTRI_URL = WebServiceURLs.entityIDToURL(TestEntityService.FARMACIA_SILVESTRI_ID);
        
        TestEntityService.CLASS_CONCEPT_ID_URL = WebServiceURLs.conceptIDToURL(TestEntityService.CLASS_CONCEPT_ID);

        TestEntityService.ROOT_ENTITY_URL = WebServiceURLs.etypeIDToURL(TestEntityService.ROOT_ENTITY_ID);

        TestEntityService.LOCATION_URL = WebServiceURLs.etypeIDToURL(TestEntityService.LOCATION_ID);

        TestEntityService.FACILITY_URL = WebServiceURLs.etypeIDToURL(TestEntityService.FACILITY_ID);

        TestEntityService.ATTR_DEF_LATITUDE_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_LATITUDE_ID);

        TestEntityService.ATTR_DEF_LONGITUDE_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_LONGITUDE_ID);

        TestEntityService.ATTR_DEF_CLASS_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_CLASS);

        TestEntityService.ATTR_DEF_DESCRIPTION_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_DESCRIPTION);

        TestEntityService.ATTR_DEF_PART_OF_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_DEF_PART_OF);

        TestEntityService.NAME_URL = WebServiceURLs.etypeIDToURL(TestEntityService.NAME_ID);

        TestEntityService.SHOPPING_FACILITY_URL = WebServiceURLs.etypeIDToURL(TestEntityService.SHOPPING_FACILITY_ID);

        TestEntityService.CERTIFIED_PRODUCT_URL = WebServiceURLs.etypeIDToURL(TestEntityService.CERTIFIED_PRODUCT_ID);

        TestEntityService.ATTR_TYPE_OF_CERTIFICATE_URL = WebServiceURLs.attrDefIDToURL(TestEntityService.ATTR_TYPE_OF_CERTIFICATE);

        TestEntityService.MELA_VAL_DI_NON_URL = WebServiceURLs.entityIDToURL(TestEntityService.MELA_VAL_DI_NON);
        
        TestEntityService.COMANO_URL = WebServiceURLs.entityIDToURL(TestEntityService.COMANO_ID);

        EntityExportServiceTest.ENTITY1_URL = WebServiceURLs.entityIDToURL(EntityExportServiceTest.ENTITY1);
        EntityExportServiceTest.ENTITY2_URL = WebServiceURLs.entityIDToURL(EntityExportServiceTest.ENTITY2);
        EntityExportServiceTest.ENTITY3_URL = WebServiceURLs.entityIDToURL(EntityExportServiceTest.ENTITY3);

    }
}
