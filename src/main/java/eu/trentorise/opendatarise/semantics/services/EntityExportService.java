package eu.trentorise.opendatarise.semantics.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;


public class EntityExportService {

	private static final String   regexInput = "@type";        
	private static final String   regexOutput = "#type";


	//	/**The method aims to change  @type keyword reserved by json-LD to #type in the file returned from the server
	//	 * 
	//	 */
	//	private void cleanFile(String fileName, String inputText){
	//
	//		PrintWriter out;
	//		try {
	//			out = new PrintWriter(fileName);
	//			out.print(inputText);
	//		} catch (FileNotFoundException e) {
	//			throw new DisiClientException("Error while creating print writer", e);
	//		}
	//
	//	}

	//	/** Method substitute reserved by JSON-LD keywords with @-symbol to #-symbol 
	//	 * @param input string 
	//	 * @return output string without @-keywords
	//	 */
	//	public String  replaceReservedKeyWords(String input){
	//		Pattern pattern = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
	//		Matcher matcher = pattern.matcher(input);
	//		String output = matcher.replaceAll(regexOutput);     // all matches
	//		return output;
	//	} 

	/** Reads file and convert text from it to String
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private String readFile(String fileName) throws IOException{
		String input="";

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String  line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			input = sb.toString();
		} finally {
			br.close();
		}
		return input;
	}

	/** The method generates JSON-LD @context 
	 * @param id
	 * @return
	 */
	public String generateJSONLDContext(Long id){
		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());

		IEntity entity = es.readEntity(id);
		EntityTypeService ets = new EntityTypeService();

		IEntityType etype = ets.getEntityType(entity.getEtype().getURL());
		List<IAttributeDef> attributeDefs = etype.getAttributeDefs();

		for (IAttributeDef attrDef: attributeDefs){
			System.out.println(attrDef.getName().getString(Locale.ENGLISH));
			System.out.println(attrDef.getURL());

		}
		return null;
	}

	//	public void generateContext() throws IOException, JsonLdError{
	//		GsonBuilder builder = new GsonBuilder();
	//		Gson gson = builder.create();
	//		//EntityContexJSONLD context = new EntityContexJSONLD();
	//		context.setAttributeDefUrl("www.example.com");
	//		context.setDataType(DataType.BOOLEAN);
	//
	//		JsonObject obj = new JsonObject();
	//		obj.addProperty("@id", context.getAttributeDefUrl());
	//		obj.addProperty("@type", context.getDataType().toString());
	//
	//		JsonObject jsonObjectAttrdefName = new JsonObject();
	//		jsonObjectAttrdefName.add("Name", obj); 
	//
	//		System.out.println(jsonObjectAttrdefName.toString());
	//	}

	public JsonObject generateEtypeContext(IEntityType etype){
		List<IAttributeDef> attrDefs = etype.getAttributeDefs();
		JsonObject jsonObject = new JsonObject();
		JsonObject finJsonObject = new JsonObject();


		for (IAttributeDef attrDef: attrDefs){

			JsonObject jsonObjectAttr = new JsonObject();
			jsonObjectAttr.addProperty("@id", attrDef.getURL());
			if(attrDef.getDataType().equals("oe:structure")){
				jsonObjectAttr.addProperty("@type", attrDef.getRangeEtypeURL());
			} else 
			{
				jsonObjectAttr.addProperty("@type", attrDef.getDataType());

			}
			finJsonObject.add(attrDef.getName().getString(Locale.ENGLISH), jsonObjectAttr);
		}

		//	finJsonObject.add("@context", jsonObject);
		finJsonObject.addProperty("xsd", "http://www.w3.org/2001/XMLSchema#");
		finJsonObject.addProperty("oe", WebServiceURLs.getURL()+"/datatypes");

		//System.out.println(finJsonObject);
		return finJsonObject;
	}

	public void  convertToJsonLd(InputStream inputStream, Writer writer) throws IOException{


		//FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(writer);

		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream));
		while(parser.hasNext())
		{
			JsonObject obj = semantifyJsonObject(parser.next());
			bw.write(obj.toString());
			bw.newLine();
			System.out.println(obj.toString());
		}
		bw.close();
	}

	/** Add attribute name instead of "values"
	 * @param jsonInput
	 * @return
	 * @throws IOException
	 */
	public  JsonObject semantifyJsonObject(JsonElement jsonInput) throws IOException{

		JsonObject o = (JsonObject)jsonInput;
		JsonObject obj = (JsonObject) o.get("entity");
		Long typeId = obj.get("typeId").getAsLong();
		obj.remove("creationDate");
		obj.remove("modificationDate");
		obj.remove("dataType");
		obj.remove("@type");
		obj.remove("typeId");
		obj.remove("globalId");



		//convert from global concept to local one
		ConceptODR codr = new ConceptODR();
		Long conceptTypeID = codr.readConceptGUID(typeId);


		EntityExportService ess = new EntityExportService();
		EntityTypeService ets = new EntityTypeService();
		/////////////////!!!!!!!!!!!!!IMPORTANT CHANGE THE ETYPE ID!!!!!!!!!!!!!!///////////
		EntityType etype = ets.getEntityTypeByConcept(conceptTypeID);

		List<IAttributeDef> attrDefs = etype.getAttributeDefs();

		JsonArray attrArray = (JsonArray) obj.get("attributes");
		JsonArray attrArrayUpdated = new JsonArray();
		obj.remove("attributes");
		for (JsonElement attr: attrArray){


			JsonObject attrObj = (JsonObject)attr;

			Long attrGlobalConceptID = attrObj.get("conceptId").getAsLong();
			obj.remove("conceptId");
			ConceptODR conceptOdr = new ConceptODR(); 
			Long attrConceptID = codr.readConceptGUID(attrGlobalConceptID);

			//	System.out.println(attrConceptID);
			for(IAttributeDef atDef: attrDefs)
			{
				AttributeDef ad = (AttributeDef) atDef;
				//System.out.println(ad.getConceptId());
				//System.out.println(attrConceptID);
				if(ad.getConceptId()==attrConceptID){


					String name = atDef.getName().getString(Locale.ENGLISH);
					//	System.out.println(name);
					attrObj.remove("creationDate");
					attrObj.remove("modificationDate");
					attrObj.remove("dataType");
					attrObj.remove("conceptId");

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

	public Long methodPost(List<Long> entitiesId, String fileName) throws ClientProtocolException, IOException{

		String formatedEntities = entitiesId.toString().replace("[", "").replace("]", "").replace(" ", "");
		System.out.println(WebServiceURLs.getURL());
		Response response = Request.Post(WebServiceURLs.getURL()+"/data/export")
				.bodyForm(Form.form().add("entityBase", "1").add("fileName", fileName).add("id", formatedEntities).build())
				.execute();
		String content = response.returnContent().asString();
		System.out.print("File: "+content);
		return Long.parseLong(content);
	}

	public InputStream methodGet(Long id, String fileName) throws ClientProtocolException, IOException{
		InputStream is =Request.Get(WebServiceURLs.getURL()+"/files/"+id)
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
