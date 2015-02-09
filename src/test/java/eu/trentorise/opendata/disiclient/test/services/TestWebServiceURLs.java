package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.services.WebServiceURLs;

/**
 *
 * @author David Leoni
 */
public class TestWebServiceURLs {

    @Test
    public void testURLConversion() {
        assertEquals(WebServiceURLs.urlToAttrDefToID(WebServiceURLs.attrDefIDToURL(3)), 3);
        assertEquals((long) WebServiceURLs.urlToEntityID(WebServiceURLs.entityIDToURL(3)), 3L);
        assertEquals(WebServiceURLs.urlToConceptID(WebServiceURLs.conceptIDToURL(3)), 3);
        assertEquals(WebServiceURLs.urlToEtypeID(WebServiceURLs.etypeIDToURL(3)), 3);

        
    }
    

}
