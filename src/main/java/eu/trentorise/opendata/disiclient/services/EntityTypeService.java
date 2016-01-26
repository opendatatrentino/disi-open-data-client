package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.disiclient.DisiClientException;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.model.SearchResult;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class EntityTypeService implements IEntityTypeService {

    Logger logger = LoggerFactory.getLogger(EntityService.class);

    public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH = 0.3;
    private static final Comparator SINGLE = new ValueComparator();

    /**
     * TODO this is static but shouldn't be...
     * @since 0.11.1
     */    
    private static Map<Long, ComplexType> swebEtypes = new HashMap();
    
    /**
     * TODO this is static but shouldn't be...
     * Is {@code null} when no population occurred.
     * @since 0.11.1
     */
    @Nullable
    private static Timestamp cachePopulationTime;

    /**
     * Fetches and caches all sweb complex types, which are also patched with
     * singular attr defs fetches for... some reason.
     *
     * @since 0.11.1
     */
    public Map<Long, ComplexType> readAllSwebComplexTypes() {
        if (cachePopulationTime != null) {
            return Collections.unmodifiableMap(swebEtypes);
        }
        
        logger.info("Found empty etype cache, going to populate it...");

        KbClient kbClient = new KbClient(getClientProtocol());

        //TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
        List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
        logger.warn("The Knowledge base is set to default (the first KB from the returned list of KB).");
        long kbId = kbList.get(0).getId();

        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeRestrictions(true);
        ctFilter.setIncludeAttributes(true);
        ctFilter.setIncludeAttributesAsProperties(true);

        List<ComplexType> complexTypeList = ctc.readComplexTypes(kbId, null, null, ctFilter);

        AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
        Map<Long, ComplexType> etypes = new HashMap();

        for (ComplexType cType : complexTypeList) {

            AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
            adf.setIncludeRestrictions(true);

            List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(cType.getId(), null, null, adf);

            List<AttributeDefinition> attributeDefList = new ArrayList();
            for (AttributeDefinition attrDef : attrDefList) {
                //System.out.println(attributeDef.toString());
                attributeDefList.add(attrDef);
            }
            cType.setAttributes(attributeDefList);
            //	System.out.println(eType.toString());

            etypes.put(cType.getId(), cType);
        }
        swebEtypes = etypes;        
        cachePopulationTime = new Timestamp(new Date().getTime());
        return etypes;
    }

    public List<IEntityType> getAllEntityTypes() {
        List<IEntityType> ret = new ArrayList();
        Map<Long, ComplexType> swebCtypes = readAllSwebComplexTypes();
        for (ComplexType swebCtype : swebCtypes.values()) {
            EntityType oeEtype = new EntityType(swebCtype);
            ret.add(oeEtype);
        }
        return ret;
    }

    public EntityType getEntityTypeByConcept(Long conceptId) {
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeRestrictions(true);
        ctFilter.setIncludeAttributes(true);
        ctFilter.setIncludeAttributesAsProperties(true);

        logger.warn("The Knowledge base is set to default: '1'.");

        List<ComplexType> complexTypes = ctc.readComplexTypes(1L, conceptId, null, ctFilter);

        ComplexType complexType = complexTypes.get(0);
        if (complexTypes.size() > 1) {
            logger.warn("There are " + complexTypes.size() + " Entity types for a given concept. The first one will be returned!.");
        }
        EntityType eType = new EntityType(complexType);
        AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
        AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
        adf.setIncludeRestrictions(true);
        List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(eType.getGUID(), null, null, adf);
        List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();

        for (AttributeDefinition attrDef : attrDefList) {

            IAttributeDef attributeDef = new AttributeDef(attrDef);
            attributeDefList.add(attributeDef);
        }
        eType.setAttrs(attributeDefList);
        swebEtypes.put(complexType.getId(), complexType);
        return eType;
    }

    /**
     *  Returns the cached etype or fetches one from the server.
    */
    public EntityType getEntityType(long id) {
        if (cachePopulationTime == null){
                readAllEntityTypes();
        }
        if (swebEtypes.containsKey(id)) {
            return new EntityType(swebEtypes.get(id));
        } else {                                
            throw new NotFoundException("Can't find etype with local id " + id + " in cache!");
        }

    }

    public void addAttributeDefToEtype(IEntityType entityType,
            IAttributeDef attrDef) {
        EntityType eType = (EntityType) entityType;

        eType.addAttributeDef(attrDef);

    }

    public void addUniqueIndexToEtype(IEntityType entityType,
            IUniqueIndex uniqueIndex) {
        throw new UnsupportedOperationException("todo to implement");
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

    public List<IEntityType> getEntityTypes(List<String> URLs) {
        List<IEntityType> etypes = new ArrayList<IEntityType>();

        for (String url : URLs) {
            etypes.add(getEntityType(url));
        }
        return etypes;
    }

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

    public List<ISearchResult> searchEntityTypes(String partialName, Locale locale) {
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        List<ISearchResult> etypesSortedSearch = new ArrayList();
        List<ComplexType> complexTypeList = ctc.readComplexTypes(1L, null, null, null);
        HashMap<ComplexType, Double> ctypeMap = new HashMap<ComplexType, Double>();

        for (ComplexType cType : complexTypeList) {

            double score = scoreName(partialName, cType.getName().get(TraceProvUtils.localeToLanguageTag(locale)));
            ctypeMap.put(cType, score);

        }

        List<ComplexType> ctypeSortedEN = getKeysSortedByValue(ctypeMap);

        for (ComplexType cType : ctypeSortedEN) {
            System.out.println(cType.getName().get("it"));

            ISearchResult etype = new SearchResult(cType);

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

    /**
     *  @since 0.11.1
     */    
    @Override
    public void refreshEtypes() {
        // todo naive but can work
        swebEtypes = new HashMap();
        cachePopulationTime = null;
        getAllEntityTypes();
    }

    /**
     *  @since 0.11.1
     */    
    @Override
    public boolean isEtypeCached(String etypeUrl) {
        return swebEtypes.containsKey(WebServiceURLs.urlToEtypeID(etypeUrl));
    }

    /**
     *  @since 0.11.1
     */    
    @Override
    public IEntityType getEtype(String etypeUrl) {
        ComplexType retSweb = swebEtypes.get(WebServiceURLs.urlToEtypeID(etypeUrl));
        if (retSweb == null) {
            throw new NotFoundException("Requested etype " + etypeUrl + " in cache, couldn't find it!");
        } else {
            return new EntityType(retSweb);

        }
    }

    private static final class ValueComparator<V extends Comparable<? super V>>
            implements Comparator<Map.Entry<?, V>> {

        public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /**
     * @since 0.11.1
     * @throws DisiClientException if not found
     */
    public ComplexType readSwebRootStructure() {
        Map<Long, ComplexType> ctypes = readAllSwebComplexTypes();
        for (ComplexType ctype : ctypes.values()) {
            if (ctype.getName().get("en").equals("Structure")) {
                return ctype;
            }
        }
        throw new DisiClientException("Couldn't find root structure named 'Structure'!");
    }

    public IEntityType getRootStructure() {
        return new EntityType(readSwebRootStructure());
    }

    /**
     *
     * @since 0.11.1
     * @throws DisiClientException if not found
     */
    public it.unitn.disi.sweb.webapi.model.kb.types.ComplexType readSwebEtype(Long id) {
        checkNotNull(id, "Found null sweb etype id!");
        Collection<ComplexType> ctypes = readAllSwebComplexTypes().values();
        for (ComplexType ctype : ctypes) {
            if (ctype.getId().equals(id)) {
                return ctype;
            }
        }
        throw new DisiClientException("Couldn't find etype with id " + id);
    }

    /**
     *
     * @since 0.11.1
     * @throws DisiClientException if not found
     */
    public it.unitn.disi.sweb.webapi.model.kb.types.EntityType readSwebRootEtype() {
        Collection<ComplexType> ctypes = readAllSwebComplexTypes().values();
        for (ComplexType ctype : ctypes) {
            if (ctype.getName().get("en").equals("Entity")) {
                return (it.unitn.disi.sweb.webapi.model.kb.types.EntityType) ctype;
            }
        }
        throw new DisiClientException("Couldn't find root etype named 'Entity'!!");
    }

    public IEntityType getRootEtype() {
        return new EntityType(readSwebRootEtype());
    }
}
