package eu.trentorise.opendatarise.semantics.model.entity;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.model.knowledge.Dict;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.NLPService;
import eu.trentorise.opendatarise.semantics.services.SemanticTextFactory;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Duration;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Moment;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 12 Mar 2014 refactored 22.03.2014
 * 
 */
public class EntityODR extends Structure implements IEntity {

	private List<Name> names;

	private Map<String, List<SemanticText>> descriptions;

	private Moment start;

	private Moment end;

	private Duration duration;

	private Long classConceptId;

	private Long partOfId;

	private Long globalId;

	private Long localId;

	private String sUrl;

	private IEntityType etype;

	private IProtocolClient api;

	public EntityODR() {}
	public EntityODR(IProtocolClient api, Entity entity){

		this.api=api;
		super.setId((Long)entity.getId());
		this.setTypeId(entity.getTypeId());
		super.setEntityBaseId(entity.getEntityBaseId()) ;
		List<Attribute> attrs = entity.getAttributes();
		//TODO should not be in constructor
		for (Attribute at :attrs){
			if (at.getConceptId()==null){
				continue;
			}
			if (at.getConceptId()==3L){
				List<Value> vals = at.getValues();
				List<Value> fixedVals = new ArrayList<Value>();

				for (Value val : vals ){
					SemanticText stext= convertSemanticStringToText ((SemanticString) val.getSemanticValue()) ;
					Value fixedVal = new Value();
					fixedVal.setValue(stext);
					fixedVal.setId(val.getId());
					fixedVals.add(fixedVal);
				}				
				at.setValues(fixedVals);
			}

			if (at.getDataType()==DataType.CONCEPT){
				List<Value> vals = at.getValues();
				List<Value> fixedVals = new ArrayList<Value>();

				for (Value val : vals ){

					if (val.getValue().getClass().equals(ConceptODR.class)){
						fixedVals.add(val);
						continue;
					}
					Concept c = (Concept)val.getValue();
					ConceptODR codr = new ConceptODR(c);

					Value fixedVal = new Value();
					fixedVal.setId(val.getId());
					fixedVal.setValue(codr);
					fixedVals.add(fixedVal);
				}				
				at.setValues(fixedVals);
			}

			if (at.getConceptId()==5L){
				List<Value> vals = at.getValues();
				List<Value> fixedVals = new ArrayList<Value>();
				EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
				Instance inst = (Instance)vals.get(0).getValue();	
				IEntity e = es.readEntity(inst.getId());
				//	EntityODR enodr = new EntityODR(WebServiceURLs.getClientProtocol(), e);
				Value fixedVal = new Value();
				fixedVal.setId(vals.get(0).getId());
				fixedVal.setValue(e);
				fixedVals.add(fixedVal);

				at.setValues(fixedVals);
			}

			if (at.getConceptId()==111001L){
				List<Value> vals = at.getValues();
				List<Value> fixedVals = new ArrayList<Value>();
				EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
				Instance inst = (Instance)vals.get(0).getValue();	
				IStructure e = es.readStructure(inst.getId());
				//	EntityODR enodr = new EntityODR(WebServiceURLs.getClientProtocol(), e);
				Value fixedVal = new Value();
				fixedVal.setId(vals.get(0).getId());
				fixedVal.setValue(e);
				fixedVals.add(fixedVal);

				at.setValues(fixedVals);
			}
		}

		super.setAttributes(attrs);
		this.setNames(entity.getNames());
		this.setDescriptions(convertDescriptionToODR(entity.getDescriptions()));
		this.setPartOfId(entity.getPartOfId());
		this.setURL(entity.getsUrl());
		this.setDuration(entity.getDuration());
		this.setStart(entity.getStart());
		this.setEnd(entity.getEnd());
		this.setClassConceptId(entity.getClassConceptId());
		this.setGlobalId(entity.getGlobalId());

	}



	//	public EntityODR(IProtocolClient api, Instance instance){
	//
	//		this.api=api;
	//		super.setId(instance.getId());
	//		this.setTypeId(instance.getTypeId());
	//		super.setEntityBaseId(instance.getEntityBaseId()) ;
	//	}

