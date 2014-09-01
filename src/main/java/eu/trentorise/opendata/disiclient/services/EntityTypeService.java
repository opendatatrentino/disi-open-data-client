package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendata.semantics.services.model.IEtypeSearchResult;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.model.EtypeSearchResult;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.KbClient;
import it.unitn.disi.sweb.webapi.model.filters.AttributeDefinitionFilter;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import it.unitn.disi.sweb.webapi.model.kb.KnowledgeBase;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @author David Leoni <david.leoni@unitn.it>
 * @date 23 July 2014
 *
 */
public class EntityTypeService implements IEntityTypeService {

    public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH = 0.3;
    private static final Comparator SINGLE = new ValueComparator();

    public List<IEntityType> getAllEntityTypes() {
        KbClient kbClient = new KbClient(getClientProtocol());
        //TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
        List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
        long kbId = kbList.get(0).getId();
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeRestrictions(true);
        ctFilter.setIncludeAttributes(true);
        ctFilter.setIncludeAttributesAsProperties(true);

        List<ComplexType> complexTypeList = ctc.readComplexTypes(kbId, null, null, ctFilter);

        AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
        List<IEntityType> etypes = new ArrayList<IEntityType>();

        for (ComplexType cType : complexTypeList) {
            EntityType eType = new EntityType(cType);
            AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
            adf.setIncludeRestrictions(true);

            List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(cType.getId(), null, null, adf);

            List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();
            for (AttributeDefinition attrDef : attrDefList) {
                IAttributeDef attributeDef = new AttributeDef(attrDef);
                //System.out.println(attributeDef.toString());
                attributeDefList.add(attributeDef);
            }
            eType.setAttrs(attributeDefList);
            //	System.out.println(eType.toString());

            etypes.add(eType);
        }
        return etypes;
    }

    public EntityType getEntityTypeByConcept(Long conceptId) {
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeRestrictions(true);
        ctFilter.setIncludeAttributes(true);
        ctFilter.setIncludeAttributesAsProperties(true);
        List<ComplexType> complexTypes = ctc.readComplexTypes(1L, conceptId, null, ctFilter);
        ComplexType complexType = complexTypes.get(0);

        EntityType eType = new EntityType(complexType);
        AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
        AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
        adf.setIncludeRestrictions(true);
        List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(eType.getGUID(), null, null, adf);
        List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();

        for (AttributeDefinition attrDef : attrDefList) {

            IAttributeDef attributeDef = new AttributeDef(attrDef);
            attributeDefList.add(attributeDef);
        }
        eType.setAttrs(attributeDefList);
        return eType;
    }

    public EntityType getEntityType(long id) {
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        ComplexTypeFilter ctFilter = new ComplexTypeFilter();
        ctFilter.setIncludeRestrictions(true);
        ctFilter.setIncludeAttributes(true);
        ctFilter.setIncludeAttributesAsProperties(true);
        ComplexType complexType = ctc.readComplexType(id, ctFilter);

        EntityType eType = new EntityType(complexType);
        AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
        AttributeDefinitionFilter adf = new AttributeDefinitionFilter();
        adf.setIncludeRestrictions(true);
        List<AttributeDefinition> attrDefList = attrDefs.readAttributeDefinitions(id, null, null, adf);
        List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();

        for (AttributeDefinition attrDef : attrDefList) {

            IAttributeDef attributeDef = new AttributeDef(attrDef);
            attributeDefList.add(attributeDef);
        }
        eType.setAttrs(attributeDefList);
        return eType;
    }

    public void addAttributeDefToEtype(IEntityType entityType,
            IAttributeDef attrDef) {
        EntityType eType = (EntityType) entityType;

        eType.addAttributeDef(attrDef);

    }

    public void addUniqueIndexToEtype(IEntityType entityType,
            IUniqueIndex uniqueIndex) {
        throw new UnsupportedOperationException("todo to implement");
    }

