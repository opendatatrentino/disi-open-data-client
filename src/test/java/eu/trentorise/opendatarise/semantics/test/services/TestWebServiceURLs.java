package eu.trentorise.opendatarise.semantics.test.services;

import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class TestWebServiceURLs {
 
    @Test
    public void testURLConversion(){
        assertEquals(WebServiceURLs.urlToAttrDefToID(WebServiceURLs.attrDefIDToURL(3)), 3);
        assertEquals((long) WebServiceURLs.urlToEntityID(WebServiceURLs.entityIDToURL(3)), 3L);
        assertEquals(WebServiceURLs.urlToConceptID(WebServiceURLs.conceptIDToURL(3)), 3);
        assertEquals(WebServiceURLs.urlToEtypeID(WebServiceURLs.etypeIDToURL(3)), 3);
        
    }
    
}
