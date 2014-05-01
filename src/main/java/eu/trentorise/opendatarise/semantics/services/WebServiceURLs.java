package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;

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
    
    public static final String PROPERTIES_PATH = "META-INF/sweb-webapi-model.properties";    
    
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
			System.out.println(api.getLocale());
			return api;
		} else return api;

	}

	public static String getURL(){
		if (fullURL==null){
			fullURL = "http://"+url+":"+port+root;
			return fullURL;
		}
		else return fullURL;
	}

	private static void readProperties(){
		Properties prop = new Properties();
		InputStream input = null;
                           
		try {
                    
                    input = Thread.currentThread().getContextClassLoader().
                            getResourceAsStream(PROPERTIES_PATH);
			// input = new FileInputStream("conf/sweb-webapi-model.properties");
                    
                    prop.load(input);
                    url = prop.getProperty("sweb.webapi.url");
                    port= Integer.parseInt(prop.getProperty("sweb.webapi.port"));
                    root = prop.getProperty("sweb.webapi.root");

		} catch (IOException ex) {
			throw new RuntimeException("Couldn't read properties file: " + PROPERTIES_PATH, ex);
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





