/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.disiclient;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.semantics.services.SearchResult;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for the client.
 * 
 * @author David Leoni
 */
public final class DisiClients {

    private static final Logger LOG = LoggerFactory.getLogger(DisiClients.class);
    private static final DisiEkb INSTANCE = new DisiEkb();;

    public static SearchResult makeSearchResult(ConceptODR codr) {

        Dict name;

        if (codr.getName() == null) {
            LOG.warn("Found null name in concept with id ", codr.getId(), " making search result with empty name");
            name = Dict.of();
        } else {
            name = codr.getName();
        }
                
        String url = SwebConfiguration.getUrlMapper().conceptIdToUrl(codr.getId());

        return SearchResult.of(url, name);
    }
    
    
    public static SearchResult makeSearchResult(ComplexType cType) {
        Dict name = DictFactory.mapToDict(cType.getName());       
        
        String url = SwebConfiguration.getUrlMapper().etypeIdToUrl(cType.getId());    
        
        return SearchResult.of(url, name);
    }
        
    public static SearchResult makeSearchResult(Entity instance) {                       
        Map<String, List<String>> names = instance.getNames().iterator().next().getNames();
        Dict name = DictFactory.multimapToDict(names);
        String url = SwebConfiguration.getUrlMapper().entityIdToUrl(instance.getId());
        
        return SearchResult.of(url, name);
    }    
    
    
    
    /**
     * Horror method to get singleton
     * 
     * @deprecated todo try to use me as little as possible, pleease
     */
    public static DisiEkb getClient(){        
        return INSTANCE;
    }
}
