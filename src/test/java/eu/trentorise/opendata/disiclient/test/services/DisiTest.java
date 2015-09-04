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
package eu.trentorise.opendata.disiclient.test.services;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.disiclient.UrlMapper;
import eu.trentorise.opendata.disiclient.test.ConfigLoader;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.services.IEkb;

/**
 * Many fields are static, but hey, world can't be perfect.
 *
 * @author David Leoni
 */
public abstract class DisiTest {

    static final IEkb ekb = ConfigLoader.init();
    static final Checker checker = Checker.of(ekb);

    static final UrlMapper um = SwebConfiguration.getUrlMapper();

    public static final long CONCEPT_1_ID = 33292L;
    public static final long CONCEPT_1_GLOBAL_ID = 33769L;
    public static final String CONCEPT_1_URL = um.conceptIdToUrl(CONCEPT_1_ID);

    public static final long NAME_CONCEPT_ID = 2L;
    public static final long NAME_GLOBAL_CONCEPT_ID = 2L;
    public static final String NAME_CONCEPT_URL = um.conceptIdToUrl(NAME_CONCEPT_ID);

    public static final long HOURS_CONCEPT_ID = 72797L;
    public static final long HOURS_GLOBAL_CONCEPT_ID = 80505L;

    public static final String HOURS_CONCEPT_URL = um.conceptIdToUrl(HOURS_CONCEPT_ID);

    public static final long OVERACHIEVEMENT_CONCEPT_ID = 120L;
    public static final long OVERACHIEVEMENT_CONCEPT_GLOBAL_ID = 203L;
    public static final String CONCEPT_3_URL = um.conceptIdToUrl(OVERACHIEVEMENT_CONCEPT_ID);

    public static final long INFORMATION_TECHNOLOGY_CONCEPT_ID = 32593L;
    public static final long INFORMATION_TECHNOLOGY_GLOBAL_CONCEPT_ID = 33069L;
    public static final String INFORMATION_TECHNOLOGY_CONCEPT_URL = um.conceptIdToUrl(INFORMATION_TECHNOLOGY_CONCEPT_ID);

    public static final long OPENING_HOURS = 7L;
    public static final String OPENING_HOURS_URL = um.etypeIdToUrl(OPENING_HOURS);

    public static final long ATTR_DEF_FACILITY_OPENING_HOURS = 66L;
    public static final long ATTR_DEF_FACILITY_OPENING_HOURS_CONCEPT_ID = 111008L;
    public static final String ATTR_DEF_FACILITY_OPENING_HOURS_URL = um.attrDefIdToUrl(ATTR_DEF_FACILITY_OPENING_HOURS, ATTR_DEF_FACILITY_OPENING_HOURS_CONCEPT_ID);
    public static final long ATTR_DEF_HOURS_OPENING_HOUR = 31L;
    public static final long ATTR_DEF_HOURS_OPENING_HOUR_CONCEPT_ID = 111011L;
    public static final String ATTR_DEF_HOURS_OPENING_HOUR_URL = um.attrDefIdToUrl(ATTR_DEF_HOURS_OPENING_HOUR, ATTR_DEF_HOURS_OPENING_HOUR_CONCEPT_ID);
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR = 30L;
    public static final long ATTR_DEF_HOURS_CLOSING_HOUR_CONCEPT_ID = 73048L;
    public static final String ATTR_DEF_HOURS_CLOSING_HOUR_URL = um.attrDefIdToUrl(ATTR_DEF_HOURS_CLOSING_HOUR, ATTR_DEF_HOURS_CLOSING_HOUR_CONCEPT_ID);

    static final long PALAZZETTO_ID = 64000L;
    /**
     * Palazzetto is a Facility. It doesn't have description. Its concept is
     * gymnasium.
     */
    public static final String PALAZZETTO_URL = um.entityIdToUrl(PALAZZETTO_ID);
    public static final String PALAZZETTO_NAME_IT = "PALAZZETTO DELLO SPORT";
    public static final long GYMNASIUM_CONCEPT_ID = 18565L;
    public static final long GYMNASIUM_CONCEPT_GLOBAL_ID = 18937L;

    public static final String GYMNASIUM_CONCEPT_URL = um.conceptIdToUrl(GYMNASIUM_CONCEPT_ID);

    static final long RAVAZZONE_ID = 15001L;
    /**
     * Ravazzone is a cool district of Mori.
     */
    public static final String RAVAZZONE_URL = um.entityIdToUrl(RAVAZZONE_ID);
    public static final String RAVAZZONE_NAME_IT = "Ravazzone";
    public static final String RAVAZZONE_NAME_EN = "Ravazzone";

    public static final long RESIDENCE_DES_ALPES_ID = 66206L;
    public static final String RESIDENCE_DES_ALPES_URL = um.entityIdToUrl(RESIDENCE_DES_ALPES_ID);

    public static final long COMANO_ID = 15007L;
    public static final String COMANO_URL = um.entityIdToUrl(COMANO_ID);

    public static final long POVO_ID = 1024;
    public static final String POVO_URL = um.entityIdToUrl(POVO_ID);

