package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
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

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DisiClients;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

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

    private static final int CACHE_SIZE = 1000;

    @Nullable
    private static Timestamp lastPopulation;

    // todo it's static, but it shouldn't be...
    private static final LoadingCache<Long, AttributeDef> attrDefCache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .expireAfterWrite(0, TimeUnit.MINUTES)
            // todo think about removal .removalListener(MY_LISTENER)
            .build(
                    new CacheLoader<Long, AttributeDef>() {
                        @Override
                        public AttributeDef load(Long id) {
                            LOG.info("Couldn't find attrdef with id " + id + " in cache, loading it....");
                            AttributeDefinitionClient attrDefsClient = new AttributeDefinitionClient(SwebConfiguration.getClientProtocol());
                            AttributeDefinition attrDef = attrDefsClient.readAttributeDefinition(id, null);
                            LOG.info("...attrdef with id " + id + " loaded in cache.");
                            return new AttributeDef(attrDef);                            
                        }
                    });

    // todo it's static, but it shouldn't be...
    private static final LoadingCache<Long, EntityType> etypesCacheById = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .expireAfterWrite(0, TimeUnit.MINUTES)
            // todo think about removal .removalListener(MY_LISTENER)
            .build(
                    new CacheLoader<Long, EntityType>() {
                        @Override
                        public EntityType load(Long id) {
                            LOG.info("Couldn't find etype with " + id + " in cache, loading it....");
                            ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
                            ComplexTypeFilter ctFilter = new ComplexTypeFilter();

                            ctFilter.setIncludeRestrictions(
                                    true);
                            ctFilter.setIncludeAttributes(
                                    true);
                            ctFilter.setIncludeAttributesAsProperties(
                                    true);
                            ComplexType complexType = ctc.readComplexType(id, ctFilter);

                            EntityType etype = new EntityType(complexType);
                            AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(SwebConfiguration.getClientProtocol());
                            AttributeDefinitionFilter adf = new AttributeDefinitionFilter();

                            adf.setIncludeRestrictions(
                                    true);
                            List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(id, null, null, adf);
                            List<IAttributeDef> attributeDefList = new ArrayList();

                            for (AttributeDefinition attrDef : attrDefList) {

                                AttributeDef attributeDef = new AttributeDef(attrDef);
                                attrDefCache.put(attrDef.getId(), attributeDef);
                                attributeDefList.add(attributeDef);
                            }

                            etype.setAttrs(attributeDefList);
                            LOG.info("...etype with id " + id + " loaded in cache.");
                            return etype;

                        }
                    });

    EntityTypeService() {
    }

    public EntityType readEntityTypeByConceptId(Long conceptId) {
        ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
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
        // double read so we read all attr defs properly.
        return etypesCacheById.getUnchecked(complexType.getId());
    }

    public EntityType readEntityType(long id) {
        return etypesCacheById.getUnchecked(id);

    }

    @Override
    public List<SearchResult> searchEntityTypes(String partialName, Locale locale) {
        ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
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

    @Override
    public List<IEntityType> readAllEntityTypes() {
        if (lastPopulation == null) {
            LOG.info("Etype list was never populated, going to do it now...");
            KbClient kbClient = new KbClient(SwebConfiguration.getClientProtocol());

            //TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
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

            AttributeDefinitionClient attrDefsClient = new AttributeDefinitionClient(SwebConfiguration.getClientProtocol());

            for (ComplexType cType : complexTypeList) {

                EntityType etype = new EntityType(cType);
                AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
                adf.setIncludeRestrictions(true);

                List<AttributeDefinition> attrDefList = attrDefsClient.readAttributeDefinitions(cType.getId(), null, null, adf);

                List<IAttributeDef> attributeDefList = new ArrayList();
                for (AttributeDefinition attrDef : attrDefList) {
                    IAttributeDef attributeDef = new AttributeDef(attrDef);
                    attributeDefList.add(attributeDef);
                }
                etype.setAttrs(attributeDefList);

                etypesCacheById.put(etype.getGUID(), etype);
            }
            LOG.info("Finished populating etypes cache.");
            lastPopulation = new Timestamp(new Date().getTime());
        } else {
            LOG.warn("GIVING BACK ALL ETYPES LIST WITHOUT CHECKING STALE ONES!");
        }

        return new ArrayList(etypesCacheById.asMap().values());
    }

    @Override
    public AttributeDef readAttrDef(String url) {
        return readAttrDef(SwebConfiguration.getUrlMapper().attrDefUrlToId(url));
    }

    public AttributeDef readAttrDef(Long id) {
        return attrDefCache.getUnchecked(id);
    }

    @Override
    public EntityType readEntityType(String url) {
        return readEntityType(SwebConfiguration.getUrlMapper().etypeUrlToId(url));
    }

    @Override
    public IEntityType readRootStructure() {
        List<IEntityType> etypes = readAllEntityTypes();
        for (IEntityType etype : etypes) {

            if (etype.getName().string(Locale.ENGLISH).equals("Structure")) {
                return etype;
            }
        }
        LOG.error("COULDN'T FIND ROOT STRUCTURE!!!!!!   RETURNING NULL  - TODO SHOULD THROW EXCEPTION");
        return null;
    }

    @Override
    public IEntityType readRootEtype() {
        List<IEntityType> etypes = readAllEntityTypes();
        for (IEntityType etype : etypes) {
            if (etype.getName().string(Locale.ENGLISH).equals("Entity")) {
                return etype;
            }
        }
        LOG.error("COULDN'T FIND ROOT ETYPE!!!!!!   RETURNING NULL  - TODO SHOULD THROW EXCEPTION");
        return null;

    }

    @Override
    public List<IEntityType> readEntityTypes(Iterable<String> URLs) {
        List<IEntityType> etypes = new ArrayList();

        for (String url : URLs) {
            etypes.add(readEntityType(url));
        }
        return etypes;

    }

    private static final class ValueComparator<V extends Comparable<? super V>>
            implements Comparator<Map.Entry<?, V>> {

        public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

}
