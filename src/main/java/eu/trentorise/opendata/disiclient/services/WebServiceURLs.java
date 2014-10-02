package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.IntegrityException;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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

    /**
     * Parses an URL having a numeric ID after the provided prefix, i.e.
     * http://entitypedia.org/concepts/14324
     *
     * @throws IllegalArgumentException on unparseable URL
     */
    private static long parseID(String prefix, String URL) {
        try {
            IntegrityChecker.checkURL(URL);
        }
        catch (IntegrityException ex) {
            throw new IllegalArgumentException(ex);
        }
        int pos = URL.indexOf(prefix) + prefix.length();
        if (pos == -1) {
            throw new IllegalArgumentException("Invalid URL for object of type " + prefix + ": " + URL);
        }
        String s = URL.substring(pos);
        try {
            return Long.parseLong(s);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid URL for object of type " + prefix + ": " + URL, ex);
        }

    }

    public static String conceptIDToURL(long ID) {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + CONCEPT_PREFIX + ID;
        return url;
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToConceptID(String URL) {
        return parseID(CONCEPT_PREFIX, URL);
    }

    public static String entityIDToURL(long ID) {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + ENTITY_PREFIX + ID;
        return url;
    }

    public static String etypeIDToURL(long ID) {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + ETYPE_PREFIX + ID;
        return url;
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToEtypeID(String URL) {
        return parseID(ETYPE_PREFIX, URL);
    }

    /**
     * @throws IllegalArgumentException on unparseable URL
     */
    @Nullable
    public static Long urlToEntityID(String URL) {
        return parseID(ENTITY_PREFIX, URL);
    }

    public static String attrDefIDToURL(long id) {
        return WebServiceURLs.getURL() + ATTR_DEF_PREFIX + id;
    }

    /**
     *
     * @throws IllegalArgumentException on unparseable URL
     */
    public static long urlToAttrDefToID(String URL) {
        return parseID(ATTR_DEF_PREFIX, URL);
    }

    public static final String PROPERTIES_FILE_NAME = "sweb-webapi-model.properties";

    private static String url;
    private static int port;
    private static Locale locale;
    private static String root;
    private static String fullURL;

    public static IProtocolClient api;

    public static IProtocolClient getClientProtocol() {

        if (api == null) {
            locale = new Locale("all");
            readProperties();
            api = ProtocolFactory.getHttpClient(locale, url, port);
            //System.out.println(api.getLocale());
            return api;
        } else {
            return api;
        }

    }
    
    public static IProtocolClient getClientProtocol(Locale locale) {

        if (api == null) {
            readProperties();
            api = ProtocolFactory.getHttpClient(locale, url, port);
            //System.out.println(api.getLocale());
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
            fullURL = "http://" + url + ":" + port + root;
            return fullURL;
        } else {
            return fullURL;
        }
    }

    private static void readProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            if (new File("conf/" + PROPERTIES_FILE_NAME).exists()) {
                input = new FileInputStream("conf/" + PROPERTIES_FILE_NAME);
            } else {
                System.out.println("Couldn't find file conf/" + PROPERTIES_FILE_NAME + ", trying in WEB-INF/");
                input = Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("META-INF/" + PROPERTIES_FILE_NAME);
                if (input == null) {
                    throw new IOException("Couldn't find file META-INF/" + PROPERTIES_FILE_NAME);
                }
            }

            prop.load(input);
            url = prop.getProperty("sweb.webapi.url");
            port = Integer.parseInt(prop.getProperty("sweb.webapi.port"));
            root = prop.getProperty("sweb.webapi.root");

        }
        catch (IOException ex) {
            throw new RuntimeException("Couldn't read properties file: " + PROPERTIES_FILE_NAME, ex);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {
                    logger.error("Couldn't close input", ex);
                }
            }
        }

    }

}
