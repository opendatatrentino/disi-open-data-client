package eu.trentorise.opendata.disiclient;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableBiMap;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.LocalizedString;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.services.DisiEkb;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.disiclient.services.EtypeService;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.AttrType;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Struct;
import eu.trentorise.opendata.semantics.model.entity.Val;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.traceprov.types.Concept;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;


import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Structure;

import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;

import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;
import it.unitn.disi.sweb.webapi.model.kb.types.EntityType;
import it.unitn.disi.sweb.webapi.model.kb.types.Presence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of static methods to convert to/from sweb
 *
 * @author David Leoni
 */
public final class Converter {

    private final static Logger LOG = LoggerFactory.getLogger(Converter.class);

    private DisiEkb ekb;

    private UrlMapper um;

    /**
     * Maps open entity data types to sweb ones, for the cases where there is a
     * one to one correspondence.
     */
    private static final ImmutableBiMap<String, DataType> ONE_TO_ONE_DATATYPES = ImmutableBiMap.<String, DataType>builder()
            .put(DataTypes.BOOLEAN, DataType.BOOLEAN)
            .put(DataTypes.CONCEPT, DataType.CONCEPT)
            .put(DataTypes.DATE, DataType.DATE)
            .put(DataTypes.FLOAT, DataType.FLOAT)
            .put(DataTypes.INTEGER, DataType.INTEGER)
            .put(DataTypes.LONG, DataType.LONG)
            .put(DataTypes.SEMANTIC_TEXT, DataType.SSTRING)
            .put(DataTypes.STRING, DataType.STRING)
            .build();

    private Attribute oeAttrToSwebAttribute(Attr oeAttr, boolean toCreate) {

        EtypeService ets = DisiClients.getSingleton().getEtypeService();

        Attribute ret = new Attribute();
        AttrDef oeAttrDef = ets.readAttrDef(oeAttr.getAttrDefId());
        ret.setDefinitionId(um.attrDefUrlToId(oeAttrDef.getId()));

        if (!toCreate) {
            ret.setId(oeAttr.getLocalID());
        }

        if (!oeAttr.getValues().isEmpty()) {

            List<Value> swebValues = new ArrayList();

            if (DataTypes.STRUCTURE.equals(oeAttrDef.getType().getDatatype())
                    || DataTypes.ENTITY.equals(oeAttrDef.getType().getDatatype())) {

                for (Val oeValue : oeAttr.getValues()) {
                    Value swebValue = new Value();
                    if (!toCreate) {
                        swebValue.setId(oeValue.getLocalID());
                    }
                    swebValue.setDataType(DataType.COMPLEX_TYPE);
                    if (DataTypes.STRUCTURE.equals(oeAttrDef.getType().getDatatype())) {
                        swebValue.setValue(oeStructureToSwebStructure((Struct) oeValue.getObj(), toCreate));
                    } else {
                        swebValue.setValue(oeEntityToSwebEntity((Entity) oeValue.getObj(), false, false));
                    }
                    swebValues.add(swebValue);
                }

            } else if (DataTypes.CONCEPT.equals(oeAttrDef.getType().getDatatype())) {

                for (Val oeValue : oeAttr.getValues()) {
                    Value swebValue = new Value();
                    if (!toCreate) {
                        swebValue.setId(oeValue.getLocalID());
                    }
                    swebValue.setDataType(DataType.CONCEPT);
                    it.unitn.disi.sweb.webapi.model.kb.concepts.Concept swebConcept = new it.unitn.disi.sweb.webapi.model.kb.concepts.Concept();
                    Concept oeConcept = (Concept) oeValue.getObj();
                    swebConcept.setId(um.conceptUrlToId(oeConcept.getId()));
                    swebValue.setValue(swebConcept);
                    swebValues.add(swebValue);
                }
            } else if (DataTypes.LOCALIZED_STRING.equals(oeAttrDef.getType().getDatatype())) {

                for (Val oeValue : oeAttr.getValues()) {
                    Value swebValue = new Value();
                    if (!toCreate) {
                        swebValue.setId(oeValue.getLocalID());
                    }
                    swebValue.setDataType(DataType.NLSTRING);
                    LocalizedString ls = (LocalizedString) oeValue.getObj();
                    swebValue.setValue(ls.str(), ls.loc().toLanguageTag());
                    swebValues.add(swebValue);
                }

            } else if (DataTypes.SEMANTIC_TEXT.equals(oeAttrDef.getType().getDatatype())) {
                for (Val oeValue : oeAttr.getValues()) {
                    Value swebValue = new Value();
                    if (!toCreate) {
                        swebValue.setId(oeValue.getLocalID());
                    }
                    swebValue.setDataType(DataType.SSTRING);
                    SemText semText = (SemText) oeValue.getObj();
                    SemanticString semanticString = DisiClients.getSingleton().getNLPService().getSemanticStringConverter().semanticString(semText);
                    swebValue.setValue(semText.getText(), semText.getLocale().toLanguageTag());
                    swebValue.setSemanticValue(semanticString);
                    swebValues.add(swebValue);
                }
            } else {
                for (Val oeValue : oeAttr.getValues()) {
                    Value swebValue = new Value();
                    if (!toCreate) {
                        swebValue.setId(oeValue.getLocalID());
                    }
                    swebValue.setDataType(oeDatatypeToSweb(oeAttrDef.getType().getDatatype()));
                    swebValue.setValue(oeValue.getObj());
                    swebValues.add(swebValue);
                }
            }
            ret.setValues(swebValues);

        } else {
            LOG.warn("Found openentity attribute with no values! Attribute id is " + oeAttr.getLocalID());
        }

        return ret;
    }

