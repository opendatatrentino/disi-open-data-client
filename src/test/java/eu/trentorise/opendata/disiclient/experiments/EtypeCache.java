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
package eu.trentorise.opendata.disiclient.experiments;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Experiment to see how to replace stale etypes
 * @deprecated DON USE IT UNTIL IT'S FINALIZED!
 */
public final class EtypeCache {

    private static final long DEFAULT_KB = 1;

    private static EtypeCache singleton;

    private HashMap<Long, Long> etypesStatusMap;

    private ImmutableList<Etype> etypes;

    private IEkb ekb;

    private EtypeCache() {
    }

    ;

    private EtypeCache(IEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
        etypes = ImmutableList.of();
    }

    /**
     * Returns the cache singleton;
     */
    public static EtypeCache of(IEkb ekb) {
        if (singleton == null) {
            singleton = new EtypeCache(ekb);
        }
        return singleton;
    }

    /**
     * Returns an immutable list of all schemas. Schemas can be fetched lazily
     * from the server if not present in cache or if stale
     */
    public synchronized List<Etype> readSchemas() {

        if (etypes.size() == 0) {
            createSchemas();
        }

        ArrayList<Long> outdatedSchemaIds = getOutdatedSchemas();
        if (!outdatedSchemaIds.isEmpty()) {
            updateSchemas(outdatedSchemaIds);
            return etypes;
        } else {
            return etypes;
        }
    }

    public void createSchemas() {

        List<Etype> etypeList = ekb.getEtypeService().readAllEtypes();
        etypes = new ImmutableList.Builder<Etype>()
                .addAll(etypeList)
                .build();
    }

    private void updateSchemas(ArrayList<Long> outdatedSchemaIds) {

        for (Long outdatedEtypeId : outdatedSchemaIds) {
            Etype updtadetEtype = ekb.getEtypeService().readEtype(
                    SwebConfiguration.getUrlMapper().etypeIdToUrl(outdatedEtypeId));
            //update immutable list;
        }
    }

    public ArrayList<Long> getOutdatedSchemas() {
        ArrayList<Long> outdatedTypes = new ArrayList();
        ComplexTypeClient ctc = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeTimestamps(true);
        List<ComplexType> complexTypeList = ctc.readComplexTypes(DEFAULT_KB, null, null, ctFilter);
        int i = 0;
        HashMap<Long, Long> freshEtypes = new HashMap();
        for (ComplexType ct : complexTypeList) {
            freshEtypes.put(ct.getConceptId(), ct.getModificationDate().getTime());
            //			System.out.println(++i+": Concept id: "+ct.getConceptId());
            //			System.out.println("Timestamp: "+ct.getModificationDate());
        }

        for (Map.Entry<Long, Long> entry : freshEtypes.entrySet()) {

            if (!etypesStatusMap.containsKey(entry.getKey())) {
                outdatedTypes.add(entry.getKey());
            } else {
                long newTime = entry.getValue();
                if (etypesStatusMap.get(entry.getKey()) < newTime) {
                    outdatedTypes.add(entry.getKey());
                }
            }
        }
        etypesStatusMap = freshEtypes;

        return outdatedTypes;
    }
}
