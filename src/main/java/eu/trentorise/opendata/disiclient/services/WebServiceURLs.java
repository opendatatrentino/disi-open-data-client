package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.OdtUtils;
import static eu.trentorise.opendata.columnrecognizers.SwebConfiguration.SWEB_WEBAPI_HOST;
import static eu.trentorise.opendata.columnrecognizers.SwebConfiguration.SWEB_WEBAPI_PORT;
import static eu.trentorise.opendata.columnrecognizers.SwebConfiguration.SWEB_WEBAPI_ROOT;
import eu.trentorise.opendata.semtext.nltext.UrlMapper;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The class reads property file and create singletone with relatted to url
 * information
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class WebServiceURLs {

    private static Logger logger = LoggerFactory.getLogger(WebServiceURLs.class);

    public static final String CONCEPT_PREFIX = "/concepts/";

    public static final String ENTITY_PREFIX = "/instances/";

    public static final String ATTR_DEF_PREFIX = "/attributedefinitions/";

    public static final String ETYPE_PREFIX = "/types/";

    private static final UrlMapper urlMapper = UrlMapper.of(WebServiceURLs.getURL() + ENTITY_PREFIX,
            WebServiceURLs.getURL() + CONCEPT_PREFIX);

    public static boolean isEntityURL(String entityURL) {
        return entityURL != null && entityURL.contains(ENTITY_PREFIX);
    }

    public static boolean isConceptURL(String conceptURL) {
        return conceptURL != null && conceptURL.contains(CONCEPT_PREFIX);
    }

    public static boolean isEtypeURL(String etypeURL) {
        return etypeURL != null && etypeURL.contains(ETYPE_PREFIX);
    }

    public static boolean isAttrDefURL(String attrDefURL) {
        return attrDefURL != null && attrDefURL.contains(ATTR_DEF_PREFIX);
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToConceptID(String URL) {
        return urlMapper.urlToConceptId(URL);
    }

    public static String conceptIDToURL(long ID) {
        return urlMapper.conceptIdToUrl(ID);
    }

    public static String entityIDToURL(long ID) {
        return urlMapper.entityIdToUrl(ID);
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    public static Long urlToEntityID(String URL) {
        return urlMapper.urlToEntityId(URL);
    }

    public static String etypeIDToURL(long ID) {
        return WebServiceURLs.getURL() + ETYPE_PREFIX + ID;
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToEtypeID(String URL) {
        return OdtUtils.parseNumericalId(WebServiceURLs.getURL() + ETYPE_PREFIX, URL);
    }

    public static String attrDefIDToURL(long id) {
        return WebServiceURLs.getURL() + ATTR_DEF_PREFIX + id;
    }

    /**
     *
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToAttrDefToID(String URL) {
        return OdtUtils.parseNumericalId(WebServiceURLs.getURL() + ATTR_DEF_PREFIX, URL);
    }

    public static final String PROPERTIES_FILE_NAME = "sweb-webapi-model.properties";

    private static String url;
    private static int port;
    private static Locale locale;
    private static String root;
    private static String fullURL;

    public static IProtocolClient api;

    public static IProtocolClient getClientProtocol() {

        SwebConfiguration.checkInitialized();
        if (api == null) {
            locale = new Locale("all");
            readProperties();
            api = ProtocolFactory.getHttpClient(locale, url, port);            
            return api;
        } else {
            return api;
        }

    }

    public static IProtocolClient getClientProtocol(Locale locale) {

        SwebConfiguration.checkInitialized();
        if (api == null) {
            readProperties();
            api = ProtocolFactory.getHttpClient(locale, url, port);            
            return api;
        } else {
            return api;
        }

    }

    public static String getURL() {
        if (fullURL == null) {
            if (url == null) {
                readProperties();
            }
            logger.warn("TODO - ASSUMING HTTP AS PROTOCOL");
            fullURL = "http://" + url + ":" + port + root;
            return fullURL;
        } else {
            return fullURL;
        }
    }

    private static void readProperties() {
       // todo 'url' should actually be called 'host'
            url = checkNotNull(SwebConfiguration.getString(SWEB_WEBAPI_HOST));              
            port = Integer.parseInt(SwebConfiguration.getString(SWEB_WEBAPI_PORT));            
            root = checkNotNull(SwebConfiguration.getString(SWEB_WEBAPI_ROOT));
    }

    public static UrlMapper getSemtextUrlMapper() {
        return urlMapper;
    }
}