    /**
     * The method returns client protocol
     *
     * @return returns an instance of ClientProtocol that contains information
     * where to connect(Url adress and port) and locale
     */
    private IProtocolClient getClientProtocol() {
        return WebServiceURLs.getClientProtocol();
    }

    public List<IEntityType> getEntityTypes(List<String> URLs) {
        List<IEntityType> etypes = new ArrayList<IEntityType>();

        for (String url : URLs) {
            etypes.add(getEntityType(url));
        }
        return etypes;
    }

    public EntityType getEntityType(String URL) {
        String s;
        try {
            s = URL.substring(URL.indexOf("es/") + 3);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Long typeID;
        try {
            typeID = Long.parseLong(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return getEntityType(typeID);
    }

    public List<IEtypeSearchResult> searchEntityTypes(String partialName) {
        ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
        List<IEtypeSearchResult> etypesSortedSearch = new ArrayList<IEtypeSearchResult>();
        List<ComplexType> complexTypeList = ctc.readComplexTypes(1L, null, null, null);
        HashMap<ComplexType, Double> ctypeMap= new HashMap<ComplexType, Double>();

        for (ComplexType cType : complexTypeList) {

           // System.out.println(cType.getName().get("it"));
            double score = scoreName(partialName, cType.getName().get("en"));
            double scoreIT = scoreName(partialName, cType.getName().get("it"));
            if (score>=scoreIT){
            ctypeMap.put(cType, score);
            } else ctypeMap.put(cType, scoreIT);

        }

        List<ComplexType> ctypeSortedEN = getKeysSortedByValue(ctypeMap);

        for (ComplexType cType : ctypeSortedEN) {
            System.out.println(cType.getName().get("it"));

            IEtypeSearchResult etype = new EtypeSearchResult(cType);
            etypesSortedSearch.add(etype);
        }

        return etypesSortedSearch;
    }

    private double scoreName(String searchName, String candidateName) {
        if (searchName.equals(candidateName)) {
            return 1.0;
        }

        int editDistance = StringUtils.getLevenshteinDistance(
                searchName, candidateName);

        // Normalize for length:
        double score
                = (double) (candidateName.length() - editDistance) / (double) candidateName.length();

        // Artificially reduce the score if the first letters don't match
        if (searchName.charAt(0) != candidateName.charAt(0)) {
            score = Math.min(score, MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH);
        }

        return Math.max(0.0, Math.min(score, 1.0));
    }

    private static <K, V extends Comparable<? super V>> List<K> getKeysSortedByValue(Map<K, V> map) {
        final int size = map.size();
        final List reusedList = new ArrayList(size);
        final List<Map.Entry<K, V>> meView = reusedList;
        meView.addAll(map.entrySet());
        Collections.sort(meView, SINGLE);
        final List<K> keyView = reusedList;
        for (int i = 0; i < size; i++) {
            keyView.set(i, meView.get(i).getKey());
        }
        return keyView;
    }

    public List<IEntityType> readAllEntityTypes() {
        return getAllEntityTypes();
    }

    public IEntityType readEntityType(String URL) {
        return getEntityType(URL);
    }

    public IEntityType readRootStructure() {
        return getRootStructure();
    }

    public IEntityType readRootEtype() {
        return getRootEtype();
    }

    public List<IEntityType> readEntityTypes(List<String> URLs) {
        return getEntityTypes(URLs);
    }

    private static final class ValueComparator<V extends Comparable<? super V>>
            implements Comparator<Map.Entry<?, V>> {

        public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    public IEntityType getRootStructure() {
        List<IEntityType> etypes = getAllEntityTypes();
        for (IEntityType etype : etypes) {

            if (etype.getName().getString(Locale.ENGLISH).equals("Structure")) {
                return etype;
            }
        }
        return null;
    }

    public IEntityType getRootEtype() {
        List<IEntityType> etypes = getAllEntityTypes();
        for (IEntityType etype : etypes) {

            if (etype.getName().getString(Locale.ENGLISH).equals("Entity")) {
                return etype;
            }
        }
        return null;
    }
}
