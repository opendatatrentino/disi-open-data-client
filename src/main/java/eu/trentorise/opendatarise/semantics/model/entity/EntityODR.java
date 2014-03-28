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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 12 Mar 2014 refactored 22.03.2014
 * 
 */
public class EntityODR extends Instance implements IEntity {

	private List<Name> names;

	private Map<String, List<SemanticString>> descriptions;

	private Moment start;

	private Moment end;

	private Duration duration;

	private Long classConceptId;

	private Long partOfId;

	private Long globalId;

	private String sUrl;

	private IEntityType etype;

	private IProtocolClient api;


	public EntityODR() {}
	public EntityODR(IProtocolClient api, Instance instance){

		this.api=api;
		super.setId(instance.getId());
		this.setTypeId(instance.getTypeId());
		super.setEntityBaseId(instance.getEntityBaseId()) ;
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
		//entity.setsUrl(this.sUrl);

		return entity;
	}
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getExternalID() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setExternalID(String externalID) {
		// TODO Auto-generated method stub

	}
	public List<IAttribute> getStructureAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setStructureAttributes(List<IAttribute> attributes) {
		// TODO Auto-generated method stub
		
	}
	public String getName(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	public void setName(Locale locale, String name) {
		// TODO Auto-generated method stub
		
	}
	public String getDescription(Locale language) {
		// TODO Auto-generated method stub
		return null;
	}
	public String setDescription(Locale language, String description) {
		// TODO Auto-generated method stub
		return null;
	}



}


