package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.Converter;
import eu.trentorise.opendata.disiclient.UrlMapper;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.KbClient;
import it.unitn.disi.sweb.webapi.model.filters.AttributeDefinitionFilter;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import it.unitn.disi.sweb.webapi.model.kb.KnowledgeBase;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;

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

import eu.trentorise.opendata.semantics.model.entity.Etype;

import eu.trentorise.opendata.semantics.services.IEtypeService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityException;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import it.unitn.disi.sweb.webapi.model.kb.types.EntityType;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 *
 */
public class EtypeService implements IEtypeService {

    private static final Logger LOG = LoggerFactory.getLogger(EntityService.class);

    public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH = 0.3;
    private static final Comparator SINGLE = new ValueComparator();

    private static final int CACHE_SIZE = 1000;

    @Nullable
    private static Timestamp lastPopulation;

    /** todo it's static, but it shouldn't be... */
    private static DisiEkb ekb;

    /** todo it's static, but it shouldn't be... */
    private static UrlMapper um;

    /* todo it's static, but it shouldn't be... */
    private static final LoadingCache<Long, AttributeDefinition> swebAttributeDefinitionsCache = CacheBuilder
	    .newBuilder().maximumSize(CACHE_SIZE).build(new CacheLoader<Long, AttributeDefinition>() {

		@Override
		public AttributeDefinition load(Long id) {
		    LOG.info("Couldn't find attrdef with id " + id + " in cache, fetching it....");
		    AttributeDefinitionClient attrDefsClient = new AttributeDefinitionClient(
			    SwebConfiguration.getClientProtocol());
		    AttributeDefinition ret = attrDefsClient.readAttributeDefinition(id, null);
		    if (ret == null) {
			throw new OpenEntityNotFoundException("Couldn't find sweb attribute definition with id " + id);
		    } else {
			LOG.info("Attrdef with id " + id + " loaded in cache.");
			return ret;
		    }
		}
	    });

    /** todo it's static, but it shouldn't be... */
    private static final LoadingCache<Long, ComplexType> swebEntityTypesCacheById = CacheBuilder
	    .newBuilder()
	    .maximumSize(CACHE_SIZE)
	    .build(new CacheLoader<Long, ComplexType>() {
		@Override
		public ComplexType load(Long id) {
		    LOG.info("Couldn't find etype with id " + id + " in cache, fetching it....");
		    ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
		    ComplexTypeFilter ctFilter = new ComplexTypeFilter();

		    ctFilter.setIncludeRestrictions(true);
		    ctFilter.setIncludeAttributes(true);
		    ctFilter.setIncludeAttributesAsProperties(true);
		    ComplexType complexType = ctc.readComplexType(id, ctFilter);

		    if (complexType == null) {
			throw new OpenEntityNotFoundException("Couldn't find sweb complex type of sweb id " + id);
		    }

		    AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(
			    SwebConfiguration.getClientProtocol());
		    AttributeDefinitionFilter adf = new AttributeDefinitionFilter();

		    adf.setIncludeRestrictions(true);
		    List<AttributeDefinition> swebAttrDefs = attrDefs.readAttributeDefinitions(id, null, null, adf);
		    List<AttributeDefinition> attrDefList = new ArrayList();

		    for (AttributeDefinition swebAttrDef : swebAttrDefs) {
			swebAttributeDefinitionsCache.put(swebAttrDef.getId(), swebAttrDef);
			attrDefList.add(swebAttrDef);
		    }

		    complexType.setAttributes(attrDefList);
		    LOG.info("Complex type with id " + id + " loaded in cache.");
		    return complexType;
		}
	    });

    EtypeService(DisiEkb ekb) {
	checkNotNull(ekb);
	this.ekb = ekb;
	this.um = SwebConfiguration.getUrlMapper();
    }

    /**
     * Returns the sweb complex type with given id if present, or {@code null}
     * otherwise.
     */
    @Nullable
    public ComplexType getSwebCachedComplexType(long complexTypeId) {
	return swebEntityTypesCacheById.getIfPresent(complexTypeId);
    }

