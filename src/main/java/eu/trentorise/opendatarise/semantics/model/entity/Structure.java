package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendatarise.semantics.DisiClientException;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;

public class Structure  extends Instance implements IStructure
{

	private IProtocolClient api;


	public Long getLocalID() {
		return super.getId();
	}

	public Structure(){
		this.api = getClientProtocol();

	}

	public Structure(Name name){
		this.api = getClientProtocol();
	}

	public List<IAttribute> getStructureAttributes() {
		if (super.getAttributes()!=null){
			List<IAttribute> atrs = convertToAttributeODR(super.getAttributes());
			return atrs;
		}else 
		{
			AttributeClient attrCl = new AttributeClient(getClientProtocol());
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
		//TODO discuss with David: structure is not an entity! it can not have ID
//		EntityTypeService ets = new EntityTypeService();
//		return ets.getEntityType(super.getTypeId());
		throw new UnsupportedOperationException("todo to implement");
	}

	public void setEtype(IEntityType type) {
		throw new UnsupportedOperationException("todo to implement");
	}

	private List<IAttribute> convertToAttributeODR(List<Attribute> attributes){
		List<IAttribute> attributesODR = new ArrayList<IAttribute>();
		for(Attribute attr: attributes){
			AttributeODR attrODR = new AttributeODR(getClientProtocol(), attr);
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

	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}

	public String getURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/instances/"+super.getId();
		return url;
	}

	public void setURL(String url) {
		throw new UnsupportedOperationException("todo to implement");

	}

	public IAttribute getAttribute(String attrDefURL) {
		List<IAttribute> attributes = getStructureAttributes();
		for (IAttribute attribute: attributes){
			if(attribute.getAttributeDefinition().getURL().equals(attrDefURL)){
				return attribute;
			}
		}
		throw new DisiClientException("There is no attribute in the structure with a given attributeDef");
	}

	public String getEtypeURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/types/"+super.getTypeId();
		return url;
	}

}


