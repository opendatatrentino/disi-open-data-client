package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Feb 2014
 * 
 */
public class EntityType implements IEntityType{

	private long conceptId;
	private List<IAttributeDef> attrs;
	private long id;
	private Map<String,String> description;
	private Map<String,String> name;

	public EntityType (ComplexType cType){

		this.conceptId=	cType.getConceptId();
		this.id= cType.getId();
		this.description= cType.getDescription();
		this.name=cType.getName();
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

		return name.get(locale.toLanguageTag());
	}

	public IConcept getConcept() {
		ConceptODR concept= new ConceptODR();
		concept = concept.readConcept(conceptId);
		return null;
	}

	public void setConcept(IConcept concept) {

		ConceptODR conc = (ConceptODR) concept;
		ComplexTypeClient ctypeCl = new ComplexTypeClient( getClientProtocol());
		ComplexType ctype  =ctypeCl.readComplexType(this.conceptId, null);
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


	public List<IAttributeDef> getAttributeDefs() {
		return this.attrs;
	}

	public void addAttributeD(AttributeDef attrDef) {
		ArrayList<IAttributeDef> attrDefList = (ArrayList<IAttributeDef>) this.attrs;
		//adding attribute on client side
		attrDefList.add(attrDef);
		this.attrs = attrDefList;
		//adding attribute on server side
		AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient(getClientProtocol());
		List<AttributeDefinition> attrList  = attrDefCl.readAttributeDefinitions(this.id, null, null, null);
		ArrayList<AttributeDefinition> atrList = new ArrayList<AttributeDefinition>(attrList);
		atrList.add(attrDef.convertAttributeDefinition());
		ComplexTypeClient ctypeCl = new ComplexTypeClient( getClientProtocol());
		ComplexType ctype  =ctypeCl.readComplexType(this.id, null);
		ctype.setAttributes(attrList);
	}

	public void removeAttributeDef(long attrDefID) {
		//TODO properly test this part
		List<IAttributeDef> attrDefList = this.attrs;
		for(int i=0; i<attrDefList.size(); i++){
			AttributeDef attrDef =(AttributeDef) attrDefList.get(i);
			if (attrDefID==attrDef.getId()){
				attrDefList.remove(i);
				break;
			}
		}
		//adding attribute on client side
		this.attrs = attrDefList;
		//adding attribute on server side
		AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient( getClientProtocol());
		List<AttributeDefinition> attrList  =attrDefCl.readAttributeDefinitions(this.id, null, null, null);
		//TODO properly test this part
		for(int i=0; i<attrList.size(); i++){
			if (attrDefID==attrList.get(i).getId()){
				attrDefList.remove(i);
				break;
			}
		}
		ComplexTypeClient ctypeCl = new ComplexTypeClient( getClientProtocol());
		ComplexType ctype  =ctypeCl.readComplexType(this.conceptId, null);
		ctype.setAttributes(attrList);
	}

	public List<IUniqueIndex> getUniqueIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeUniqueIndex(long uniqueIndexID) {
		// TODO Auto-generated method stub

	}

	public void addUniqueIndex(IUniqueIndex uniqueIndex) {
		// TODO Auto-generated method stub

	}

	public Long getGUID() {
		return this.id;
	}
	
	public Long getConceptID(){
		return this.conceptId;
	}

	public String getURL() {
		String st  = "http://opendata.disi.unitn.it:8080/odt/types/"+this.id+
			"?includeAttributes=false&includeAttributesAsProperties=false&includeRestrictions=false&includeRules=false&includeTimestamps=false";

		return st;
	}

	public String getURI() {
		String st  = "http://opendata.disi.unitn.it:8080/odt/types/"+this.id+
				"?includeAttributes=false&includeAttributesAsProperties=false&includeRestrictions=false&includeRules=false&includeTimestamps=false";

			return st;
	}

	/** The method returns client protocol 
	 * @return returns an instance of ClientProtocol that contains information where to connect(Url adress and port) and locale
	 */
	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}

	public void addAttributeDef(IAttributeDef attr) {
		AttributeDef atDef =(AttributeDef) attr; 
		addAttributeD(atDef);

	}
}
