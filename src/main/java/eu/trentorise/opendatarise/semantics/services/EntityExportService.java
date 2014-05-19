package eu.trentorise.opendatarise.semantics.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;


public class EntityExportService {

	private static final String   regexInput = "@type";        
	private static final String   regexOutput = "#type";


	private File exportEntities(List<Long> entitiesID, String filename, Long eb){
		File file = null;

		return file;
	}

	/**The method aims to change  @type keyword reserved by json-LD to #type in the file returned from the server
	 * 
	 */
	private void cleanFile(String fileName, String inputText){

		PrintWriter out;
		try {
			out = new PrintWriter(fileName);
			out.print(inputText);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Method substitute reserved by JSON-LD keywords with @-symbol to #-symbol 
	 * @param input string 
	 * @return output string without @-keywords
	 */
	public String  replaceReservedKeyWords(String input){
		Pattern pattern = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		String output = matcher.replaceAll(regexOutput);     // all matches
		return output;
	} 

	private String readFile(String fileName) throws IOException{
		String input="";

		BufferedReader br = new BufferedReader(new FileReader("file.txt"));
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

}
