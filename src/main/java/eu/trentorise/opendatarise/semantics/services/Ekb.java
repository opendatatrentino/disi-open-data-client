package eu.trentorise.opendatarise.semantics.services;


import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.IIdentityService;
import eu.trentorise.opendata.semantics.services.IKnowledgeService;
import eu.trentorise.opendata.semantics.services.INLPService;
import eu.trentorise.opendata.semantics.services.ISearchService;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import eu.trentorise.opendatarise.semantics.services.shematching.MatchingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author David Leoni
 * 
 */
public class Ekb implements IEkb {
 
    
    private  INLPService NLPService;
    private  IKnowledgeService knowledgeService;
    private  ISemanticMatchingService semanticMatchingService;
    private  IIdentityService identityService;
    private  IEntityTypeService entityTypeService;
    private  IEntityService entityService;
    private  ISearchService searchService;
    private List<Locale> defaultLocales;

    public Ekb() {        
        this.NLPService = new NLPService();
        this.entityTypeService = new EntityTypeService();
        this.knowledgeService = new KnowledgeService();
        this.identityService = new IdentityService();
        this.semanticMatchingService = new MatchingService();      
        this.entityService = new EntityService(WebServiceURLs.getClientProtocol());
        this.searchService = new Search(WebServiceURLs.getClientProtocol());
        List<Locale> locs = new ArrayList();
        locs.add(Locale.ENGLISH);
        this.defaultLocales = Collections.unmodifiableList(locs);
    }
    

    public void setDefaultLocales(List<Locale> locales) {
        this.defaultLocales = Collections.unmodifiableList(new ArrayList(locales));
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

    public ISearchService getSearchService() {
        return searchService;
    }

    /**
     * odr d 0.3 hard coding locales support!
    */
    public List<Locale> getSupportedLocales() {
        List<Locale> ret = new ArrayList<Locale>();
        ret.add(Locale.ITALIAN);
        ret.add(Locale.ENGLISH);
        return ret;
    }

}