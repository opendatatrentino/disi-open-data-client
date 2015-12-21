package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.model.Configuration;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a little hack to sweb client to allow loading configuration
 * from wherever we want
 *
 * @author David Leoni
 */
public class DisiConfiguration extends Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(DisiConfiguration.class);

    public static final String SWEB_PROPERTIES_FILENAME = "sweb-webapi-model.properties";

    public static final String SWEB_WEBAPI_ROOT = "sweb.webapi.root";
    public static final String SWEB_WEBAPI_TEST_USER = "sweb.webapi.test.user";
    public static final String SWEB_WEBAPI_KB_DEFAULT = "sweb.webapi.kb.default";    
    public static final String SWEB_WEBAPI_HOST = "sweb.webapi.host";
    public static final String SWEB_WEBAPI_PORT = "sweb.webapi.port";
    public static final String SWEB_WEBAPI_PROXY = "sweb.webapi.proxy";
    public static final String SWEB_WEBAPI_PROXY_HOST = "sweb.webapi.proxy.host";
    public static final String SWEB_WEBAPI_PROXY_PORT = "sweb.webapi.proxy.port";
    public static final String SWEB_WEBAPI_IDLE_TIMEOUT = "sweb.webapi.idle.timeout";
    public static final String SWEB_WEBAPI_READ_TIMEOUT = "sweb.webapi.read.timeout";
    public static final String SWEB_WEBAPI_MAX_CONNECTIONS = "sweb.webapi.maxconnections";
        
    private DisiConfiguration() {
        super(SWEB_PROPERTIES_FILENAME);
    }   
    
    /**
     * Throws exception if client is not properly initialized
     */
    public static void checkInitialized() {       
        TraceProvUtils.checkNonEmpty(getString(SWEB_WEBAPI_ROOT), SWEB_WEBAPI_ROOT);        
        TraceProvUtils.checkNonEmpty(getString(SWEB_WEBAPI_TEST_USER), SWEB_WEBAPI_TEST_USER);
        TraceProvUtils.checkNonEmpty(getString(SWEB_WEBAPI_KB_DEFAULT), SWEB_WEBAPI_KB_DEFAULT);
        TraceProvUtils.checkNonEmpty(getString(SWEB_WEBAPI_HOST), SWEB_WEBAPI_HOST);
        TraceProvUtils.checkNonEmpty(getString(SWEB_WEBAPI_PORT), SWEB_WEBAPI_PORT);
    }

    /**
     * Overrides existing configuration with given properties.
     */
    static public void init(Map<String, String> properties) {
        checkNotNull(properties);
        if (props.size() > 0) {
            LOG.info("Found " + props.size() + " sweb properties, updating " + properties.size() + " of them.");

            for (Entry<String, String> entry : properties.entrySet()) {
                if (entry.getKey().startsWith(DisiEkb.PROPERTIES_PREFIX)) {
                    props.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

}