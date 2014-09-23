package eu.trentorise.opendata.disiclient.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IStructure;

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
		EntityTypeService ets = new EntityTypeService();
                Long id = super.getTypeId();
                if (id == null){
                    throw new RuntimeException("Got a null id for super.getTypeId() in Structure.java!");
                } else {
                    return ets.getEntityType((long) id);
                }	
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
		//if(super.getId()!=null){
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
		throw new DisiClientException("There is no attribute having attributeDef URL: " + attrDefURL + " in the structure with URL " + getURL());
	}

	public String getEtypeURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/types/"+super.getTypeId();
		return url;
	}
	
	Structure convertToStructure(it.unitn.disi.sweb.webapi.model.eb.Structure st){
		Structure s = new Structure();
		s.setAttributes(st.getAttributes()); 
		s.setEntityBaseId(st.getEntityBaseId());
		s.setTypeId(st.getTypeId());
		s.setId(st.getId());
		return s;
	}

}


