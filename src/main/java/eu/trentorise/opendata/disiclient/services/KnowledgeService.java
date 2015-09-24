package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClientException;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.client.kb.VocabularyClient;
import it.unitn.disi.sweb.webapi.model.kb.vocabulary.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.traceprov.types.Concept;
import javax.annotation.Nullable;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 *
 */
public class KnowledgeService implements IKnowledgeService {

    private static final Logger LOG = LoggerFactory.getLogger(KnowledgeService.class);

    private static final long ROOT_CONCEPT_ID = 1L;
    private static final long ROOT_GLOBAL_CONCEPT_ID = 1L;
    
    public static final long NAME_CONCEPT_ID = 2L;
    public static final long NAME_GLOBAL_CONCEPT_ID = 2L;
        
    public static final long DESCRIPTION_CONCEPT_ID = 3L;
    public static final long DESCRIPTION_GLOBAL_CONCEPT_ID = 3L;

    public static final long CLASS_CONCEPT_ID = 42806L;
    public static final long CLASS_GLOBAL_CONCEPT_ID = 43482L;
    
    
    public static final Long PART_OF_CONCEPT_ID1 = 5l;
    public static final Long PART_OF_CONCEPT_ID2 = 22l;
    public static final long CONTACT_CONCEPT_ID = 111001;

    private static final int CACHE_SIZE = 1000;
    private final LoadingCache<Long, it.unitn.disi.sweb.webapi.model.kb.concepts.Concept> conceptCacheById;
    private final LoadingCache<Long, it.unitn.disi.sweb.webapi.model.kb.concepts.Concept> conceptCacheByGuid;
    private DisiEkb ekb;
    
    
    KnowledgeService(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
        conceptCacheByGuid = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                // todo think about removal .removalListener(MY_LISTENER)
                .build(
                        new CacheLoader<Long, it.unitn.disi.sweb.webapi.model.kb.concepts.Concept>() {
                            @Nullable
                            @Override
                            public it.unitn.disi.sweb.webapi.model.kb.concepts.Concept load(Long conceptGuid) {
                                LOG.info("Couldn't find concept with global id " + conceptGuid + " in cache, fetching it.");
                                ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
                                LOG.warn("todo - fixed entity Base = 1");
                                List<it.unitn.disi.sweb.webapi.model.kb.concepts.Concept> concepts = client.readConcepts(1L, conceptGuid, null, null, null, null);
                                if (concepts.isEmpty()) {
                                    return null;
                                } else {
                                    if (concepts.size() > 1) {
                                        LOG.warn("todo - only the first concept is returned. The number of returned concepts were: " + concepts.size());
                                    }                  
                                    it.unitn.disi.sweb.webapi.model.kb.concepts.Concept conc = concepts.get(0);
                                    conceptCacheById.put(conc.getId(), conc);
                                    return conc;
                                }

                            }
                        });
        conceptCacheById = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                // todo think about removal .removalListener(MY_LISTENER)
                .build(
                        new CacheLoader<Long, it.unitn.disi.sweb.webapi.model.kb.concepts.Concept>() {
                            @Nullable
                            @Override
                            public it.unitn.disi.sweb.webapi.model.kb.concepts.Concept load(Long conceptId) {
                                LOG.info("Couldn't find concept with id " + conceptId + " in cache, fetching it.");
                                ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
                                it.unitn.disi.sweb.webapi.model.kb.concepts.Concept conc = client.readConcept(conceptId, false);
                                if (conc == null) {
                                    return null;
                                } else {                                    
                                    conceptCacheByGuid.put(conc.getGlobalId(), conc);
                                    return conc;
                                }

                            }
                        });
    }

    @Override
    public List<Concept> readConcepts(List<String> urls) {
        List<Concept> concepts = new ArrayList();

        for (String url : urls) {
            Concept c = readConcept(url);
            concepts.add(c);
        }
        return concepts;
    }

