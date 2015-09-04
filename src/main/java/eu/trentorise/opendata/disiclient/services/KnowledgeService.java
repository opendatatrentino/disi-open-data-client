package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.DisiClients;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import java.util.concurrent.TimeUnit;
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
    public static final long DESCRIPTION_CONCEPT_ID = 3L;
    public static final long DESCRIPTION_GLOBAL_CONCEPT_ID = 3L;
    
    public static final Long PART_OF_CONCEPT_ID1 = 5l;
    public static final Long PART_OF_CONCEPT_ID2 = 22l;  
    public static final long CONTACT_CONCEPT_ID = 111001;
    
    private static final int CACHE_SIZE = 1000;
    private final LoadingCache<Long, ConceptODR> conceptCacheById;
    private final LoadingCache<Long, ConceptODR> conceptCacheByGuid;

    KnowledgeService() {

        conceptCacheByGuid = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterWrite(0, TimeUnit.MINUTES)
                // todo think about removal .removalListener(MY_LISTENER)
                .build(
                        new CacheLoader<Long, ConceptODR>() {
                            @Nullable
                            @Override
                            public ConceptODR load(Long conceptGuid) {
                                LOG.info("Couldn't find concept with global id " + conceptGuid + " in cache, fetching it.");
                                ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
                                LOG.warn("todo - fixed entity Base = 1");
                                List<Concept> concepts = client.readConcepts(1L, conceptGuid, null, null, null, null);
                                if (concepts.isEmpty()) {
                                    return null;
                                } else {
                                    if (concepts.size() > 1) {
                                        LOG.warn("todo - only the first concept is returned. The number of returned concepts were: " + concepts.size());
                                    }
                                    Concept conc = concepts.get(0);
                                    ConceptODR conceptODR = new ConceptODR(conc);
                                    conceptCacheById.put(conc.getId(), conceptODR);
                                    return conceptODR;
                                }

                            }
                        });
        conceptCacheById = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterWrite(0, TimeUnit.MINUTES)
                // todo think about removal .removalListener(MY_LISTENER)
                .build(
                        new CacheLoader<Long, ConceptODR>() {
                            @Nullable
                            @Override
                            public ConceptODR load(Long conceptId) {
                                LOG.info("Couldn't find concept with id " + conceptId + " in cache, fetching it.");
                                ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
                                Concept conc = client.readConcept(conceptId, false);
                                if (conc == null) {
                                    return null;
                                } else {
                                    ConceptODR conceptODR = new ConceptODR(conc);
                                    conceptCacheByGuid.put(conc.getGlobalId(), conceptODR);
                                    return conceptODR;
                                }

                            }
                        });
    }

    @Override
    public List<IConcept> readConcepts(List<String> urls) {
        List<IConcept> concepts = new ArrayList();

        for (String url : urls) {
            IConcept c = readConcept(url);
            concepts.add(c);
        }
        return concepts;
    }

    public ConceptODR readConceptById(Long conceptId) {
        checkNotNull(conceptId);
        return conceptCacheById.getUnchecked(conceptId);
    }

    public ConceptODR readConceptByGuid(Long conceptGuid) {
        checkNotNull(conceptGuid);
        return conceptCacheByGuid.getUnchecked(conceptGuid);
    }

    @Override
    public IConcept readConcept(String url) {
        return readConceptById(SwebConfiguration.getUrlMapper().conceptUrlToId(url));
    }

    @Override
    public IConcept readRootConcept() {
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
        List<Concept> concepts = client.readConcepts(1L, null, lowerCasePartialName, null, null, null);

        for (Concept c : concepts) {
            ConceptODR codr = new ConceptODR(c);
            SearchResult sr = DisiClients.makeSearchResult(codr);
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

}
