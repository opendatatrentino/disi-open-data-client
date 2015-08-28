package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.DictFactory;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.filters.AttributeDefinitionFilter;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.Presence;

import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.DataTypes;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 */
public class AttributeDef implements IAttributeDef {

    private long categoryId;
    private long conceptId;
    private String dataType;
    private long id;
    private Map<String, String> description;
    public Map<String, String> name;
    private long typeId;
    private boolean presence;
    private boolean isSet;
    private Integer entityTypeID;
    private IConcept concept;

    public AttributeDef(AttributeDefinition attrDef) {

        this.isSet = attrDef.isSet();
        this.categoryId = attrDef.getCategoryId();
        this.conceptId = attrDef.getConceptId();
        this.dataType = attrDef.getDataType().name();
        this.id = attrDef.getId();
        this.description = attrDef.getDescription();
        this.name = attrDef.getName();
        this.typeId = attrDef.getTypeId();
        if (attrDef.getRestrictionOnList() != null) {
            this.entityTypeID = (Integer) attrDef.getRestrictionOnList().getDefaultValue();
        }
        if ((attrDef.getPresence().equals("STRICTLY_MANDATORY")) || (attrDef.getPresence().equals("MANDATORY"))) {
            this.presence = true;
        } else {
            this.presence = false;
        }
    }

    public AttributeDef(long id) {
        AttributeDefinitionClient attrDefClient = new AttributeDefinitionClient(getClientProtocol());
        AttributeDefinitionFilter attrDefFilter = new AttributeDefinitionFilter();
        attrDefFilter.setIncludeRestrictions(true);
        AttributeDefinition attrDef = attrDefClient.readAttributeDefinition(id, attrDefFilter);
        this.isSet = attrDef.isSet();
        this.categoryId = attrDef.getCategoryId();
        this.conceptId = attrDef.getConceptId();
        this.dataType = attrDef.getDataType().name();
        this.id = attrDef.getId();
        this.description = attrDef.getDescription();
        this.name = attrDef.getName();
        this.typeId = attrDef.getTypeId();
        if (attrDef.getRestrictionOnList() != null) {
            this.entityTypeID = (Integer) attrDef.getRestrictionOnList().getDefaultValue();
        }
        if ((attrDef.getPresence().equals("STRICTLY_MANDATORY")) || (attrDef.getPresence().equals("MANDATORY"))) {
            this.presence = true;
        } else {
            this.presence = false;
        }
    }

    @Override
    public String toString() {
        return "AttributeDef [categoryId=" + categoryId + ", conceptId="
                + conceptId + ", dataType=" + dataType + ", id=" + id
                + ", description=" + description + ", name=" + name
                + ", typeId=" + typeId + ", presence=" + presence + "]";
    }

    private IProtocolClient getClientProtocol() {
        return WebServiceURLs.getClientProtocol();
    }

    public String getName(Locale locale) {
        return this.name.get(locale.toString());
    }

    public String getDataType() {
        if (this.dataType.equals("COMPLEX_TYPE")) {
            ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
            ComplexType cType = ctc.readComplexType(this.entityTypeID, null);
            if (cType.getClass().getName().equalsIgnoreCase("it.unitn.disi.sweb.webapi.model.kb.types.EntityType")) {
                return DataTypes.ENTITY;
            } else {
                return DataTypes.STRUCTURE;
            }
        }
        if (this.dataType.equals("STRUCTURE")) {
            return DataTypes.STRUCTURE;
        }
        if (this.dataType.equals("STRING")) {
            return DataTypes.STRING;
        }
        if (this.dataType.equals("BOOLEAN")) {
            return DataTypes.BOOLEAN;
        }
        if (this.dataType.equals("DATE")) {
            return DataTypes.DATE;
        }
        if (this.dataType.equals("INTEGER")) {
            return DataTypes.INTEGER;
        }
        if (this.dataType.equals("FLOAT")) {
            return DataTypes.FLOAT;
        }
        if (this.dataType.equals("LONG")) {
            return DataTypes.LONG;
        }
        if (this.dataType.equals("CONCEPT")) {
            return DataTypes.CONCEPT;
        }
        if (this.dataType.equals("SSTRING")) {
            return DataTypes.SEMANTIC_TEXT;
        }
        if (this.dataType.equals("NLSTRING")) {
            return DataTypes.STRING;
        }
        if (this.dataType.equals("ENTITY")) {
            return DataTypes.ENTITY;
        } else {
            return this.dataType;
        }
    }

    public EntityType getRangeEType() {
        if (this.dataType.equals("COMPLEX_TYPE")) {

            ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
            if (this.entityTypeID != null) {
                ComplexType cType = ctc.readComplexType(this.entityTypeID, null);
                EntityType etype = new EntityType(cType);
                return etype;
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    public IConcept getConcept() {
        long id = this.conceptId;
        ConceptODR concept = new ConceptODR();
        this.concept = concept.readConcept(id);
        return this.concept;
    }

    public boolean isSet() {
        return this.isSet;
    }

    public boolean isMandatory() {
        return this.presence;
    }

    public String getRegularExpression() {
        // TODO Postponed due to the absence copyOf the functionality on the API Client            
        throw new UnsupportedOperationException("todo to implement");
    }

    public void setRegularExpression(String regularExpression) {
        // TODO Postponed due to the absecopyOfe of the functionality on the API Client
        throw new UnsupportedOperationException("todo to implement");

    }

    public Long getGUID() {
        return this.id;
    }

    public String getURL() {
        return WebServiceURLs.attrDefIDToURL(this.id);
    }

    public AttributeDefinition convertAttributeDefinition() {
        AttributeDefinition atr = new AttributeDefinition();
        atr.setCategoryId(this.categoryId);
        atr.setConceptId(conceptId);
        ODRDataType dataT = new ODRDataType();
        atr.setDataType(dataT.convertDataType(this.dataType));
        atr.setDescription(this.description);
        atr.setId(id);
        atr.setName(this.name);
        if (presence = true) {
            atr.setPresence(Presence.MANDATORY);
        }
        atr.setSet(this.isSet);
        atr.setTypeId(this.typeId);
        return atr;
    }

    public long getId() {
        return this.id;
    }

    public long getConceptId() {
        return this.conceptId;
    }

    public AttributeDefinition addAttributeDefinition() {
        AttributeDefinitionClient attrDefClient = new AttributeDefinitionClient(getClientProtocol());
        AttributeDefinition attrDef = attrDefClient.readAttributeDefinition(id, null);
        return attrDef;
    }

    public Long getEType() {
        return this.typeId;
    }

    public long getRangeEntityTypeID() {
        return this.entityTypeID.longValue();
    }

    public String getEtypeURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/types/" + this.typeId;
        return url;
    }

    public String getRangeEtypeURL() {
        if (this.entityTypeID == null) {
            return null;
        } else {
            String fullUrl = WebServiceURLs.getURL();
            String url = fullUrl + "/types/" + this.entityTypeID;
            return url;
        }
    }

    public Dict getName() {
        return DictFactory.mapToDict(this.name);
    }

    public Dict getDescription() {
        return DictFactory.mapToDict(this.description);
    }

    public String getConceptURL() {
        String fullUrl = WebServiceURLs.getURL();
        String url = fullUrl + "/concepts/" + this.conceptId;
        return url;
    }

    public boolean isList() {
        return this.isSet;
    }

}