    private Converter(DisiEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
        this.um = SwebConfiguration.getUrlMapper();
    }

    public static Map<String, String> dictToMap(Dict description) {
        Map<String, String> ret = new HashMap();
        for (Locale loc : description.locales()) {
            List<String> strings = description.get(loc);
            if (strings.size() >= 1) {
                ret.put(loc.toLanguageTag(), strings.get(0));
                if (strings.size() > 1) {
                    LOG.warn("Found more than one string for locale " + loc + " while converting Dict to Map<String,String>, taking only first string");
                }
            }
        }
        return ret;
    }

    public static Dict mapToDict(Map<String, String> map) {
        Dict.Builder dictBuilder = Dict.builder();
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());
            dictBuilder = dictBuilder.put(l, (String) pairs.getValue());
        }
        return dictBuilder.build();
    }

    public static Dict multimapToDict(Map<String, List<String>> map) {
        Dict.Builder dictBuilder = Dict.builder();
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());

            List<String> vals = (List<String>) pairs.getValue();

            dictBuilder.put(l, vals);

        }
        return dictBuilder.build();
    }

    public static Dict swebNamesToDict(List<Name> names) {
        Dict.Builder dictBuilder = Dict.builder();
        for (Name name : names) {
            dictBuilder.put(Converter.multimapToDict(name.getNames()));
        }
        return dictBuilder.build();
    }

    public static Dict semtextsToDict(Map<String, List<SemText>> semtextsMap) {

        if (semtextsMap.isEmpty()) {
            return Dict.of();
        }

        Dict.Builder dict = Dict.builder();

        Iterator<?> it = semtextsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());
            List<SemText> vals = (List<SemText>) pairs.getValue();
            List<String> strings = new ArrayList();
            for (SemText stexts : vals) {
                strings.add(stexts.getText());
            }
            dict = dict.put(l, strings);
        }
        return dict.build();
    }

    public static DataType oeDatatypeToSweb(String openEntityDataType) {

        if (ONE_TO_ONE_DATATYPES.containsKey(openEntityDataType)) {
            return ONE_TO_ONE_DATATYPES.get(openEntityDataType);
        } else {
            switch (openEntityDataType) {
                case DataTypes.LOCALIZED_STRING:
                    return DataType.NLSTRING;
                case DataTypes.STRUCTURE:
                    return DataType.COMPLEX_TYPE;
                case DataTypes.ENTITY:
                    return DataType.COMPLEX_TYPE;
                default:
                    throw new UnsupportedOperationException("Unsupported open entity datatype: '" + openEntityDataType + "'");
            }
        }

    }

    /**
     * @param swebComplexType the id of the desired attriubte definition 
     * @param swebComplexType the complex type containing the desired attriubte definition 
     * @throws OpenEntityNotFoundException
     */
    public static AttributeDefinition swebAttributeDefinition(ComplexType swebComplexType, long attrDefId) {
        for (AttributeDefinition attrDef : swebComplexType.getAttributes()) {
            if (attrDefId == attrDef.getId()) {
                return attrDef;
            }
        }
        throw new OpenEntityNotFoundException("Couldn't find attribute definition with id " + attrDefId + " in sweb EntityType with id " + swebComplexType.getId());
    }

    /**
     * @param swebComplexType the etype containing the attribute definition of the {@code swebAttribute}
     */
    public Attr swebAttributeToOeAttr(Attribute swebAttribute, ComplexType swebComplexType) {

        Attr.Builder b = Attr.builder();

        b.setLocalID(swebAttribute.getId());
        
        AttributeDefinition swebAttrDef = swebAttributeDefinition(swebComplexType, swebAttribute.getDefinitionId());
        
        b.setAttrDefId(um.attrDefToUrl(swebAttrDef));

        if (swebAttribute.getConceptId() == null) {
            throw new IllegalArgumentException("Found attribute with null concept id while converting from sweb attribute with id" + swebAttribute.getId() + "to OpenEntity attribute");
        }

        if (DataType.CONCEPT.equals(swebAttribute.getDataType())) {

            for (Value swebVal : swebAttribute.getValues()) {
                it.unitn.disi.sweb.webapi.model.kb.concepts.Concept swebConcept = (it.unitn.disi.sweb.webapi.model.kb.concepts.Concept) swebVal.getValue();
                Concept oeConcept = ekb.getConverter().swebConceptToOeConcept(swebConcept);

                b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(oeConcept).build());
            }
        } else if (DataType.NLSTRING.equals(swebAttribute.getDataType())) {

            for (Value swebVal : swebAttribute.getValues()) {
                String swebString = (String) swebVal.getValue();
                LocalizedString locString = LocalizedString.of(Locale.forLanguageTag(swebVal.getLanguageCode()), swebString);
                b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(locString).build());
            }
        } else if (DataType.SSTRING.equals(swebAttribute.getDataType())) {

            for (Value swebVal : swebAttribute.getValues()) {
                SemText semText;
                String swebString = (String) swebVal.getValue();
                SemanticString swebSemanticString = (SemanticString) swebVal.getSemanticValue();
                if (swebSemanticString == null) {
                    LOG.warn("COULDN'T FIND SEMANTIC VALUE IN SWEB SSTRING VALUE WITH ID " + swebVal.getId() + ", CONVERTING ONLY  TEXT!");
                    semText = SemText.of(Locale.forLanguageTag(swebVal.getLanguageCode()), swebString);
                } else {
                    semText = DisiClients.getSingleton().getNLPService().getSemanticStringConverter().semText(swebSemanticString, true);
                }

                b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(semText).build());
            }
        } else if (DataType.COMPLEX_TYPE.equals(swebAttribute.getDataType())) {
            
            // todo shouldn't perform reads here....
            Long rangeEntityTypeId = (Long) swebAttrDef.getRestrictionOnList().getDefaultValue();
            ComplexType rangeSwebComplexType = ekb.getEtypeService().readSwebComplexType(rangeEntityTypeId);
            
            for (Value swebVal : swebAttribute.getValues()) {
                if (swebIsComplexTypeOeStructure(rangeSwebComplexType)) {
                    it.unitn.disi.sweb.webapi.model.eb.Structure swebStructure = (it.unitn.disi.sweb.webapi.model.eb.Structure) swebVal.getValue();
                    Struct oeStructure = swebStructureToOeStruct(swebStructure, rangeSwebComplexType);
                    b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(oeStructure).build());
                } else {
                    it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity = (it.unitn.disi.sweb.webapi.model.eb.Entity) swebVal.getValue();
                    Entity oeEntity = swebEntityToOeEntity(swebEntity, (EntityType) rangeSwebComplexType);
                    b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(oeEntity).build());
                }
            }

        } else {
            for (Value swebVal : swebAttribute.getValues()) {
                b.addValues(Val.builder().setLocalID(swebVal.getId()).setObj(swebVal.getValue()).build());
            }
        }
        return b.build();
    }

    public Struct swebStructureToOeStruct(it.unitn.disi.sweb.webapi.model.eb.Instance swebInstance, ComplexType entityType) {
        Struct.Builder b = Struct.builder();

        for (Attribute a : swebInstance.getAttributes()) {
            String attrDefUrl = um.attrDefToUrl(swebAttributeDefinition(entityType, a.getDefinitionId()));
            b.putAttrs(attrDefUrl, swebAttributeToOeAttr(a, entityType));
        }

        b.setEtypeId(um.etypeIdToUrl(swebInstance.getTypeId()));
        b.setId(um.entityIdToUrl(swebInstance.getId()));
        return b.build();
    }

    public Entity swebEntityToOeEntity(it.unitn.disi.sweb.webapi.model.eb.Entity entity, EntityType swebEntityType) {
        Entity.Builder b = Entity.builder();

        for (Attribute a : entity.getAttributes()) {
            String attrDefUrl = um.attrDefToUrl(swebAttributeDefinition(swebEntityType, a.getDefinitionId()));
            b.putAttrs(attrDefUrl, swebAttributeToOeAttr(a, swebEntityType));
        }

        b.setEtypeId(um.etypeIdToUrl(entity.getTypeId()));
        b.setId(um.entityIdToUrl(entity.getId()));

        b.setName(Converter.swebNamesToDict(entity.getNames()));
        b.setDescription(Converter.swebSemanticStringsToDict(entity.getDescriptions()));
        return b.build();
    }

    public boolean swebIsComplexTypeOeStructure(ComplexType cType) {
        return !cType.getClass().equals(it.unitn.disi.sweb.webapi.model.kb.types.EntityType.class);
    }

    private Etype.Builder swebComplexTypeToOeEtypeBuilder(ComplexType cType) {
        Etype.Builder b = Etype.builder();
        b.setConceptId(um.conceptIdToUrl(cType.getConceptId()));
        b.setId(um.etypeIdToUrl(cType.getId()));
        b.setDescription(Converter.mapToDict(cType.getDescription()));
        b.setName(Converter.mapToDict(cType.getName()));
        b.setStruct(swebIsComplexTypeOeStructure(cType));
        List<AttributeDefinition> swebAttrDefs = cType.getAttributes();

        for (AttributeDefinition ad : swebAttrDefs) {
            b.putAttrDefs(um.attrDefIdToUrl(ad.getId(), ad.getConceptId()), swebAttributeDefToOeAttrDef(ad));
        }

        return b;
    }

    public Etype swebComplexTypeToOeEtype(ComplexType complexType) {
        if (ekb.getConverter().swebIsComplexTypeOeStructure(complexType)) {
            return swebComplexTypeToOeEtypeBuilder(complexType).build();
        } else {
            return ekb.getConverter().swebComplexTypeToOeEtype((EntityType) complexType);
        }
    }

    public Etype swebEntityTypeToOeEtype(EntityType swebEntityType) {
        Etype.Builder b = swebComplexTypeToOeEtypeBuilder((ComplexType) swebEntityType);
        b.setNameAttrDefId(um.attrDefToUrl(swebEntityType.getNameDefinition()));
        b.setDescrAttrDefId(um.attrDefToUrl(swebEntityType.getDescriptionDefinition()));
        return b.build();
    }

    public SearchResult makeSearchResult(it.unitn.disi.sweb.webapi.model.kb.concepts.Concept codr) {

        Dict name;

        if (codr.getName() == null) {
            LOG.warn("Found null name in concept with id ", codr.getId(), " making search result with empty name");
            name = Dict.of();
        } else {
            name = mapToDict(codr.getName());
        }
                
        String url = SwebConfiguration.getUrlMapper().conceptIdToUrl(codr.getId());

        return SearchResult.of(url, name);
    }
    
    
    public SearchResult makeSearchResult(ComplexType cType) {
        Dict name = Converter.mapToDict(cType.getName());       
        
        String url = SwebConfiguration.getUrlMapper().etypeIdToUrl(cType.getId());    
        
        return SearchResult.of(url, name);
    }
        
    public SearchResult makeSearchResult(it.unitn.disi.sweb.webapi.model.eb.Entity swebInstance) {                       
        Map<String, List<String>> names = swebInstance.getNames().iterator().next().getNames();
        Dict name = Converter.multimapToDict(names);
        String url = SwebConfiguration.getUrlMapper().entityIdToUrl(swebInstance.getId());
        
        return SearchResult.of(url, name);
    }  
    
    public AttrDef swebAttributeDefToOeAttrDef(AttributeDefinition attrDef) {
        AttrDef.Builder b = AttrDef.builder();
        AttrType.Builder atb = AttrType.builder();

        b.setConceptId(um.conceptIdToUrl(attrDef.getConceptId()));
        b.setId(um.attrDefIdToUrl(attrDef.getId(), attrDef.getConceptId()));
        b.setDescription(Converter.mapToDict(attrDef.getDescription()));
        b.setName(Converter.mapToDict(attrDef.getName()));
        atb.setMandatory((attrDef.getPresence().equals(Presence.STRICTLY_MANDATORY)) || (attrDef.getPresence().equals(Presence.MANDATORY)));

        Etype rangeEtype;

        if (attrDef.getDataType().equals(DataType.COMPLEX_TYPE)) {
            Long rangeEtypeId = (Long) attrDef.getRestrictionOnList().getDefaultValue();
            rangeEtype = ekb.getEtypeService().readEtype(um.etypeIdToUrl(rangeEtypeId));

        } else {
            rangeEtype = null;
        }
        String rangeEtypeUrl = rangeEtype == null ? null : rangeEtype.getId();
        atb.setEtypeId(rangeEtypeUrl);
        atb.setDatatype(Converter.swebDatatypeToOpenEntity(attrDef.getDataType(), rangeEtypeUrl));
        atb.setList(attrDef.isSet());
        atb.setEtypeName(rangeEtype == null ? Dict.of() : rangeEtype.getName());
        b.setType(atb.build());
        return b.build();
    }

    /**
     * Used in entity descriptions
     */
    public static Dict swebSemanticStringsToDict(Map<String, List<SemanticString>> swebDescr) {
        Dict.Builder builder = Dict.builder();
        for (String loc : swebDescr.keySet()) {
            for (SemanticString ss : swebDescr.get(loc)) {
                builder.put(Locale.forLanguageTag(loc), ss.getText());
            }
        }
        return builder.build();
    }

    public static String swebDatatypeToOpenEntity(DataType swebDatatype, @Nullable String etypeUrl) {

        if (ONE_TO_ONE_DATATYPES.containsValue(swebDatatype)) {
            return ONE_TO_ONE_DATATYPES.inverse().get(swebDatatype);
        } else {
            switch (swebDatatype) {
                case COMPLEX_TYPE: {
                    checkNotNull(etypeUrl, "datatype is COMPLEX_TYPE, need entityTypeId to determine if it ENTITY or STRUCTURE, but found null");
                    Etype etype = DisiClients.getSingleton().getEtypeService().readEtype(etypeUrl);
                    if (etype.isStruct()) {
                        return DataTypes.STRUCTURE;
                    } else {
                        return DataTypes.ENTITY;
                    }
                }
                case NLSTRING:
                    
                    return DataTypes.LOCALIZED_STRING;
                default:
                    throw new UnsupportedOperationException("UNSUPPORTED SWEB DATATYPE " + swebDatatype + " !");
            }

        }
    }

    public static Object swebObjToOpenEntityObj(Object obj) {
        checkNotNull(obj, "Why on Earth sweb obj (that is, the Object inside Value) should be null??");
        throw new UnsupportedOperationException("TODO IMPLEMENT ME!");
        //return obj;
    }

    /**
     * Converts from Entity to sweb format. This was the previous disify
     * function
     *
     * @param root When true, all first level attributes are copied to output
     * entity. Eventual subentities in Val are copied as non-root. When false,
     * only URL and etype are copied to output entity.
     * @throws IllegalArgumentException if provided entity is not valid
     */
    public it.unitn.disi.sweb.webapi.model.eb.Entity oeEntityToSwebEntity(Entity entity, boolean root, boolean toCreate) {

        LOG.info("converting entity.getURL = " + entity.getId() + " to sweb format");

        if (!root && toCreate) {
            throw new IllegalArgumentException("Tried to produce a subentity marked to be created, which is unsupported!");
        }       

        it.unitn.disi.sweb.webapi.model.eb.Entity ret = new it.unitn.disi.sweb.webapi.model.eb.Entity();
        LOG.warn("SETTING HARD CODED ENTITY BASE ID = 1.");
        ret.setEntityBaseId(1L);

        if (!toCreate) {
            ret.setId(um.entityUrlToId(entity.getId()));
            // ret.setsUrl(entity.getUrl()); this surl stuff looks mysterious, MAYBE we can do without it.
        }

        List<Attribute> newSwebAttrs = new ArrayList();

        if (root) {
            Checker.of(DisiClients.getSingleton()).checkEntity(entity, true);
            for (Attr oeAttr : entity.getAttrs().values()) {
                newSwebAttrs.add(oeAttrToSwebAttribute(oeAttr, toCreate));
            }

            ret.setAttributes(newSwebAttrs);
            ret.setTypeId(um.etypeUrlToId(entity.getEtypeId()));
        }

        /*
         if (entity.getEtype() == null) {
         throw new IllegalArgumentException("Provided entity must have etype! Entity URL is " + entity.getURL());
         }
         */
        return ret;
    }

    /**
     * Converts from Struct to sweb format.
     *
     * @throws IllegalArgumentException if provided structure is not valid
     */
    public Structure oeStructureToSwebStructure(Struct oeStruct, boolean toCreate) {

        LOG.info("converting structure.getURL = " + oeStruct.getId() + " to sweb format");

        Structure ret = new Structure();
        LOG.warn("SETTING HARD CODED ENTITY BASE ID = 1.");
        ret.setEntityBaseId(1L);

        if (!toCreate) {
            ret.setId(um.entityUrlToId(oeStruct.getId()));
            // ret.setsUrl(entity.getUrl()); todo this surl stuff looks mysterious, MAYBE we can do without it.
        }

        List<Attribute> newSwebAttrs = new ArrayList();

        ekb.getChecker().checkStruct(oeStruct, true);
        for (Attr oeAttr : oeStruct.getAttrs().values()) {
            newSwebAttrs.add(oeAttrToSwebAttribute(oeAttr, toCreate));
        }

        ret.setAttributes(newSwebAttrs);
        ret.setTypeId(um.etypeUrlToId(oeStruct.getEtypeId()));

        return ret;
    }

    public static Converter of(DisiEkb ekb) {
        return new Converter(ekb);
    }

    public Concept swebConceptToOeConcept(it.unitn.disi.sweb.webapi.model.kb.concepts.Concept swebConcept) {
        Concept.Builder b = Concept.builder();
        b.setId(um.conceptIdToUrl(swebConcept.getId()));
        b.setName(mapToDict(swebConcept.getName()));
        b.setDescription(mapToDict(swebConcept.getDescription()));
        return b.build();
    }

}
