package eu.trentorise.opendatarise.semantic.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.Presence;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

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

	public AttributeDef(AttributeDefinition attrDef){
		this.isSet = attrDef.isSet();
		this.categoryId = attrDef.getCategoryId();
		this.conceptId = attrDef.getConceptId();
		this.dataType = attrDef.getDataType().name();
		this.id = attrDef.getId();
		this.description = attrDef.getDescription();
		this.name = attrDef.getName();
		this.typeId=attrDef.getTypeId();

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
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}

	public String getName(Locale locale) {
		return this.name.get(locale.toLanguageTag());
	}

	public String getDataType() {
		return this.dataType;
	}

	public IEntityType getRangeEType() {
		if (this.dataType.equals(DataTypes.COMPLEX_TYPE)){
			ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
			//TODO knowledge base assumed to be '1' change of API is required 
			List<ComplexType> cType = ctc.readComplexTypes(1L, this.conceptId, null, null);
			//TODO we take the first one from the list change of API is required 
			EntityType  etype = new EntityType(cType.get(0));
			return etype;
		} 
		else return null;
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
		// TODO Postponed due to the absence of the functionality on the API Client
		return null;
	}

	public String getURL() {
		// TODO Postponed due to the absence of the functionality on the API Client
		return null;
	}

	public String getURI() {
		// TODO Postponed due to the absence of the functionality on the API Client
		return null;
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

	private AttributeDefinition addAttributeDefinition(){
		AttributeDefinitionClient attrDefClient = new AttributeDefinitionClient(getClientProtocol());
		AttributeDefinition attrDef = attrDefClient.readAttributeDefinition(id, null);

		return attrDef;
	}
}