	private void setClassConceptId(Long classConceptId) {
		this.classConceptId = classConceptId;

	}
	@Override
	public String toString() {
		String str = "EntityODR [\n"+"id"+super.getId()
				+ "names=" + names + 
				", descriptions=" + descriptions
				+ ", start=" + start 
				+ ", end=" + end + ", duration="
				+ duration + ", classConceptId=" 
				+ classConceptId
				+ ", partOfId=" + partOfId 
				+ ", globalId=" + globalId
				+ ", sUrl=" + sUrl + ","
				+ "\n etype=" + etype + ",\nattributes="+
				"[\n";
		for(IAttribute attr:super.getStructureAttributes()){
			str += "\t"+attr+"\n";
		}

		str+="]";
		return str;
	}


	public Long getGUID() {
		return globalId;
	}

	public Long getLocalID() {
		
		if (super.getId()!=null){
		return super.getId();}
		else return this.localId;
		
	}
	public void setGlobalId(Long globalId) {
		this.globalId = globalId;
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url="";
		if(this.localId!=null){
			url  = fullUrl+"/instances/"+this.localId;}
		else
		{
			url  = fullUrl+"/instances/"+this.getId();
		}
		return url;	
	}

	public void setURL(String sUrl) {
		this.sUrl = sUrl;
	}

	public List<Name> getNames() {
		return names;
	}

