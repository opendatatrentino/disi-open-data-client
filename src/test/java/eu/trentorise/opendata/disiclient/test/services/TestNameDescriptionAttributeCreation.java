package eu.trentorise.opendata.disiclient.test.services;

import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.FACILITY_ID;
import static eu.trentorise.opendata.disiclient.test.services.TestEntityService.PALAZZETTO_ID;
import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityService;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.IdentityService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.commons.Dict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestNameDescriptionAttributeCreation {

    private static final Logger logger = LoggerFactory.getLogger(TestNameDescriptionAttributeCreation.class);
    
    @Test
    public void testCreateDescription() {
        EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
        IdentityService idServ = new IdentityService();
        //String name = PALAZZETTO_NAME_IT;
        Dict.Builder namesBuilder = Dict.builder();
        Dict newNames = namesBuilder.put(Locale.ITALIAN, "Buon Giorno")
                .put(Locale.ENGLISH, "Hello")
                .put(Locale.FRENCH, "Bonjour").build();
        logger.info(newNames.toString());
		//String name = "my entity name";
        //		Search search = new Search(WebServiceURLs.getClientProtocol());
        //		List<Name> names = search.nameSearch(name);

        //		for (Name n: names ){
        //			System.out.println("Names:"+n);
        //		}
        EntityODR entity = (EntityODR) enServ.readEntity(PALAZZETTO_ID);
        List<Attribute> attrs = entity.getAttributes();
        List<Attribute> attrs1 = new ArrayList<Attribute>();
        //List<IAttribute> iattr=entity.getStructureAttributes();
        for (Attribute atr : attrs) {
            if (atr.getName().get("en").equalsIgnoreCase("Name")) {
                Attribute a = createAttributeNameEntityWithDict(newNames);
                attrs1.add(a);
            } else //				if (atr.getName().get("en").equalsIgnoreCase("Description")){
            //					IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
            //					//Value v = atr.getValues().get(0);
            //				//	AttributeODR attr = enServ.createAttribute(atDef, "my description");
            //					//Attribute a=attr.convertToAttribute();
            //					attrs1.add(atr);
            //				} 
            if (atr.getName().get("en").equalsIgnoreCase("Longitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 11.466894f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
            } else if (atr.getName().get("en").equalsIgnoreCase("Latitude")) {
                IAttributeDef atDef = new AttributeDef(atr.getDefinitionId());
                AttributeODR attr = enServ.createAttribute(atDef, 46.289413f);
                Attribute a = attr.convertToAttribute();
                attrs1.add(a);
                //					
            }
        }

        Entity en = new Entity();
        en.setEntityBaseId(1L);
        en.setTypeId(FACILITY_ID);
        en.setAttributes(attrs1);
        EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
        EntityODR ent = new EntityODR(WebServiceURLs.getClientProtocol(), en);
        Long id = es.createEntity(ent);
        assertNotNull(id);

    }

    public Attribute createAttributeNameEntityWithDict(Object value) {
        EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = ets.getEntityType(12L);

        List<IAttributeDef> attrDefList = etype.getAttributeDefs();
        List<Attribute> attrs = new ArrayList<Attribute>();

        Attribute a = null;
        for (IAttributeDef atd : attrDefList) {
            if (atd.getName().string(Locale.ENGLISH).equals("Name")) {
                //	System.out.println(atd.getName());
                AttributeODR attr = es.createNameAttributeODR(atd, value);
                a = attr.convertToAttribute();
                return a;
            }
        }
        return a;
    }

    @Test
    public void testCreateName() {

    }
}
