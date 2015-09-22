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

import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for the client.
 * 
 * @author David Leoni
 */
public final class DisiClients {

    private static final Logger LOG = LoggerFactory.getLogger(DisiClients.class);
    private static DisiEkb INSTANCE;

      
    
    
    
    /**
     * Horror method to get singleton
     * 
     * @deprecated todo try to use me as little as possible, pleease
     */
    public static DisiEkb getSingleton(){        
        return INSTANCE;
    }

    /**
     * Horror method to set singleton
     * 
     * @deprecated todo try to use me as little as possible, pleease
     */    
    public static void setSingleton(DisiEkb disiEkb){
        checkNotNull(disiEkb);
        INSTANCE = disiEkb;
        LOG.info("Set disiclient singleton");
    }
}
