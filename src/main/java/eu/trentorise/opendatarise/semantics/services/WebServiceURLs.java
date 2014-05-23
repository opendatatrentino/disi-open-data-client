package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/** The class reads property file and create singletone with relatted to url information
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 * @date 1 May 2014
 * 
 */
public class WebServiceURLs {
    
    public static final String PROPERTIES_FILE_NAME = "sweb-webapi-model.properties";    
    
    
	private static String url;
	private static int port;
	private static Locale locale;
	private static String root;
	private static String fullURL;



	public static IProtocolClient api;  

	public static IProtocolClient getClientProtocol(){

		if (api==null){
			locale=new Locale("all");
			readProperties();
			api = ProtocolFactory.getHttpClient(locale, url, port);
			//System.out.println(api.getLocale());
			return api;
		} else return api;

	}

	public static String getURL(){
		if (fullURL==null){
			if(url==null){
				readProperties();
			}
			fullURL = "http://"+url+":"+port+root;
			return fullURL;
		}
		else return fullURL;
	}

	private static void readProperties(){
		Properties prop = new Properties();
		InputStream input = null;
                           
		try {
                                              
                    if (new File("conf/" + PROPERTIES_FILE_NAME).exists()){
                        input = new FileInputStream("conf/" + PROPERTIES_FILE_NAME);
                    } else {                        
                        System.out.println("Couldn't find file conf/" + PROPERTIES_FILE_NAME + ", trying in WEB-INF/");
                        input = Thread.currentThread().getContextClassLoader().
                            getResourceAsStream("META-INF/" + PROPERTIES_FILE_NAME);
                        if (input == null){
                            throw new IOException("Couldn't find file META-INF/" + PROPERTIES_FILE_NAME);
                        }
                    }
                                        		                    
                    prop.load(input);
                    url = prop.getProperty("sweb.webapi.url");
                    port= Integer.parseInt(prop.getProperty("sweb.webapi.port"));
                    root = prop.getProperty("sweb.webapi.root");

		} catch (IOException ex) {
			throw new RuntimeException("Couldn't read properties file: " + PROPERTIES_FILE_NAME, ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
                        }
		}

	}



}





