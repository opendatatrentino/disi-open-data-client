package eu.trentorise.opendata.disiclient.model.entity;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.disiclient.DictFactory;
import eu.trentorise.opendata.disiclient.DisiClients;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 *
 */
public class EntityType implements IEntityType {

    private long conceptId;
    private List<IAttributeDef> attrs;
    private long id;
    private Map<String, String> description;
    private Map<String, String> name;

    public EntityType(ComplexType cType) {

        this.conceptId = cType.getConceptId();
        this.id = cType.getId();
        this.description = cType.getDescription();
        this.name = cType.getName();
    }

    public EntityType() {
    }

    @Override
    public String toString() {
        return "EntityType [conceptId=" + conceptId + ", attrs=" + attrs
                + ", id=" + id + ", description=" + description + ", name="
                + name + "]";
    }

    public String getName(Locale locale) {
        return name.get(OdtUtils.localeToLanguageTag(locale));
    }

    public Map<String, String> getName1() {

        return name;
    }

    @Override
    public IConcept getConcept() {        
        return DisiClients.getSingleton().getKnowledgeService().readConceptById(conceptId);        
    }

    
    @Override
    public void setConcept(IConcept concept) {

        ConceptODR conc = (ConceptODR) concept;
        ComplexTypeClient ctypeCl = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
        ComplexType ctype = ctypeCl.readComplexType(this.conceptId, null);
        
        // set concept on server-side 
        ctype.setConceptId(conc.getId());
        //set concept on client-side
        this.conceptId = conc.getId();
    }

    public void setAttrs(List<IAttributeDef> attrs) {
        this.attrs = attrs;
        //		List<AttributeDefinition> attrList  = new ArrayList<AttributeDefinition>();
        //		for(IAttributeDef attrDef: attrs){
        //			AttributeDef attr =(AttributeDef) attrDef;
        //			attrList.add(attr.convertAttributeDefinition());
        //		}
        //		ComplexTypeClient ctypeCl = new ComplexTypeClient( getClientProtocol());
        //		ComplexType ctype  =ctypeCl.readComplexType(this.id, null);
        //		ctype.setAttributes(attrList);
    }

    @Override
    public List<IAttributeDef> getAttributeDefs() {
        if (this.attrs != null) {
            return this.attrs;
        } else {            
            EntityType etype = DisiClients.getSingleton().getEntityTypeService().readEntityType(this.id);
            this.attrs = etype.getAttributeDefs();
            return this.attrs;
        }
    }

    public void addAttributeD(AttributeDef attrDef) {
        ArrayList<IAttributeDef> attrDefList = (ArrayList<IAttributeDef>) this.attrs;
        //adding attribute on client side
        attrDefList.add(attrDef);
        this.attrs = attrDefList;
        //adding attribute on server side
        AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient(SwebConfiguration.getClientProtocol());
        List<AttributeDefinition> attrList = attrDefCl.readAttributeDefinitions(this.id, null, null, null);
        ArrayList<AttributeDefinition> atrList = new ArrayList(attrList);
        atrList.add(attrDef.convertAttributeDefinition());
        ComplexTypeClient ctypeCl = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
        ComplexType ctype = ctypeCl.readComplexType(this.id, null);
        ctype.setAttributes(attrList);
    }

    public void removeAttributeDef(long attrDefID) {
        List<IAttributeDef> attrDefList = this.attrs;
        for (int i = 0; i < attrDefList.size(); i++) {
            AttributeDef attrDef = (AttributeDef) attrDefList.get(i);
            if (attrDefID == attrDef.getId()) {
                attrDefList.remove(i);
                break;
            }
        }
        //adding attribute on client side
        this.attrs = attrDefList;
        //adding attribute on server side
        AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient(SwebConfiguration.getClientProtocol());
        List<AttributeDefinition> attrList = attrDefCl.readAttributeDefinitions(this.id, null, null, null);
        for (int i = 0; i < attrList.size(); i++) {
            if (attrDefID == attrList.get(i).getId()) {
                attrDefList.remove(i);
                break;
            }
        }
        ComplexTypeClient ctypeCl = new ComplexTypeClient(SwebConfiguration.getClientProtocol());
        ComplexType ctype = ctypeCl.readComplexType(this.conceptId, null);
        ctype.setAttributes(attrList);
    }

    @Override
    public List<IUniqueIndex> getUniqueIndexes() {
        throw new UnsupportedOperationException("The metohf is not supported");
    }

    public void removeUniqueIndex(long uniqueIndexID) {
        throw new UnsupportedOperationException("The metohf is not supported");

    }

    public void addUniqueIndex(IUniqueIndex uniqueIndex) {
        throw new UnsupportedOperationException("The metohf is not supported");

    }

    public Long getGUID() {
        return this.id;
    }

    public Long getConceptID() {
        return this.conceptId;
    }

    @Override
    public String getURL() {
        return SwebConfiguration.getUrlMapper().etypeIdToUrl(this.id);
    }


    @Override
    public Dict getName() {
        return DictFactory.mapToDict(this.name);
    }

    public Dict getDescription() {
        return DictFactory.mapToDict(this.description);
    }



    @Override
    public IAttributeDef getNameAttrDef() {
        List<IAttributeDef> attrDefs = getAttributeDefs();
        for (IAttributeDef attr : attrDefs) {
            AttributeDef ad = (AttributeDef) attr;
            if (ad.getName(Locale.ENGLISH).equalsIgnoreCase("name")) {
                //	System.out.println(ad.getName(Locale.ENGLISH));
                return attr;
            }
        }
        return null;
    }

    @Override
    public IAttributeDef getDescriptionAttrDef() {
        List<IAttributeDef> attrDefs = getAttributeDefs();
        for (IAttributeDef attr : attrDefs) {
            AttributeDef ad = (AttributeDef) attr;
            if (ad.getName(Locale.ENGLISH).equals("Description")) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public String getConceptURL() {        
        return SwebConfiguration.getUrlMapper().conceptIdToUrl(this.conceptId);        
    }

    @Override
    public IAttributeDef getAttrDef(String URL) {
        List<IAttributeDef> attrDefs = getAttributeDefs();
        for (IAttributeDef attrDef : attrDefs) {
            if (attrDef.getURL().equals(URL)) {
                return attrDef;
            }
        }

        return null;
    }

}
