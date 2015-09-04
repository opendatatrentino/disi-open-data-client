package eu.trentorise.opendata.disiclient.test.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.SchemaMapping;
import eu.trentorise.opendata.traceprov.data.DcatMetadata;
import eu.trentorise.opendata.traceprov.types.ClassType;
import eu.trentorise.opendata.traceprov.types.Def;
import eu.trentorise.opendata.traceprov.types.ListType;
import eu.trentorise.opendata.traceprov.types.StringType;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMatchingService {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private IEkb client;

    String resourceName = "IMPIANTI RISALITA";

    String col1 = "nr";
    String col2 = "Comune";
    String col3 = "Insegna";
    String col4 = "Tipo";
    String col5 = "Frazione";
    String col6 = "Indirizio";
    String col7 = "Civico";

    List<String> cols = new ArrayList<String>() {
        {
            add("nr");
            add("Comune");
            add("Insegna");
            add("Tipo");
            add("Frazione");
            add("Indirizio");
            add("Civico");
        }
    };

    List<List<String>> bodies = new ArrayList<List<String>>() {
        {
            add(new ArrayList<String>() {
                {
                    add("1");
                    add("2");
                    add("3");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("ANDALO");
                    add("ARCO");
                    add("BASELGA DI PINE");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("AL FAGGIO");
                    add("OSTERIA IL RITRATTO");
                    add("AI DUE CAMI");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("Ristorante");
                    add("Ristorante-Bar");
                    add("Albergo-Ristorante-Bar");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("ANDALO");
                    add("ARCO");
                    add("BASELGA DI PINE");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("Via Fovo");
                    add("Via Ferrera");
                    add("Via Pontara");
                }
            });
            add(new ArrayList<String>() {
                {
                    add("11");
                    add("30");
                    add("352");
                }
            });
        }
    };

    @Before
    public void beforeMethod() {
        ConfigLoader.init();
        client = ConfigLoader.init();
    }

    @After
    public void after() {
        client = null;
    }

    /**
     * TODO REVIEW this thing always sets property type to StringType ....
     */
    private ClassType etypeToClassType(IEntityType et) {
        ClassType.Builder builder = ClassType.builder();
        builder.setId(et.getURL());
        for (IAttributeDef attrDef : et.getAttributeDefs()) {
            String attrName = attrDef.getName().str(Locale.ENGLISH);
            builder.putMethodDefs(attrName, Def.builder().setId(attrDef.getURL()).setType(StringType.of()).build());
        }
        return builder.build();
    }


    @Test
    public void testMatchingService() {

        List<IEntityType> allEntityTypes = client.getEntityTypeService().readAllEntityTypes();

        for (IEntityType et : allEntityTypes) {

            ClassType classType = etypeToClassType(et);
            
            List<SchemaMapping> schemaMappings = client.getSchemaMatchingService().matchSchemas(DcatMetadata.of(),
                    ListType.of(classType),null);
            
            assertEquals(allEntityTypes.size(), schemaMappings.size());
            
            LOG.warn("TODO MATCHING SERVICE NEEDS BETTER TESTING");
        }
    }

}
