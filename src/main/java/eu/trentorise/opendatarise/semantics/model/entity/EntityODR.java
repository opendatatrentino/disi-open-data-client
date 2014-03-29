package eu.trentorise.opendatarise.semantics.model.entity;

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
import it.unitn.disi.sweb.webapi.model.eb.sstring.SemanticString;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.services.EntityService;
/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 12 Mar 2014 refactored 22.03.2014
 * 
 */
public class EntityODR extends Structure implements IEntity {

	private List<Name> names;

	private Map<String, List<SemanticString>> descriptions;

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
		super.setAttributes(entity.getAttributes());
		this.setNames(entity.getNames());
		this.setDescriptions(entity.getDescriptions());
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
		return super.getId();
	}
	public void setGlobalId(Long globalId) {
		this.globalId = globalId;
	}

	public String getURL() {
		return sUrl;
	}

	public void setURL(String sUrl) {
		this.sUrl = sUrl;
	}

	public List<Name> getNames() {
		return names;
	}

	public void setNames(List<Name> names) {
		this.names = names;
	}

	public Map<String, List<SemanticString>> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<String, List<SemanticString>> descriptions) {
		this.descriptions = descriptions;
	}

	public Long getClassConceptId() {
		return classConceptId;
	}

	public void setClassConceptId(Long classConceptId) {
		this.classConceptId = classConceptId;
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
		this.etype=(EntityType) type;
		super.setTypeId(type.getGUID());
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
		entity.setDescriptions(this.descriptions);
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
		List<String> stName = name.get(locale.toLanguageTag());
		return stName.get(0);
	}

	public void setName(Locale locale, String name) {
		EntityService entServ = new EntityService(this.api);
		Name nam;
		if (this.names==null){
			List<Name> names = new ArrayList<Name>();
			nam = new Name();
			Map<String,List<String>> nameMap = new HashMap<String,List<String>>();
			List<String> strs = new ArrayList<String>();
			strs.add(name);
			nameMap.put(locale.toLanguageTag(), strs);
			nam.setNames(nameMap);
			names.add(nam);
			this.names=names;

		} else{ 

			nam=this.names.get(0);

			Map<String,List<String>> decomposedNames = nam.getNames();

			if (decomposedNames.containsKey(locale.toLanguageTag())){
				List<String> strs = decomposedNames.get(locale.toLanguageTag());
				strs.add(name);
				decomposedNames.put(locale.toLanguageTag(), strs);
			}

			else {
				List<String> strs = new ArrayList<String>();
				strs.add(name);
				decomposedNames.put(locale.toLanguageTag(), strs);
				System.out.println(decomposedNames);
				nam.setNames(decomposedNames);

			} 
			entServ.updateEntity(nam);
		}


	}

	public String getDescription(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String setDescription(Locale language, String description) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}



}


