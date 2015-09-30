package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
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
import eu.trentorise.opendata.disiclient.UrlMapper;


import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.DataTypes;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityExportService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityExportService.class);

    private static final String REGEX_INPUT = "@type";

    private DisiEkb ekb;
    private UrlMapper um;
    
    EntityExportService(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
        this.um = SwebConfiguration.getUrlMapper();
    }

    /**
     * The method generates JSON-LD @context
     *
     * @param id
     * @return
     */
    public String generateJSONLDContext(Long id) {
        EntityService es = DisiClients.getSingleton().getEntityService();

        /*
        it.unitn.disi.sweb.webapi.model.eb.Entity entity = es.readSwebEntity(id);   

        Etype etype = DisiClients.getSingleton().getEtypeService().readEtype(entity.getEtypeURL());
        Collection<AttrDef> attributeDefs = etype.getAttrDefs().values();

        for (AttrDef attrDef : attributeDefs) {
            System.out.println(attrDef.getName().string(Locale.ENGLISH));
            System.out.println(attrDef.getURL());
        }
        
        //return null;
        */
        throw new UnsupportedOperationException("TODO implement me!");
    }

    public JsonObject generateEtypeContext(Etype etype) {
        Collection<AttrDef> attrDefs = etype.getAttrDefs().values();
        JsonObject finJsonObject = new JsonObject();

        for (AttrDef attrDef : attrDefs) {

            JsonObject jsonObjectAttr = new JsonObject();
            jsonObjectAttr.addProperty("@id", attrDef.getId());
            if (attrDef.getType().getDatatype().equals(DataTypes.STRUCTURE)) {
                jsonObjectAttr.addProperty(REGEX_INPUT, attrDef.getType().getEtypeId());
            } else {
                jsonObjectAttr.addProperty(REGEX_INPUT, attrDef.getType().getDatatype());

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
        bw.write("[\n");
        boolean first = true;
        while (parser.hasNext()) {
            if (!first){
        	bw.write(",\n");        	
            }
            first = false;
            JsonObject obj = semantifyJsonObject(parser.next());
            bw.write(obj.toString());
            bw.newLine();
            System.out.println(obj.toString());
        }
        bw.write("]");
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
            
            // dav commentedLong locid =  um.entityUrlToId(ekb.getEntityService().readEntityByGlobalId(globalID).getsUrl()); 
        	    
            Long locid = obj.get("id").getAsLong();
            
            obj.remove("globalId");
            String globalIdURL = um.entityIdToUrl(locid);

            obj.addProperty("globalId", globalIdURL);
        }
        catch (NullPointerException e) {
            LOG.warn("SOMETHING WENT WRONG WHILE SEMANTIFYING JSON OBJECT - TODO INVESTIGATE THIS THING");
        }

	//convert from global concept to local one
        Long conceptTypeID = ekb.getKnowledgeService().readConceptByGuid(typeId).getId();
        
        EtypeService ets = ekb.getEtypeService();
        /////////////////!!!!!!!!!!!!!IMPORTANT CHANGE THE ETYPE ID!!!!!!!!!!!!!!///////////
        ComplexType etype = ets.readSwebComplexTypeByConceptId(conceptTypeID);

        List<AttributeDefinition> attrDefs = etype.getAttributes();

        JsonArray attrArray = (JsonArray) obj.get("attributes");
        JsonArray attrArrayUpdated = new JsonArray();
        obj.remove("attributes");
        for (JsonElement attr : attrArray) {

            JsonObject attrObj = (JsonObject) attr;

            Long attrGlobalConceptID = attrObj.get("conceptId").getAsLong();
            obj.remove("conceptId");
            Long attrConceptID = ekb.getKnowledgeService().readConceptByGuid(attrGlobalConceptID).getId();
                    
            for (AttributeDefinition ad : attrDefs) {
                
		
                if (ad.getConceptId().equals(attrConceptID)) {

                    String name = ad.getName().get("en");
                
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

        
        JsonObject contextJson = generateEtypeContext(ekb.getConverter().swebComplexTypeToOeEtype(etype));
        obj.add("@context", contextJson);
        
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
