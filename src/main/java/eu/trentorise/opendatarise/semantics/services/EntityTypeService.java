package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.KbClient;
import it.unitn.disi.sweb.webapi.model.filters.AttributeDefinitionFilter;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import it.unitn.disi.sweb.webapi.model.kb.KnowledgeBase;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.model.IEtypeSearchResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.model.EtypeSearchResult;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 27 Feb 2014
 * 
 */
public class EntityTypeService implements IEntityTypeService {

	public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH = 0.3;
	private static final Comparator SINGLE = new ValueComparator();


	public List<IEntityType> getAllEntityTypes() {
		KbClient kbClient = new KbClient(getClientProtocol());
		//TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
		List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
		long kbId =  kbList.get(0).getId();
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexTypeFilter ctFilter= new ComplexTypeFilter();
		ctFilter.setIncludeRestrictions(true);
		ctFilter.setIncludeAttributes(true);
		ctFilter.setIncludeAttributesAsProperties(true);

		List<ComplexType> complexTypeList= ctc.readComplexTypes(kbId, null,null,ctFilter);

		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		List<IEntityType> etypes = new ArrayList<IEntityType>();

		for(ComplexType cType: complexTypeList){
			EntityType eType = new EntityType(cType);
			AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
			adf.setIncludeRestrictions(true);

			List<AttributeDefinition>  attrDefList = attrDefs.readAttributeDefinitions(cType.getId(), null, null, adf);

			List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();
			for (AttributeDefinition attrDef: attrDefList){
				IAttributeDef attributeDef = new AttributeDef(attrDef);
				//System.out.println(attributeDef.toString());
				attributeDefList.add(attributeDef);
			}
			eType.setAttrs(attributeDefList);
			//	System.out.println(eType.toString());

			etypes.add(eType);
		}
		return etypes;
	}

	public EntityType getEntityType(long id){
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexTypeFilter ctFilter= new ComplexTypeFilter();
		ctFilter.setIncludeRestrictions(true);
		ctFilter.setIncludeAttributes(true);
		ctFilter.setIncludeAttributesAsProperties(true);
		ComplexType complexType = ctc.readComplexType(id, ctFilter);
		
		EntityType eType = new EntityType(complexType);
		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
		adf.setIncludeRestrictions(true);
		List<AttributeDefinition>  attrDefList = attrDefs.readAttributeDefinitions(id, null, null, adf);
		List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();
		
		for (AttributeDefinition attrDef: attrDefList){
			
			IAttributeDef attributeDef = new AttributeDef(attrDef);
			attributeDefList.add(attributeDef);
		}
		eType.setAttrs(attributeDefList);
		return eType;
	}

	public void addAttributeDefToEtype(IEntityType entityType,
			IAttributeDef attrDef) {
		EntityType eType = (EntityType)entityType;

		eType.addAttributeDef(attrDef);

	}

	public void addUniqueIndexToEtype(IEntityType entityType,
			IUniqueIndex uniqueIndex) {
		// TODO Auto-generated method stub
	}

	/** The method returns client protocol 
	 * @return returns an instance of ClientProtocol that contains information where to connect(Url adress and port) and locale
	 */

	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}

	public List<IEntityType> getEntityTypes(List<String> URLs) {
		List<IEntityType> etypes = new ArrayList<IEntityType>();

		for (String url: URLs){
			etypes.add(getEntityType(url));
		}
		return etypes;
	}

	public EntityType getEntityType(String URL) {
		String s = URL.substring(URL.indexOf("es/") + 3);
		Long typeID = Long.parseLong(s);
		return getEntityType(typeID);
	}

	public List<IEtypeSearchResult> searchEntityTypes(String partialName) {
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		List<IEtypeSearchResult> etypesSortedSearch=new ArrayList<IEtypeSearchResult>();
		List<ComplexType> complexTypeList= ctc.readComplexTypes(1L, null,null,null);
		HashMap<ComplexType, Double> ctypeMap= new HashMap<ComplexType, Double>();   

		for (ComplexType cType: complexTypeList){
			double score = scoreName(partialName,cType.getName().get("en"));
			ctypeMap.put(cType, score);
		}

		List<ComplexType> ctypeSorted = getKeysSortedByValue(ctypeMap);

		for (ComplexType cType: ctypeSorted){
			IEtypeSearchResult etype = new EtypeSearchResult(cType);
			etypesSortedSearch.add(etype);
		}

		return etypesSortedSearch;
	}

	private double scoreName(String searchName, String candidateName) {
		if (searchName.equals(candidateName)) return 1.0;

		int editDistance = StringUtils.getLevenshteinDistance(
				searchName, candidateName);

		// Normalize for length:
		double score =
				(double)(candidateName.length() - editDistance) / (double)candidateName.length();

		// Artificially reduce the score if the first letters don't match
		if (searchName.charAt(0) != candidateName.charAt(0)) {
			score = Math.min(score, MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH);
		}

		return Math.max(0.0, Math.min(score, 1.0));
	}

	private static <K, V extends Comparable<? super V>> List<K> getKeysSortedByValue(Map<K, V> map) {
		final int size = map.size();
		final List reusedList = new ArrayList(size);
		final List<Map.Entry<K, V>> meView = reusedList;
		meView.addAll(map.entrySet());
		Collections.sort(meView, SINGLE);
		final List<K> keyView = reusedList;
		for (int i = 0; i < size; i++) {
			keyView.set(i, meView.get(i).getKey());
		}
		return keyView;
	}

	private static final class ValueComparator<V extends Comparable<? super V>>
	implements Comparator<Map.Entry<?, V>> {
		public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	}
}