    static final long CAMPANIL_PARTENZA_ID = 64235L;
    /**
     * "Campanil partenza" is a Facility. Entity concept is Detachable
     * chairlift. Has attributes orari. Has descriptions both in Italian and
     * English. Name is only in Italian.
     */
    public static final String CAMPANIL_PARTENZA_URL = um.entityIdToUrl(CAMPANIL_PARTENZA_ID);
    public static final long DETACHABLE_CHAIRLIFT_CONCEPT_ID = 111009L;
    public static final long DETACHABLE_CHAIRLIFT_GLOBAL_CONCEPT_ID = 120783L;
    public static final String DETACHABLE_CHAIRLIFT_CONCEPT_URL = um.conceptIdToUrl(DETACHABLE_CHAIRLIFT_CONCEPT_ID);
    public static final String CAMPANIL_PARTENZA_NAME_IT = "Campanil partenza";

    static final long ANDALO_ID = 2089L;
    /**
     * Andalo is one of those nasty locations with "Place Name" as Name type
     */
    public static final String ANDALO_URL = um.entityIdToUrl(ANDALO_ID);

    public static final long ROOT_ENTITY_ID = 21L;
    public static final String ROOT_ENTITY_URL = um.etypeIdToUrl(ROOT_ENTITY_ID);

    public static final Long LOCATION_ID = 18L;
    public static final String LOCATION_URL = um.etypeIdToUrl(LOCATION_ID);

    // Facility
    public static final long FACILITY_ID = 12L;
    public static final String FACILITY_URL = um.etypeIdToUrl(FACILITY_ID);

    public static final long ATTR_DEF_LATITUDE_ID = 69L;
    public static final long ATTR_DEF_LATITUDE_CONCEPT_ID = 45421L;
    public static final String ATTR_DEF_LATITUDE_CONCEPT_URL = um.conceptIdToUrl(ATTR_DEF_LATITUDE_CONCEPT_ID);
    public static final String ATTR_DEF_LATITUDE_URL = um.attrDefIdToUrl(ATTR_DEF_LATITUDE_ID, ATTR_DEF_LATITUDE_CONCEPT_ID);
    public static final long ATTR_DEF_LONGITUDE_ID = 68L;
    public static final long ATTR_DEF_LONGITUDE_CONCEPT_ID = 45427L;
    public static final String ATTR_DEF_LONGITUDE_CONCEPT_URL = um.conceptIdToUrl(ATTR_DEF_LONGITUDE_CONCEPT_ID);
    public static final String ATTR_DEF_LONGITUDE_URL = um.attrDefIdToUrl(ATTR_DEF_LONGITUDE_ID, ATTR_DEF_LONGITUDE_CONCEPT_ID);
    public static final long ATTR_DEF_CLASS = 58L;
    public static final long ATTR_DEF_CLASS_CONCEPT_ID = 42806L;
    public static final String ATTR_DEF_CLASS_URL = um.attrDefIdToUrl(ATTR_DEF_CLASS, ATTR_DEF_CLASS_CONCEPT_ID);
    public static final long ATTR_DEF_DESCRIPTION = 62L;
    public static final long ATTR_DEF_DESCRIPTION_CONCEPT_ID = 3L;
    public static final String ATTR_DEF_DESCRIPTION_URL = um.attrDefIdToUrl(ATTR_DEF_DESCRIPTION, ATTR_DEF_DESCRIPTION_CONCEPT_ID);

    public static final long ATTR_DEF_PART_OF = 60L;
    public static final long ATTR_DEF_PART_OF_CONCEPT_ID = 5L;
    /**
     * Part-of has {@link #ROOT_ENTITY_URL} as range
     */
    public static final String ATTR_DEF_PART_OF_URL = um.attrDefIdToUrl(ATTR_DEF_PART_OF, ATTR_DEF_PART_OF_CONCEPT_ID);

    public static final long NAME_ID = 10L;
    public static final String NAME_URL = um.etypeIdToUrl(NAME_ID);

    // Shopping facility
    public static final long SHOPPING_FACILITY_ID = 1L;
    public static final String SHOPPING_FACILITY_URL = um.etypeIdToUrl(SHOPPING_FACILITY_ID);

    // Certified product stuff 
    public static final long CERTIFIED_PRODUCT_ID = 17L;
    public static final String CERTIFIED_PRODUCT_URL = um.etypeIdToUrl(CERTIFIED_PRODUCT_ID);

    public static final long ATTR_TYPE_OF_CERTIFICATE = 110L;
    public static final long ATTR_TYPE_OF_CERTIFICATE_CONCEPT_ID = 111103L;
    public static final String ATTR_TYPE_OF_CERTIFICATE_URL = um.attrDefIdToUrl(ATTR_TYPE_OF_CERTIFICATE, ATTR_TYPE_OF_CERTIFICATE_CONCEPT_ID);

    /**
     * It is of type 'Certified product' NOTE: CREATED WITH ODR, WILL DISAPPEAR
     * FROM SERVER ONCE IT IS REGENERATED
     */
    public static final long MELA_VAL_DI_NON = 75167L;
    /**
     * NOTE: CREATED WITH ODR, WILL DISAPPEAR FROM SERVER ONCE IT IS REGENERATED
     */
    public static final String MELA_VAL_DI_NON_URL = um.etypeIdToUrl(MELA_VAL_DI_NON);

    /**
     * This contact is a structure
     */
    public static final long KINDERGARDEN_CONTACT_ID = 64008L;
    public static final long ATTR_DEF_TELEPHONE_ID = 177L;
    public static final long TELEPHONE_CONCEPT_ID = 23985L;
    public static final String ATTR_DEF_TELEPHONE_URL = um.attrDefIdToUrl(ATTR_DEF_TELEPHONE_ID, TELEPHONE_CONCEPT_ID);

}