    /**
     * 
     * @throws OpenEntityNotFoundException
     */
    public ComplexType readSwebComplexTypeByConceptId(Long conceptId) {
	ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
	ComplexTypeFilter ctFilter = new ComplexTypeFilter();
	ctFilter.setIncludeRestrictions(true);
	ctFilter.setIncludeAttributes(true);
	ctFilter.setIncludeAttributesAsProperties(true);

	LOG.warn("The Knowledge base is set to default: '1'.");

	List<ComplexType> complexTypes = ctc.readComplexTypes(1L, conceptId, null, ctFilter);

	if (complexTypes.isEmpty()) {
	    throw new OpenEntityNotFoundException("Couldn't find sweb complex type of concept id " + conceptId);
	}

	ComplexType complexType = complexTypes.get(0);
	if (complexTypes.size() > 1) {
	    LOG.warn("Searched for etype with concept " + conceptId + ", and " + complexTypes.size()
		    + " results were found. Only the first one will be returned!");
	}

	// double read so we read all attr defs properly.
	return readSwebComplexType(complexType.getId());
    }

    /**
     * 
     * throw OpenEntityNotFoundException if any of the etypes is not present.
     */
    public List<ComplexType> readSwebComplexTypes(Iterable<Long> ids) {
	List<ComplexType> ret = new ArrayList();
	LOG.info("todo - reading etypes one by one (there's no etype export in sweb)");
	for (Long id : ids) {
	    ret.add(readSwebComplexType(id));
	}
	return ret;
    }

    /**
     * 
     * @throw OpenEntityNotFoundException
     */
    public ComplexType readSwebComplexType(long id) {
	ComplexType cached = swebEntityTypesCacheById.getIfPresent(id);

	if (cached == null) {
	    try {
		return swebEntityTypesCacheById.getUnchecked(id);
	    } catch (Exception ex) {
		Throwables.propagateIfPossible(ex.getCause());
		throw new OpenEntityException("Had some issue while reading sweb complex type with sweb id " + id);
	    }
	} else {
	    LOG.info("Requested etype with id " + id + " was found in client cache.");
	    return cached;
	}

    }

    @Override
    public List<SearchResult> searchEtypes(String partialName, Locale locale) {
	List<SearchResult> etypesSortedSearch = new ArrayList();

	HashMap<Etype, Double> ctypeMap = new HashMap();

	List<Etype> etypes = readAllEtypes();

	for (Etype etype : etypes) {
	    double score = scoreName(partialName, etype.getName().some(locale).str());
	    ctypeMap.put(etype, score);
	}

	List<Etype> ctypeSortedEN = getKeysSortedByValue(ctypeMap);

	for (Etype cType : ctypeSortedEN) {
	    SearchResult etype = SearchResult.of(cType.getConceptId(), cType.getName());
	    etypesSortedSearch.add(etype);
	}

	return etypesSortedSearch;
    }

