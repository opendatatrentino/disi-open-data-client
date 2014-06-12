package eu.trentorise.opendatarise.semantics.model.entity;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.model.knowledge.Dict;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.NLPService;
import static eu.trentorise.opendatarise.semantics.services.NLPService.localeToLanguageTag;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
		return name.get(localeToLanguageTag(locale));
	}

	public Map<String, String> getName1() {

		return name;
	}

	public IConcept getConcept() {
		ConceptODR concept= new ConceptODR();
		concept = concept.readConcept(conceptId);
		return concept;
	}

	public void setConcept(IConcept concept) {

		ConceptODR conc = (ConceptODR) concept;
		ComplexTypeClient ctypeCl = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
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
		if (this.attrs!=null){
		return this.attrs;}
		else {
			EntityTypeService ets = new EntityTypeService();
			EntityType etype= ets.getEntityType(this.id);
			this.attrs= etype.getAttributeDefs();
			return this.attrs;
		}
	}

	public void addAttributeD(AttributeDef attrDef) {
		ArrayList<IAttributeDef> attrDefList = (ArrayList<IAttributeDef>) this.attrs;
		//adding attribute on client side
		attrDefList.add(attrDef);
		this.attrs = attrDefList;
		//adding attribute on server side
		AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient(WebServiceURLs.getClientProtocol());
		List<AttributeDefinition> attrList  = attrDefCl.readAttributeDefinitions(this.id, null, null, null);
		ArrayList<AttributeDefinition> atrList = new ArrayList<AttributeDefinition>(attrList);
		atrList.add(attrDef.convertAttributeDefinition());
		ComplexTypeClient ctypeCl = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
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
		AttributeDefinitionClient attrDefCl = new AttributeDefinitionClient(WebServiceURLs.getClientProtocol());
		List<AttributeDefinition> attrList  =attrDefCl.readAttributeDefinitions(this.id, null, null, null);
		//TODO properly test this part
		for(int i=0; i<attrList.size(); i++){
			if (attrDefID==attrList.get(i).getId()){
				attrDefList.remove(i);
				break;
			}
		}
		ComplexTypeClient ctypeCl = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
		ComplexType ctype  =ctypeCl.readComplexType(this.conceptId, null);
		ctype.setAttributes(attrList);
	}

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

	public Long getConceptID(){
		return this.conceptId;
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/types/"+this.id;

		return url;
	}

	public void addAttributeDef(IAttributeDef attr) {
		AttributeDef atDef =(AttributeDef) attr; 
		addAttributeD(atDef);

	}


	public IDict getName() {
		Dict dict = new Dict();
		Iterator it = this.name.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = NLPService.languageTagToLocale((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());

		}
		return dict;
	}

	public IDict getDescription() {
		Dict dict = new Dict();
		Iterator it = this.description.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = NLPService.languageTagToLocale((String)pairs.getKey());
			dict = dict.putTranslation(l, (String)pairs.getValue());
		}
		return dict;
	}


	public void removeAttributeDef(String attrDefURL) {
        throw new UnsupportedOperationException("todo to implement");

	}


	public void removeUniqueIndex(String uniqueIndexURL) {
        throw new UnsupportedOperationException("todo to implement");

	}

	public IAttributeDef getNameAttrDef() {
		List<IAttributeDef> attrDefs =getAttributeDefs();
		for (IAttributeDef attr: attrDefs){
			AttributeDef ad = (AttributeDef) attr;
			String adName = ad.getName(Locale.ENGLISH);
			if (ad.getName(Locale.ENGLISH).equalsIgnoreCase("name")){
				return attr;
			}
		}
		return null;
	}

	public IAttributeDef getDescriptionAttrDef() {
		List<IAttributeDef> attrDefs = getAttributeDefs();
		for (IAttributeDef attr: attrDefs){
			AttributeDef ad = (AttributeDef) attr;
			if (ad.getName(Locale.ENGLISH).equals("Description")){
				return attr;
			}
		}
		return null;
	}


}
