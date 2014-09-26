package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.disiclient.services.shematching.MatchingService;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Leoni
 *
 */
public class DisiEkb implements IEkb {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private INLPService NLPService;
    private IKnowledgeService knowledgeService;
    private ISemanticMatchingService semanticMatchingService;
    private IIdentityService identityService;
    private IEntityTypeService entityTypeService;
    private IEntityService entityService;
    private List<Locale> defaultLocales;

    public DisiEkb() {
        this.NLPService = new NLPService();
        this.entityTypeService = new EntityTypeService();
        this.knowledgeService = new KnowledgeService();
        this.identityService = new IdentityService();
        this.semanticMatchingService = new MatchingService();
        this.entityService = (IEntityService) new EntityService(WebServiceURLs.getClientProtocol());
        List<Locale> locs = new ArrayList<Locale>();
        locs.add(Locale.ENGLISH);
        this.defaultLocales = Collections.unmodifiableList(locs);
    }

    public void setDefaultLocales(List<Locale> locales) {
        this.defaultLocales = Collections.unmodifiableList(new ArrayList<Locale>(locales));
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

}