    private double scoreName(String searchName, String candidateName) {
	if (searchName.equals(candidateName)) {
	    return 1.0;
	}

	int editDistance = StringUtils.getLevenshteinDistance(searchName, candidateName);

	// Normalize for length:
	double score = (double) (candidateName.length() - editDistance) / (double) candidateName.length();

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

    @Override
    public List<Etype> readAllEtypes() {
	if (lastPopulation == null) {
	    LOG.info("Etype list was never populated, going to do it now...");
	    KbClient kbClient = new KbClient(SwebConfiguration.getClientProtocol());

	    // TODO decide what to do with knowledge base id which knowldege
	    // base id to take the first one?
	    List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
	    LOG.warn("The Knowledge base is set to default (the first KB from the returned list of KB).");
	    long kbId = kbList.get(0).getId();

	    ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
	    ComplexTypeFilter ctFilter = new ComplexTypeFilter();
	    ctFilter.setIncludeRestrictions(true);
	    ctFilter.setIncludeAttributes(true);
	    ctFilter.setIncludeAttributesAsProperties(true);
	    ctFilter.setIncludeTimestamps(true);

	    List<ComplexType> complexTypeList = ctc.readComplexTypes(kbId, null, null, ctFilter);

	    AttributeDefinitionClient attrDefsClient = new AttributeDefinitionClient(
		    SwebConfiguration.getClientProtocol());

	    for (ComplexType cType : complexTypeList) {
		AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
		adf.setIncludeRestrictions(true);

		List<AttributeDefinition> swebAttrDefinitionList = attrDefsClient
			.readAttributeDefinitions(cType.getId(), null, null, adf);

		List<AttributeDefinition> attributeDefList = new ArrayList();
		for (AttributeDefinition swebAttributeDefinition : swebAttrDefinitionList) {

		    swebAttributeDefinitionsCache.put(swebAttributeDefinition.getId(), swebAttributeDefinition);
		    attributeDefList.add(swebAttributeDefinition);
		}
		cType.setAttributes(attributeDefList);

		swebEntityTypesCacheById.put(cType.getId(), cType);
	    }
	    LOG.info("Finished populating etypes cache.");
	    lastPopulation = new Timestamp(new Date().getTime());
	} else {
	    LOG.warn("RETURNING CACHED ETYPES LIST WITHOUT CHECKING STALE ONES!");
	}
	ConcurrentMap<Long, ComplexType> retMap = swebEntityTypesCacheById.asMap();
	List<Etype> ret = new ArrayList();
	for (ComplexType swebCt : retMap.values()) {
	    ret.add(ekb.getConverter().swebComplexTypeToOeEtype(swebCt));
	}
	return ret;
    }

    @Override
    public AttrDef readAttrDef(String url) {
	return readAttrDef(um.attrDefUrlToId(url));
    }

    public AttrDef readAttrDef(Long id) {
	AttributeDefinition cached = swebAttributeDefinitionsCache.getIfPresent(id);
	if (cached == null) {
	    try {
		cached = swebAttributeDefinitionsCache.getUnchecked(id);
	    } catch (Exception ex) {
		Throwables.propagateIfPossible(ex.getCause());
		throw new OpenEntityException(
			"Something wrong occurred when readeing attribute definition with sweb id " + id);
	    }
	} else {
	    LOG.info("Requested attrDef with id " + id + " was found in client cache.");
	}

	@Nullable
	ComplexType ct = null;
	if (DataType.COMPLEX_TYPE.equals(cached.getDataType())) {
	    ct = ekb.getEtypeService().getSwebCachedComplexType(Converter.swebAttrDefToRangeEntityTypeId(cached));
	}

	return Converter.swebAttributeDefToOeAttrDef(cached, ct);
    }

    @Override
    public Etype readEtype(String url) {
	return ekb.getConverter().swebComplexTypeToOeEtype(readSwebComplexType(um.etypeUrlToId(url)));
    }

    @Override
    public Etype readRootStruct() {
	List<Etype> etypes = readAllEtypes();
	for (Etype etype : etypes) {

	    if (etype.getName().string(Locale.ENGLISH).equals("Structure")) {
		return etype;
	    }
	}
	throw new OpenEntityNotFoundException("Couldn't find root structure!");
    }

    @Override
    public Etype readRootEtype() {
	List<Etype> etypes = readAllEtypes();
	for (Etype etype : etypes) {
	    if (etype.getName().string(Locale.ENGLISH).equals("Entity")) {
		return etype;
	    }
	}
	throw new OpenEntityNotFoundException("Couldn't find root structure!");

    }

    @Override
    public List<Etype> readEtypes(Iterable<String> urls) {
	List<Etype> ret = new ArrayList();

	for (String url : urls) {
	    ret.add(readEtype(url));
	}
	return ret;

    }

    public EntityType readSwebEntityType(Long typeId) {
	return (EntityType) readSwebComplexType(typeId);
    }

    private static final class ValueComparator<V extends Comparable<? super V>> implements Comparator<Map.Entry<?, V>> {

	public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
	    return o2.getValue().compareTo(o1.getValue());
	}
    }

}
