package eu.trentorise.opendata.disiclient.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.KbClient;
import it.unitn.disi.sweb.webapi.model.filters.AttributeDefinitionFilter;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DisiClients;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 * 
 *
 */
public class EntityTypeService implements IEntityTypeService {

	private static final Logger LOG = LoggerFactory.getLogger(EntityService.class);

	public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH = 0.3;
	private static final Comparator SINGLE = new ValueComparator();

        @Override
	public List<IEntityType> getAllEntityTypes() {
		KbClient kbClient = new KbClient(getClientProtocol());

		//TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
		List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
		LOG.warn("The Knowledge base is set to default (the first KB from the returned list of KB).");
		long kbId = kbList.get(0).getId();

		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexTypeFilter ctFilter = new ComplexTypeFilter();
		ctFilter.setIncludeRestrictions(true);
		ctFilter.setIncludeAttributes(true);
		ctFilter.setIncludeAttributesAsProperties(true);
                ctFilter.setIncludeTimestamps(true);
                
		List<ComplexType> complexTypeList = ctc.readComplexTypes(kbId, null, null, ctFilter);

		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		List<IEntityType> etypes = new ArrayList();

		for (ComplexType cType : complexTypeList) {
			EntityType eType = new EntityType(cType);
			AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
			adf.setIncludeRestrictions(true);

			List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(cType.getId(), null, null, adf);

			List<IAttributeDef> attributeDefList = new ArrayList();
			for (AttributeDefinition attrDef : attrDefList) {
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

	public EntityType getEntityTypeByConcept(Long conceptId) {
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexTypeFilter ctFilter = new ComplexTypeFilter();
		ctFilter.setIncludeRestrictions(true);
		ctFilter.setIncludeAttributes(true);
		ctFilter.setIncludeAttributesAsProperties(true);

		LOG.warn("The Knowledge base is set to default: '1'.");

		List<ComplexType> complexTypes = ctc.readComplexTypes(1L, conceptId, null, ctFilter);

		ComplexType complexType = complexTypes.get(0);
		if (complexTypes.size() > 1) {
			LOG.warn("There are " + complexTypes.size() + " Entity types for a given concept. The first one will be returned!.");
		}
		EntityType eType = new EntityType(complexType);
		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
		adf.setIncludeRestrictions(true);
		List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(eType.getGUID(), null, null, adf);
		List<IAttributeDef> attributeDefList = new ArrayList();

		for (AttributeDefinition attrDef : attrDefList) {

			IAttributeDef attributeDef = new AttributeDef(attrDef);
			attributeDefList.add(attributeDef);
		}
		eType.setAttrs(attributeDefList);
		return eType;
	}

        @Override
	public EntityType getEntityType(long id) {
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexTypeFilter ctFilter = new ComplexTypeFilter();
		ctFilter.setIncludeRestrictions(true);
		ctFilter.setIncludeAttributes(true);
		ctFilter.setIncludeAttributesAsProperties(true);
		ComplexType complexType = ctc.readComplexType(id, ctFilter);

		EntityType eType = new EntityType(complexType);
		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
		adf.setIncludeRestrictions(true);
		List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(id, null, null, adf);
		List<IAttributeDef> attributeDefList = new ArrayList();

		for (AttributeDefinition attrDef : attrDefList) {

			IAttributeDef attributeDef = new AttributeDef(attrDef);
			attributeDefList.add(attributeDef);
		}
		eType.setAttrs(attributeDefList);
		return eType;
	}

	

	
	/**
	 * The method returns client protocol
	 *
	 * @return returns an instance of ClientProtocol that contains information
	 * where to connect(Url adress and port) and locale
	 */
	private IProtocolClient getClientProtocol() {
		return WebServiceURLs.getClientProtocol();
	}

        @Override
	public List<IEntityType> getEntityTypes(List<String> URLs) {
		List<IEntityType> etypes = new ArrayList();

		for (String url : URLs) {
			etypes.add(getEntityType(url));
		}
		return etypes;
	}

        @Override
	public EntityType getEntityType(String URL) {
		String s;
		try {
			s = URL.substring(URL.indexOf("es/") + 3);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Long typeID;
		try {
			typeID = Long.parseLong(s);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		return getEntityType(typeID);
	}

        @Override
	public List<SearchResult> searchEntityTypes(String partialName, Locale locale) {
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		List<SearchResult> etypesSortedSearch = new ArrayList();
		List<ComplexType> complexTypeList = ctc.readComplexTypes(1L, null, null, null);
		HashMap<ComplexType, Double> ctypeMap = new HashMap();

		for (ComplexType cType : complexTypeList) {

			double score = scoreName(partialName, cType.getName().get(OdtUtils.localeToLanguageTag(locale)));
			ctypeMap.put(cType, score);

		}

		List<ComplexType> ctypeSortedEN = getKeysSortedByValue(ctypeMap);

		for (ComplexType cType : ctypeSortedEN) {
			System.out.println(cType.getName().get("it"));

			SearchResult etype = DisiClients.makeSearchResult(cType);

			etypesSortedSearch.add(etype);
		}

		return etypesSortedSearch;
	}

	private double scoreName(String searchName, String candidateName) {
		if (searchName.equals(candidateName)) {
			return 1.0;
		}

		int editDistance = StringUtils.getLevenshteinDistance(
				searchName, candidateName);

		// Normalize for length:
			double score
			= (double) (candidateName.length() - editDistance) / (double) candidateName.length();

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

	public List<IEntityType> readAllEntityTypes() {
		return getAllEntityTypes();
	}

	public IEntityType readEntityType(String URL) {
		return getEntityType(URL);
	}

	public IEntityType readRootStructure() {
		return getRootStructure();
	}

	public IEntityType readRootEtype() {
		return getRootEtype();
	}

	public List<IEntityType> readEntityTypes(List<String> URLs) {
		return getEntityTypes(URLs);
	}

	private static final class ValueComparator<V extends Comparable<? super V>>
	implements Comparator<Map.Entry<?, V>> {

		public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	}

	public IEntityType getRootStructure() {
		List<IEntityType> etypes = getAllEntityTypes();
		for (IEntityType etype : etypes) {

			if (etype.getName().string(Locale.ENGLISH).equals("Structure")) {
				return etype;
			}
		}
		return null;
	}

	public IEntityType getRootEtype() {
		List<IEntityType> etypes = getAllEntityTypes();
		for (IEntityType etype : etypes) {

			if (etype.getName().string(Locale.ENGLISH).equals("Entity")) {
				return etype;
			}
		}
		return null;
	}
}