    public it.unitn.disi.sweb.webapi.model.kb.concepts.Concept readConceptById(Long conceptId) {
        checkNotNull(conceptId);
        it.unitn.disi.sweb.webapi.model.kb.concepts.Concept cached = conceptCacheById.getIfPresent(conceptId);
        if (cached == null) {
            return conceptCacheById.getUnchecked(conceptId);
        } else {
            LOG.info("Requested concept with id " + conceptId + " was found in client cache.");
            return cached;
        }

    }

    public it.unitn.disi.sweb.webapi.model.kb.concepts.Concept readConceptByGuid(Long conceptGuid) {
        checkNotNull(conceptGuid);
        it.unitn.disi.sweb.webapi.model.kb.concepts.Concept cached = conceptCacheByGuid.getIfPresent(conceptGuid);
        if (cached == null) {
            return conceptCacheByGuid.getUnchecked(conceptGuid);
        } else {
            LOG.info("Requested concept with id " + conceptGuid + " was found in client cache.");
            return cached;
        }
    }

    @Override
    public Concept readConcept(String url) {
        return ekb.getConverter().swebConceptToOeConcept(readConceptById(SwebConfiguration.getUrlMapper().conceptUrlToId(url)));
    }

    @Override
    public Concept readRootConcept() {
        return readConcept(SwebConfiguration.getUrlMapper().conceptIdToUrl(ROOT_CONCEPT_ID));
    }

    @Override
    public List<SearchResult> searchConcepts(String partialName, Locale locale
    ) {

        LOG.warn("TODO - SETTING CONCEPT PARTIAL NAME TO LOWERCASE");
        String lowerCasePartialName = partialName.toLowerCase(locale);

        List<SearchResult> conceptRes = new ArrayList();

        ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
        LOG.warn("Knowledge base is set to default (1)");
        List<it.unitn.disi.sweb.webapi.model.kb.concepts.Concept> concepts = client.readConcepts(1L, null, lowerCasePartialName, null, null, null);

        for (it.unitn.disi.sweb.webapi.model.kb.concepts.Concept c : concepts) {            
            SearchResult sr = ekb.getConverter().makeSearchResult(c);
            conceptRes.add(sr);
        }

        return conceptRes;
    }

    /**
     * The maximum distance between two concepts todo super arbitrary number
     *
     * @return
     */
    public int getConceptHierarchyDiameter() {
        return 50;
    }

    /**
     * Returns the distance between two concept. The method uses LCA approach.
     *
     * @param source source concept
     * @param target target concept
     * @return
     */
    public double getConceptsDistance(long source, long target) {
        if ((source < 0) || (target < 0)) {
            throw new IllegalArgumentException("Invalid concept ids: source " + source + ", target " + target);
        }
        ConceptClient cClient = new ConceptClient(SwebConfiguration.getClientProtocol());
        Integer distanceInteger = cClient.getDistanceUsingLca(source, target);
        if (distanceInteger == null) {
            throw new DisiClientException("Server returned null distance between concepts!");
        }
        int distanceInt = (int) distanceInteger;
        if (distanceInt < 0) {
            return 1.0;
        }
        if (Math.abs(distanceInt) == 1) {
            return 0.0;
        }
        return distanceInt * 1.0 / getConceptHierarchyDiameter();
    }

    @Override
    public double getConceptsDistance(String sourceUrl, String targetUrl) {
        return getConceptsDistance(SwebConfiguration.getUrlMapper().conceptUrlToId(sourceUrl),
                SwebConfiguration.getUrlMapper().conceptUrlToId(sourceUrl));
    }

    public Map<String, Long> readVocabularies() {
 	Map<String, Long> mapVocabs = new HashMap();
 	VocabularyClient vc = new VocabularyClient(SwebConfiguration.getClientProtocol());
 	List<Vocabulary> vocabs = vc.readVocabularies(1L, null, null);
 	for (Vocabulary v : vocabs) {
 	    mapVocabs.put(v.getLanguageCode(), v.getId());
 	}
 	return mapVocabs;
     }
    
}
