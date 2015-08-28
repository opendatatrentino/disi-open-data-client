package eu.trentorise.opendata.schemamatcher.implementation.service;


import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.util.EtypeCache;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCachedEtypes {

    private final static Logger LOG = LoggerFactory.getLogger(TestSchemaImport.class.getName());

    
    private IEkb ekb;
    

    @Before
    public void beforeMethod() {
        ekb = ConfigLoader.init();
    
    }
    
    @After
    public void afterMethod(){
        ekb = null;        
    }    
    
    @Test // TODO Ignored TEST!
    @Ignore
    public void cacheTest() {
            
        //	ec.createSchemas();
        List<IEntityType> etypes = EtypeCache.of(ekb).readSchemas();
        
        assertTrue(etypes.size() > 0);
        for (IEntityType etype : etypes) {
            System.out.println(etype.getName().strings(Locale.ENGLISH));
            System.out.println(etype.getAttributeDefs().get(0).getConceptURL());

        }
                
    }
    
    

}