	public IDict getName() {
		Dict dict = new Dict();
		if((this.names==null)&&(super.getId()==null)){
			return dict;
		} else
			if(this.names==null){
				EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
				EntityODR e =(EntityODR)es.readEntity(super.getId());
				this.names = e.getNames();
				this.descriptions=e.getDescriptions();
				this.classConceptId = e.getClassConceptId();
			}
			else
				for (Name name: this.names)
				{
					Map<String,List<String>> nameMap = name.getNames();


					Iterator it = nameMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry pairs = (Map.Entry)it.next();
						Locale l = NLPService.languageTagToLocale((String) pairs.getKey());
						ArrayList<String> vals = (ArrayList<String>) pairs.getValue();
						//System.out.println(vals.get(0));
						dict = dict.putTranslation(l, vals.get(0));

					}

				}
		return dict;
	}

	public void setNames(List<Name> names) {
		this.names = names;
	}

	public Map<String, List<SemanticText>> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<String, List<SemanticText>> descriptions) {
		this.descriptions = descriptions;
	}

	public Long getClassConceptId() {
		return classConceptId;
	}

	public void setClassConceptId(Long classConceptId, AttributeDef classAttributeDef) {
		this.classConceptId = classConceptId;
		List<IAttribute> attributes =  super.getStructureAttributes() ;
		if (attributes.size()!=0){
			for (IAttribute attr: attributes){
				AttributeDef ad = (AttributeDef) attr;
				if (ad.getName(Locale.ENGLISH).equals("Class")){
					ValueODR val = new ValueODR();
					val.setValue(classConceptId);
					attr.addValue(val);
				} else 
				{
					ValueODR val = new ValueODR();
					val.setValue(classConceptId);
					AttributeODR at = new AttributeODR(classAttributeDef, val);
					attributes.add(at);
				}
			}
		} else {
			List<IAttribute> attrs = new ArrayList<IAttribute>();
			ValueODR val = new ValueODR();
			val.setValue(classConceptId);
			AttributeODR at = new AttributeODR(classAttributeDef, val);
			attrs.add(at);
			super.setStructureAttributes(attrs);
		}


	}

	public Long getPartOfId() {
		return partOfId;
	}

	public void setPartOfId(Long partOfId) {
		this.partOfId = partOfId;
	}

	public Moment getStart() {
		return start;
	}

	public void setStart(Moment start) {
		this.start = start;
	}

	public Moment getEnd() {
		return end;
	}

	public void setEnd(Moment end) {
		this.end = end;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	//	public String getURI() {
	//		throw new UnsupportedOperationException("Such a field does not exists in the model.");
	//	}
	//
	//	public String getExternalID() {
	//		throw new UnsupportedOperationException("Such a field does not exists in the model.");
	//	}
	//
	//	public void setExternalID(String externalID) {
	//		throw new UnsupportedOperationException("Such a field does not exists in the model.");
	//	}

	public List<IAttribute> getEntityAttributes() {
		if (super.getAttributes()!=null){
			List<IAttribute> atrs = convertToAttributeODR(super.getAttributes());
			return atrs;
		}else 
		{
			AttributeClient attrCl = new AttributeClient(this.api);
			Pagination page = new Pagination(1,10);
			List<Attribute> attrs =attrCl.readAttributes(super.getId(), null, null);
			super.setAttributes(attrs);
			List<IAttribute> attrODR = convertToAttributeODR(attrs);
			return attrODR;
		}
	}

	public void setEntityAttributes(List<IAttribute> attributes) {
		//client side
		super.setAttributes(convertToAttributes(attributes));
		//server side
		//		InstanceClient instanceCl= new  InstanceClient(api);
		//		Instance instance = instanceCl.readInstance(super.getId(), null);
		//		List<Attribute> attrs = new ArrayList<Attribute>();
		//		for (IAttribute attr:attributes ){
		//			AttributeODR attrODR = (AttributeODR)attr;
		//			attrs.add(attrODR.convertToAttribute());
		//		}
		//		instance.setAttributes(attrs);
		//instanceCl.update(instance);
	}

	public void addAttribute(IAttribute attribute) {
		//client side
		AttributeODR attrODR = (AttributeODR) attribute; 
		Attribute attr = attrODR.convertToAttribute();
		List<Attribute> attrs = super.getAttributes();
		attrs.add(attr);
		super.setAttributes(attrs);
		//server side - create attr 
		AttributeClient attrCl = new AttributeClient(api);
		attrCl.create(attr);
		// add attr to the list of existing attrs

	}

	public IEntityType getEtype() {
		if(this.etype!=null){
			return this.etype;
		}
		else { 
			ComplexTypeClient ctc = new ComplexTypeClient(this.api);
			ComplexType ctype = ctc.readComplexType(super.getTypeId(), null);
			EntityType etype= new EntityType(ctype);
			this.etype=etype;
		}
		return etype;
	}

	public void setEtype(IEntityType type) {
		//locally
		EntityType etype = (EntityType) type;
		this.etype=etype;
		super.setTypeId(etype.getGUID());
		//on the server
		//		InstanceClient instanceCl= new  InstanceClient(this.api);
		//		Instance instance = instanceCl.readInstance(super.getId(), null);
		//		instance.setTypeId(type.getGUID());
		//	instanceCl.update(instance);
	}

	private List<IAttribute> convertToAttributeODR(List<Attribute> attributes){
		List<IAttribute> attributesODR = new ArrayList<IAttribute>();
		for(Attribute attr: attributes){
			AttributeODR attrODR = new AttributeODR(api, attr);
			attributesODR.add(attrODR);
		}
		return attributesODR;
	}

	public List<Attribute> convertToAttributes(List<IAttribute> attributes){
		List<Attribute> attrs = new ArrayList<Attribute>();
		for(IAttribute attr: attributes){
			AttributeODR attribute =  (AttributeODR) attr;
			Attribute at = attribute.convertToAttribute();
			attrs.add(at);
		}
		return attrs;
	}

	public Entity convertToEntity() {
		Entity entity = new Entity();
		entity.setTypeId(this.getTypeId());
		entity.setDuration(this.duration);
		entity.setAttributes(super.getAttributes());
		entity.setDescriptions(convertDescriptionToSWEB(this.descriptions));
		entity.setEnd(this.end);
		entity.setGlobalId(this.globalId);
		entity.setId(super.getId());
		entity.setNames(this.names);
		entity.setEntityBaseId(this.getEntityBaseId());
		entity.setStart(this.start);
		entity.setPartOfId(this.partOfId);
		entity.setEntityBaseId(this.getEntityBaseId());
		entity.setsUrl(this.sUrl);

		return entity;
	}

	public String getName(Locale locale) {

		Map<String,List<String>>  name = this.names.get(0).getNames();
		List<String> stName = name.get(NLPService.localeToLanguageTag(locale));
		return stName.get(0);
	}

	//	public void setName(Locale locale, String name) {
	//
	//		EntityService entServ = new EntityService(this.api);
	//		Name nameStructure = new Name();
	//		List<Attribute> attributes = super.getAttributes();
	//		Attribute nameAttribute = new Attribute();
	//		List<IAttributeDef> attrs = this.getEtype().getAttributeDefs();
	//		Long nameAttrDefID= 0L;
	//		for (IAttributeDef atd:attrs){
	//			AttributeDef ad = (AttributeDef) atd;
	//			if (ad.getName(locale.ENGLISH).equals("Name"))
	//				//				AttributeDef ad = (AttributeDef) atd;
	//				//				nameAttrDefID =ad.getGUID();
	//		}
	//		nameAttribute.setDefinitionId(nameAttrDefID);
	//		attributes.add(nameAttribute);
	//		List<Value>nameValues=new ArrayList<Value>();
	//		nameValues.add(new Value(name, 1L));
	//		nameAttribute.setValues(nameValues);
	//		attributes.add(nameAttribute);
	//
	//	}

	public void setName(Locale locale, List<String> names) {
        throw new UnsupportedOperationException("todo to implement");

	}
	public IDict getDescription() {
		Dict dict = new Dict();
		if (this.descriptions==null){
			return dict;
		}
		Map<String,List<SemanticText>> descriptionMap =  this.descriptions;
		if(descriptionMap.isEmpty()){
			return dict;
		}
		Iterator it = descriptionMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Locale l = NLPService.languageTagToLocale((String)pairs.getKey());
			ArrayList<SemanticText> vals = (ArrayList<SemanticText>) pairs.getValue();
			dict = dict.putTranslation(l, vals.get(0).getText());

		}

		return dict;
	}

	/** Method converts description attribute from  EntityPedia datatype to ODR datatype.
	 * @param descriptionSString
	 * @return 
	 */
	private Map<String,List<SemanticText>> convertDescriptionToODR(Map<String,List<SemanticString>> descriptionSString){

		Map<String,List<SemanticText>> odrDescriptionMap = new  HashMap<String,List<SemanticText>>();
		if(descriptionSString==null){
			return odrDescriptionMap;
		} 
		SemanticTextFactory stf = new SemanticTextFactory();

		Iterator it = descriptionSString.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			List<SemanticText> sTextList = new ArrayList<SemanticText>();	

			List<SemanticString> SStringList =  (List<SemanticString>) pairs.getValue();
			for (SemanticString sstring:SStringList){
				SemanticText stext = (SemanticText) stf.semanticText(sstring);
				sTextList.add(stext);
			}
			odrDescriptionMap.put((String)pairs.getKey(), sTextList);
			it.remove(); // avoids a ConcurrentModificationException
		}

		return odrDescriptionMap;

	} 

	private SemanticText convertSemanticStringToText(SemanticString sstring){

		SemanticTextFactory stf = new SemanticTextFactory();
		SemanticText stext = (SemanticText) stf.semanticText(sstring);

		return stext;
	}

	public Map<String,List<SemanticString>> convertDescriptionToSWEB(Map<String,List<SemanticText>> descriptionSText){

		Map<String,List<SemanticString>> epDescriptionMap = new  HashMap<String,List<SemanticString>>();
		SemanticTextFactory stf = new SemanticTextFactory();
		if(descriptionSText==null){
			return epDescriptionMap;
		}
		Iterator it = descriptionSText.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			List<SemanticString> sStringList = new ArrayList<SemanticString>();	

			List<SemanticText> SStringList =  (List<SemanticText>) pairs.getValue();
			for (SemanticText stext:SStringList){
				SemanticString sstring = (SemanticString) stf.semanticString(stext);
				sStringList.add(sstring);
			}
			epDescriptionMap.put((String)pairs.getKey(), sStringList);
			it.remove(); // avoids a ConcurrentModificationException
		}

		return epDescriptionMap;
	} 

	public void setName(Locale locale, String name) {
        throw new UnsupportedOperationException("todo to implement");

	}
	public void setDescription(Locale language, String description) {
        throw new UnsupportedOperationException("todo to implement");

	}
}


