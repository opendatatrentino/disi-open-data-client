package eu.trentorise.opendata.disiclient.test.services;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameDescrCreationIT extends DisiTest {

    private static final Logger logger = LoggerFactory.getLogger(NameDescrCreationIT.class);

    private IEntityService es;
    private IEtypeService ets;
    
    @Before
    public void before(){
	es =  ekb.getEntityService();
	ets = ekb.getEtypeService();
    }
    
    @After
    public void after(){
	es = null;
	ets = null;
    }
    
    @Test
    public void testCreateName() {
                
        Dict.Builder namesBuilder = Dict.builder();
        Dict newNames = namesBuilder.put(Locale.ITALIAN, "Buon Giorno")
                .put(Locale.ENGLISH, "Hello")
                .put(Locale.FRENCH, "Bonjour").build();
        logger.info(newNames.toString());

        Entity entity = es.readEntity(PALAZZETTO_URL);
        Entity.Builder enb = Entity.builder();
        Etype etype = ets.readEtype(entity.getId());        
        
        enb.setNameAttr(newNames, entity.getEtypeId(), ets);
        enb.putObj(etype.attrDefByName("Longitude"), 11.466894f);
        enb.putObj(etype.attrDefByName("Latitude"), 46.289413f);
                
        enb.setEtypeId(FACILITY_URL);                       
        Entity newEn = es.createEntity(enb.build());
        checker.checkEntity(newEn);

    }


    
}
