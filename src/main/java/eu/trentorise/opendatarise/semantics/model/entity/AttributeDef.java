package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.Presence;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.model.knowledge.Dict;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;

/** 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 25 Feb 2014
 * 
 */
public class AttributeDef implements IAttributeDef {

	private long categoryId;
	private long conceptId;
	private String dataType;
	private long id;
	private Map<String,String> description;
	public Map<String,String> name;
	private long typeId;
	private boolean presence;
	private boolean isSet;
	private Integer entityTypeID;

	public AttributeDef(AttributeDefinition attrDef){
		this.isSet = attrDef.isSet();
		this.categoryId = attrDef.getCategoryId();
		this.conceptId = attrDef.getConceptId();
		this.dataType = attrDef.getDataType().name();
		this.id = attrDef.getId();
		this.description = attrDef.getDescription();
		this.name = attrDef.getName();
		this.typeId=attrDef.getTypeId();
		if(attrDef.getRestrictionOnList()!=null){
			this.entityTypeID=(Integer) attrDef.getRestrictionOnList().getDefaultValue();
		}
		if ((attrDef.getPresence().equals("STRICTLY_MANDATORY"))||(attrDef.getPresence().equals("MANDATORY")))
		{this.presence=true;}
		else {this.presence=false;}		
	}

	public AttributeDef (long id){
		AttributeDefinitionClient attrDefClient = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinition attrDef = attrDefClient.readAttributeDefinition(id, null);
		this.isSet = attrDef.isSet();
		this.categoryId = attrDef.getCategoryId();
		this.conceptId = attrDef.getConceptId();
		this.dataType = attrDef.getDataType().name();
		this.id = attrDef.getId();
		this.description = attrDef.getDescription();
		this.name = attrDef.getName();
		this.typeId=attrDef.getTypeId();
		if(attrDef.getRestrictionOnList()!=null){
			this.entityTypeID=(Integer) attrDef.getRestrictionOnList().getDefaultValue();
		}
		if ((attrDef.getPresence().equals("STRICTLY_MANDATORY"))||(attrDef.getPresence().equals("MANDATORY")))
		{this.presence=true;}
		else {this.presence=false;}		
	}

	@Override
	public String toString() {
		return "AttributeDef [categoryId=" + categoryId + ", conceptId="
				+ conceptId + ", dataType=" + dataType + ", id=" + id
				+ ", description=" + description + ", name=" + name
				+ ", typeId=" + typeId + ", presence=" + presence + "]";
	}

	private IProtocolClient getClientProtocol(){
		return WebServiceURLs.getClientProtocol();
	}

	public String getName(Locale locale) {
		return this.name.get(locale.toLanguageTag());
	}

	public String getDataType() {
		if (this.dataType.equals("COMPLEX_TYPE")) return DataTypes.STRUCTURE;
		if (this.dataType.equals("STRUCTURE")) return DataTypes.STRUCTURE;
		if (this.dataType.equals("STRING")) return DataTypes.STRING;
		if (this.dataType.equals("BOOLEAN")) return DataTypes.BOOLEAN;
		if (this.dataType.equals("DATE")) return DataTypes.DATE;
		if (this.dataType.equals("INTEGER")) return DataTypes.INTEGER;
		if (this.dataType.equals("FLOAT")) return DataTypes.FLOAT;
		if (this.dataType.equals("LONG")) return DataTypes.LONG;
		if (this.dataType.equals("CONCEPT")) return DataTypes.CONCEPT;
		if (this.dataType.equals("SSTRING")) return DataTypes.SEMANTIC_TEXT;
		if (this.dataType.equals("NLSTRING")) return DataTypes.NLSTRING;
		if (this.dataType.equals("ENTITY")) return DataTypes.ENTITY;
		else return this.dataType;
	}

	public IEntityType getRangeEType() {
		if (this.dataType.equals("COMPLEX_TYPE")){
			ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
			if (this.entityTypeID!=null){
				ComplexType cType = ctc.readComplexType(this.entityTypeID, null);
				EntityType  etype = new EntityType(cType);
				return etype;
			} 	
			else return null;

			//			if (this.conceptId==5){
			//				ComplexType cType = ctc.readComplexType(21L, null);
			//				EntityType  etype = new EntityType(cType);
			//				return etype; 
			//			}  else
			//				if ((this.conceptId==73462)||(this.conceptId==73562)){
			//					ComplexType cType = ctc.readComplexType(16L, null);
			//					EntityType  etype = new EntityType(cType);
			//					return etype; 
			//				}  else
			//					if (this.conceptId==72844){
			//						ComplexType cType = ctc.readComplexType(3L, null);
			//						EntityType  etype = new EntityType(cType);
			//						return etype; 
			//					}  					
			//			List<ComplexType> cType = ctc.readComplexTypes(1L, this.conceptId, null, null);
			//			if(cType.size()>0){
			//				EntityType  etype = new EntityType(cType.get(0));
			//				return etype;} else 
			//					return null;
			//		} 
		} else return null;
	}

	public IConcept getConcept() {
		long id = this.conceptId;
		ConceptODR concept = new ConceptODR();
		concept = concept.readConcept(id);
		return concept;
	}

	public boolean isSet() {
		return this.isSet;
	}

	public boolean isMandatory() {
		return this.presence;
	}
	public String getRegularExpression() {
		// TODO Postponed due to the absence of the functionality on the API Client
		return null;
	}

	public void setRegularExpression(String regularExpression) {
		// TODO Postponed due to the absence of the functionality on the API Client

	}

	public Long getGUID() {
		return this.id;
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/attributedefinitions/"+this.id;
		return url;	
	}

	public AttributeDefinition convertAttributeDefinition(){
		AttributeDefinition atr = new AttributeDefinition();
		atr.setCategoryId(this.categoryId);
		atr.setConceptId(conceptId);
		ODRDataType dataT= new ODRDataType(); 
		atr.setDataType(dataT.convertDataType(this.dataType));
		atr.setDescription(this.description);
		atr.setId(id);
		atr.setName(this.name);
		if (presence=true)
		{atr.setPresence(Presence.MANDATORY);}
		atr.setSet(this.isSet);
		atr.setTypeId(this.typeId);
		return atr;
	}

	public long getId(){
		return this.id;
	}

	public long getConceptId(){
		return this.conceptId;	
	}

	public AttributeDefinition addAttributeDefinition(){
		AttributeDefinitionClient attrDefClient = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinition attrDef = attrDefClient.readAttributeDefinition(id, null);
		return attrDef;
	}

	public Long getEType() {
		return this.typeId;
	}

	public String getETypeURL() {
		if (this.typeId==null)
		{return null;} 
		else {
			String fullUrl = WebServiceURLs.getURL();
			String url  = fullUrl+"/types/"+this.typeId;
			return url;}
	}

	public String getRangeETypeURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/types/"+this.entityTypeID;
		return url;
	}

	public IDict getName() {
		Dict dict = new Dict();
		Iterator it = this.name.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = Locale.forLanguageTag((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());

		}
		return dict;
	}

	public IDict getDescription() {
		Dict dict = new Dict();
		Iterator it = this.description.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = Locale.forLanguageTag((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());
		}
		return dict;
	}



}
