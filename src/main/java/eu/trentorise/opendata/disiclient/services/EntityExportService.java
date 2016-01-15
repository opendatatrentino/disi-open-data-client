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

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.DataTypes;

public class EntityExportService {

	private static final String regexInput = "@type";

	/**
	 * The method generates JSON-LD @context
	 *
	 * @param id
	 * @return
	 */
	public String generateJSONLDContext(Long id) {
		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());

		IEntity entity = es.readEntity(id);
		EntityTypeService ets = new EntityTypeService();

		IEntityType etype = ets.getEntityType(entity.getEtype().getURL());
		List<IAttributeDef> attributeDefs = etype.getAttributeDefs();

		for (IAttributeDef attrDef : attributeDefs) {
			System.out.println(attrDef.getName().getString(Locale.ENGLISH));
			System.out.println(attrDef.getURL());
		}
		return null;
	}

	public JsonObject generateEtypeContext(IEntityType etype) {
		List<IAttributeDef> attrDefs = etype.getAttributeDefs();
		JsonObject finJsonObject = new JsonObject();

		for (IAttributeDef attrDef : attrDefs) {

			JsonObject jsonObjectAttr = new JsonObject();
			jsonObjectAttr.addProperty("@id", attrDef.getURL());
			if (attrDef.getDataType().equals(DataTypes.STRUCTURE)) {
				jsonObjectAttr.addProperty(regexInput, attrDef.getRangeEtypeURL());
			} else {
				jsonObjectAttr.addProperty(regexInput, attrDef.getDataType());

			}
			finJsonObject.add(attrDef.getName().getString(Locale.ENGLISH), jsonObjectAttr);
		}

		finJsonObject.addProperty("xsd", "http://www.w3.org/2001/XMLSchema#");
		finJsonObject.addProperty("oe", WebServiceURLs.getURL() + "/datatypes");

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
		obj.remove(regexInput);
		obj.remove("typeId");
		Long globalID=null;
		try {
		 globalID = obj.get("globalId").getAsLong();
		 EntityService es = new EntityService();
	        Long locid = es.readEntityGlobalID(globalID);
			
			obj.remove("globalId");
			String globalIdURL =WebServiceURLs.getURL()+"/instances/"+locid.toString(); 

			obj.addProperty("globalId", globalIdURL);
		} catch(NullPointerException e) {
			
		}
		
	


		//convert from global concept to local one
		
		Long conceptTypeID = new KnowledgeService().readConceptGUID(typeId);

		EntityTypeService ets = new EntityTypeService();
		/////////////////!!!!!!!!!!!!!IMPORTANT CHANGE THE ETYPE ID!!!!!!!!!!!!!!///////////
		EntityType etype = ets.getEntityTypeByConcept(conceptTypeID);

		List<IAttributeDef> attrDefs = etype.getAttributeDefs();

		JsonArray attrArray = (JsonArray) obj.get("attributes");
		JsonArray attrArrayUpdated = new JsonArray();
		obj.remove("attributes");
		for (JsonElement attr : attrArray) {

			JsonObject attrObj = (JsonObject) attr;

			Long attrGlobalConceptID = attrObj.get("conceptId").getAsLong();
			obj.remove("conceptId");
			Long attrConceptID = new KnowledgeService().readConceptGUID(attrGlobalConceptID);

			//	System.out.println(attrConceptID);
			for (IAttributeDef atDef : attrDefs) {
				AttributeDef ad = (AttributeDef) atDef;
				//System.out.println(ad.getConceptId());
				//System.out.println(attrConceptID);
				if (ad.getConceptId() == attrConceptID) {

					String name = atDef.getName().getString(Locale.ENGLISH);
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
		//System.out.println(WebServiceURLs.getURL());
		Response response = Request.Post(WebServiceURLs.getURL() + "/data/export")
				.bodyForm(Form.form().add("entityBase", "1").add("fileName", fileName).add("id", formatedEntities).add("maxDepth", "1").build())
				.execute();
		String content = response.returnContent().asString();
		System.out.print("File: " + content);
		return Long.parseLong(content);
	}

	public Long methodPostRDF(List<Long> entitiesId, String fileName) throws ClientProtocolException, IOException {

		String formatedEntities = entitiesId.toString().replace("[", "").replace("]", "").replace(" ", "");
		System.out.println(WebServiceURLs.getURL());
		Response response = Request.Post(WebServiceURLs.getURL() + "/data/exportRDF")
				.bodyForm(Form.form().add("entityBase", "1").add("fileName", fileName).add("id", formatedEntities).add("maxDepth", "1").add("namespace", "http://www.w3.org/1999/02/22-rdf-syntax-ns").build())
				.execute();
		String content = response.returnContent().asString();
		System.out.print("File: " + content);
		return Long.parseLong(content);
	}

	public InputStream methodGet(Long id, String fileName) throws ClientProtocolException, IOException {
		InputStream is = Request.Get(WebServiceURLs.getURL() + "/files/" + id)
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
