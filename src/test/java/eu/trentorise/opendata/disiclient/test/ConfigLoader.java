package eu.trentorise.opendata.disiclient.test;

import eu.trentorise.opendata.disiclient.services.DisiConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Loads configuration for tests
 * @author David Leoni
 */
public class ConfigLoader {
       
    /**
     * Loads configuration from conf/sweb-webapi-model-override.properties file
     */
    public static void init() {
        File file = new File ("conf/sweb-webapi-model-override.properties");
        try {            
            Properties props = new Properties();
            InputStream in = new FileInputStream(file);
    //        props.lo
            props.load(in);            
            DisiConfiguration.init((Map)props);
        } catch (Exception ex){
            throw new RuntimeException("Error while loading " + file.getAbsolutePath(), ex);
        }
    }}