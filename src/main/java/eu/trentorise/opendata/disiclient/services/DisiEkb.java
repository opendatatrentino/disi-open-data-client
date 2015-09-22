package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import eu.trentorise.opendata.schemamatcher.odr.impl.MatchingService;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.Converter;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.services.IEkb;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Leoni
 *
 */
public class DisiEkb implements IEkb {

    public static final String PROPERTIES_PREFIX = "sweb.webapi";
    
    private static final Logger LOG = LoggerFactory.getLogger(DisiEkb.class);
    
    private Converter converter;
    private NlpService NLPService;
    private KnowledgeService knowledgeService;
    private MatchingService schemaMatchingService;
    private IdentityService identityService;
    private EtypeService entityTypeService;
    private EntityService entityService;
    /** disi client specific */
    private Search searchService;
    /** disi client specific */
    private EntityExportService entityExportService;
    
    private List<Locale> defaultLocales;
    
    private Checker checker;
    
    /**
     * Constructor for the client - by default sets English locale. Note that to
     * complete initialization {@link #setProperties(java.util.Map) } MUST be
     * called!
     */
     public DisiEkb() {		     
        this.defaultLocales = ImmutableList.of(Locale.ENGLISH);            
    }

    @Override
    public List<Locale> getDefaultLocales() {
        return defaultLocales;
    }

    @Override
    public NlpService getNLPService() {
        return NLPService;
    }

    @Override
    public KnowledgeService getKnowledgeService() {
        return knowledgeService;
    }

    @Override
    public MatchingService getSchemaMatchingService() {
        return schemaMatchingService;
    }

    @Override
    public IdentityService getIdentityService() {
        return identityService;
    }

    @Override
    public EtypeService getEtypeService() {
        return entityTypeService;
    }

    @Override
    public EntityService getEntityService() {
        return entityService;
    }

    @Override
    public List<Locale> getSupportedLocales() {
        List<Locale> ret = new ArrayList();
        LOG.warn("TODO LOCALES SUPPORT IS HARD CODED!");
        ret.add(Locale.ITALIAN);
        ret.add(Locale.ENGLISH);
        return ret;
    }
    
    @Override
    public String getPropertyNamespace() {
        return PROPERTIES_PREFIX;
    }

    /**
     * A call to this method completes the initialization of Disi client.
     * @param properties a map with the 4 mandatory properties of sweb 
     */
    // todo this thing still uses static initializer, probably we will never get read of it
    @Override
    public void setProperties(Map<String, String> properties) {
        checkNotNull(properties);
        SwebConfiguration.init(properties);
        this.converter = Converter.of(this);
        this.NLPService = new NlpService(this);
        this.entityTypeService = new EtypeService(this);
        this.knowledgeService = new KnowledgeService(this);
        this.identityService = new IdentityService(this);
        this.schemaMatchingService = new MatchingService(this);
        this.entityService = new EntityService(this);
        
        // disi specific
        this.searchService = new Search(this);
        this.entityExportService = new EntityExportService(this);
        this.checker = Checker.of(this);
    }

    public Checker getChecker() {
        return checker;
    }

    public Converter getConverter() {
        return converter;
    }
    
    
    /**
     * Disi client specific
     */
    public Search getSearchService() {
        return searchService;
    }

    /**
     * Disi client specific
     */
    public EntityExportService getEntityExportService() {
        return entityExportService;
    }
    
    

    @Override
    public void setDefaultLocales(Iterable<Locale> locales) {
        checkNotNull(locales);        
        if (!locales.iterator().hasNext()) {
            throw new IllegalArgumentException("Locales can't be empty!");
        }
        this.defaultLocales = ImmutableList.copyOf(locales);
    }    

}
