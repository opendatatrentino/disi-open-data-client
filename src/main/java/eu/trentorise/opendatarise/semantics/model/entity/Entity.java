package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 12 Mar 2014
 * 
 */
public class Entity implements IEntity {

	private long id;
	private long globalId;
	private List<IAttribute> attributes;
	private EntityType etype;
	private long typeId;
	private IProtocolClient api;


	public Entity(IProtocolClient api, Instance instance){

		this.api=api;
		this.id = instance.getId();
		this.typeId= instance.getTypeId();

	}

	//	public Entity(Instance instance,IProtocolClient api ){
	//		this.id = instance.getId();
	//	}

	public Long getLocalID() {
		// TODO Auto-generated method stub
		return this.id;
	}

	public Long getGUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setURL(String url) {
		// TODO Auto-generated method stub

	}

	public String getExternalID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExternalID(String externalID) {
		// TODO Auto-generated method stub

	}

	public List<IAttribute> getAttributes() {
		if (this.attributes!=null){
			return this.attributes;
		}else 
		{
			AttributeClient attrCl = new AttributeClient(this.api);
			Pagination page = new Pagination(1,10);
			List<Attribute> attrs =attrCl.readAttributes(this.id, null, page);
			List<IAttribute> attrODR = convertToAttributeODR(attrs);
			this.attributes=attrODR;
			return attrODR;
		}
	}

	public void setAttributes(List<IAttribute> attributes) {
	//client side
		this.attributes=attributes;
		//server side
		

	}

	public void addAttribute(IAttribute attribute) {
	//client side
		this.attributes.add(attribute);
		//server side 
		AttributeODR attrODR = (AttributeODR) attribute; 
		Attribute attr = attrODR.convertToAttribute();
		AttributeClient attrCl = new AttributeClient(api);
		attrCl.create(attr);
		
	}

	public IEntityType getEtype() {
		if(this.etype!=null){
			return this.etype;
		}
		else { 
			ComplexTypeClient ctc = new ComplexTypeClient(this.api);
			ComplexType ctype = ctc.readComplexType(this.typeId, null);
			EntityType etype= new EntityType(ctype);
			this.etype=etype;
		}
		return etype;
	}

	public void setEtype(IEntityType type) {
		this.etype=(EntityType) type;
	}

	private List<IAttribute> convertToAttributeODR(List<Attribute> attributes){
		List<IAttribute> attributesODR = new ArrayList<IAttribute>();
		for(Attribute attr: attributes){
			AttributeODR attrODR = new AttributeODR(api, attr);
			attributesODR.add(attrODR);
		}
		return attributesODR;
	}

}
