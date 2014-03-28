package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;

public class Structure  extends Instance implements IStructure
{
	private IProtocolClient api;


	public Long getLocalID() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IAttribute> getStructureAttributes() {
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

	public void setStructureAttributes(List<IAttribute> attributes) {
		super.setAttributes(convertToAttributes(attributes));		
	}

	public IEntityType getEtype() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEtype(IEntityType type) {
		// TODO Auto-generated method stub
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
}
