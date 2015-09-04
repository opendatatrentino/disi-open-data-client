package eu.trentorise.opendata.disiclient.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClients;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityExportService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityExportService.class);

    private static final String REGEX_INPUT = "@type";

    EntityExportService() {
    }

    /**
     * The method generates JSON-LD @context
     *
     * @param id
     * @return
     */
    public String generateJSONLDContext(Long id) {
        EntityService es = DisiClients.getClient().getEntityService();

        IEntity entity = es.readEntity(id);        

        IEntityType etype = DisiClients.getClient().getEntityTypeService().readEntityType(entity.getEtypeURL());
        List<IAttributeDef> attributeDefs = etype.getAttributeDefs();

        for (IAttributeDef attrDef : attributeDefs) {
            System.out.println(attrDef.getName().string(Locale.ENGLISH));
            System.out.println(attrDef.getURL());
        }
        throw new UnsupportedOperationException("TODO implement me!");
        //return null;
    }

    public JsonObject generateEtypeContext(IEntityType etype) {
        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        JsonObject finJsonObject = new JsonObject();

        for (IAttributeDef attrDef : attrDefs) {

            JsonObject jsonObjectAttr = new JsonObject();
            jsonObjectAttr.addProperty("@id", attrDef.getURL());
            if (attrDef.getDatatype().equals(DataTypes.STRUCTURE)) {
                jsonObjectAttr.addProperty(REGEX_INPUT, attrDef.getRangeEtypeURL());
            } else {
                jsonObjectAttr.addProperty(REGEX_INPUT, attrDef.getDatatype());

            }
            finJsonObject.add(attrDef.getName().string(Locale.ENGLISH), jsonObjectAttr);
        }

        finJsonObject.addProperty("xsd", "http://www.w3.org/2001/XMLSchema#");
        finJsonObject.addProperty("oe", SwebConfiguration.getBaseUrl() + "/datatypes");

        return finJsonObject;
    }

    public void convertToJsonLd(InputStream inputStream, Writer writer) throws IOException {

        BufferedWriter bw = new BufferedWriter(writer);

        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream));
        while (parser.hasNext()) {
            JsonObject obj = semantifyJsonObject(parser.next());
            bw.write(obj.toString());
            bw.newLine();
            System.out.println(obj.toString());
        }
        bw.close();
    }

    /**
     * Add attribute name instead of "values"
     *
     * @param jsonInput
     * @return
     * @throws IOException
     */
    public JsonObject semantifyJsonObject(JsonElement jsonInput) throws IOException {

        JsonObject o = (JsonObject) jsonInput;
        JsonObject obj = (JsonObject) o.get("entity");
        Long typeId = obj.get("typeId").getAsLong();
        obj.remove("creationDate");
        obj.remove("modificationDate");
        obj.remove("dataType");
        obj.remove(REGEX_INPUT);
        obj.remove("typeId");
        Long globalID;
        try {
            globalID = obj.get("globalId").getAsLong();
            
            Long locid = DisiClients.getClient().getEntityService().readEntityByGlobalId(globalID).getId();

            obj.remove("globalId");
            String globalIdURL = SwebConfiguration.getUrlMapper().entityIdToUrl(locid);

            obj.addProperty("globalId", globalIdURL);
        }
        catch (NullPointerException e) {
            LOG.warn("SOMETHING WENT WRONG WHILE SEMANTIFYING JSON OBJECT - TODO INVESTIGATE THIS THING");
        }

		//convert from global concept to local one
        Long conceptTypeID = SwebClientCrap.readConceptGUID(typeId);

        EntityTypeService ets = new EntityTypeService();
        /////////////////!!!!!!!!!!!!!IMPORTANT CHANGE THE ETYPE ID!!!!!!!!!!!!!!///////////
        EntityType etype = ets.readEntityTypeByConceptId(conceptTypeID);

        List<IAttributeDef> attrDefs = etype.getAttributeDefs();

        JsonArray attrArray = (JsonArray) obj.get("attributes");
        JsonArray attrArrayUpdated = new JsonArray();
        obj.remove("attributes");
        for (JsonElement attr : attrArray) {

            JsonObject attrObj = (JsonObject) attr;

            Long attrGlobalConceptID = attrObj.get("conceptId").getAsLong();
            obj.remove("conceptId");
            Long attrConceptID = SwebClientCrap.readConceptGUID(attrGlobalConceptID);

            //	System.out.println(attrConceptID);
            for (IAttributeDef atDef : attrDefs) {
                AttributeDef ad = (AttributeDef) atDef;
				//System.out.println(ad.getConceptId());
                //System.out.println(attrConceptID);
                if (ad.getConceptId() == attrConceptID) {

                    String name = atDef.getName().string(Locale.ENGLISH);
                    //	System.out.println(name);
                    attrObj.remove("creationDate");
                    attrObj.remove("modificationDate");
                    attrObj.remove("dataType");
                    //  attrObj.remove("conceptId");

                    JsonElement valueElement = attrObj.get("values");
                    attrObj.remove("values");
                    attrObj.add(name, valueElement);
                    attrArrayUpdated.add(attrObj);
                    break;
                }

            }
            obj.add("attributes", attrArrayUpdated);
        }
        /////////////////!!!!!!!!!!!!!IMPORTANT CHANGE THE ETYPE ID!!!!!!!!!!!!!!///////////

        JsonObject contextJson = generateEtypeContext(etype);
        obj.add("@context", contextJson);
        //System.out.println(obj);
        return obj;
    }

    public Long methodPost(List<Long> entitiesId, String fileName) throws ClientProtocolException, IOException {

        String formatedEntities = entitiesId.toString().replace("[", "").replace("]", "").replace(" ", "");

        Response response = Request.Post(SwebConfiguration.getBaseUrl() + "/data/export")
                .bodyForm(Form.form().add("entityBase", "1").add("fileName", fileName).add("id", formatedEntities).add("maxDepth", "1").build())
                .execute();
        String content = response.returnContent().asString();
        System.out.print("File: " + content);
        return Long.parseLong(content);
    }

    public Long methodPostRDF(List<Long> entitiesId, String fileName) throws ClientProtocolException, IOException {

        String formatedEntities = entitiesId.toString().replace("[", "").replace("]", "").replace(" ", "");
        Response response = Request.Post(SwebConfiguration.getBaseUrl() + "/data/exportRDF")
                .bodyForm(Form.form().add("entityBase", "1").add("fileName", fileName).add("id", formatedEntities).add("maxDepth", "1").add("namespace", "http://www.w3.org/1999/02/22-rdf-syntax-ns").build())
                .execute();
        String content = response.returnContent().asString();
        System.out.print("File: " + content);
        return Long.parseLong(content);
    }

    public InputStream methodGet(Long id, String fileName) throws ClientProtocolException, IOException {
        InputStream is = Request.Get(SwebConfiguration.getBaseUrl() + "/files/" + id)
                .execute().returnContent().asStream();
		//		File file = new File(fileName);
        //		outputStream = new FileOutputStream(file);
        //
        //		int read = 0;
        //		byte[] bytes = new byte[1024];
        //
        //		while ((read = is.read(bytes)) != -1) {
        //			outputStream.write(bytes, 0, read);
        //		}
        //
        //		System.out.println("Done!");
        return is;
    }
}
