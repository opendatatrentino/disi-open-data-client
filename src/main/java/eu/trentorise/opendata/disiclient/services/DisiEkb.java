package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import eu.trentorise.opendata.disiclient.services.shematching.MatchingService;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;

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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PROPERTIES_PREFIX = "sweb.webapi";

    private INLPService NLPService;
    private IKnowledgeService knowledgeService;
    private ISemanticMatchingService semanticMatchingService;
    private IIdentityService identityService;
    private IEntityTypeService entityTypeService;
    private IEntityService entityService;
    private List<Locale> defaultLocales;

    /**
     * Constructor for the client - by default sets English locale. Note that to
     * complete initialization {@link #setProperties(java.util.Map) } MUST be
     * called!
     */
    public DisiEkb() {
        this.defaultLocales = ImmutableList.of(Locale.ENGLISH);
    }

    
    public List<Locale> getDefaultLocales() {
        return defaultLocales;
    }

    
    public INLPService getNLPService() {
        return NLPService;
    }

    
    public IKnowledgeService getKnowledgeService() {
        return knowledgeService;
    }

    
    public ISemanticMatchingService getSemanticMatchingService() {
        return semanticMatchingService;
    }

    
    public IIdentityService getIdentityService() {
        return identityService;
    }

    
    public IEntityTypeService getEntityTypeService() {
        return entityTypeService;
    }

    
    public IEntityService getEntityService() {
        return entityService;
    }

    
    public List<Locale> getSupportedLocales() {
        List<Locale> ret = new ArrayList<Locale>();
        logger.warn("TODO LOCALES SUPPORT IS HARD CODED!");
        ret.add(Locale.ITALIAN);
        ret.add(Locale.ENGLISH);
        return ret;
    }

    
    public String getPropertyNamespace() {
        return PROPERTIES_PREFIX;
    }

    /**
     * A call to this method completes the initialization of Disi client.
     * @param properties a map with the 4 mandatory properties of sweb 
     */
    
    public void setProperties(Map<String, String> properties) {
        checkNotNull(properties);
        DisiConfiguration.init(properties);
        this.NLPService = new NLPService();
        this.entityTypeService = new EntityTypeService();
        this.knowledgeService = new KnowledgeService();
        this.identityService = new IdentityService();
        this.semanticMatchingService = new MatchingService();
        this.entityService = (IEntityService) new EntityService();
    }

    
    public void setDefaultLocales(Iterable<Locale> locales) {
        checkNotNull(locales);
        if (!locales.iterator().hasNext()) {
            throw new IllegalArgumentException("Locales can't be empty!");
        }
        this.defaultLocales = ImmutableList.copyOf(locales);
    }


	public void setDefaultLocales(List<Locale> locales) {
		// TODO Auto-generated method stub
		
	}

}
