package eu.trentorise.opendatarise.semantics.model.entity;

import eu.trentorise.opendatarise.semantics.services.EntityService;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 13 Mar 2014
 * 
 */
public class ValueODR extends Value implements IValue {
	private Long id;
	private Long attrId;
	private Object value;
	IProtocolClient api;



	@Override
	public String toString() {
		return "ValueODR [id=" + id + ", attrId=" + attrId + ", value=" + value
				+ ", api=" + api + "]";
	}

	public ValueODR(){}

	public ValueODR(IProtocolClient api, Value value ){
		this.id=value.getId();
		this.attrId=value.getAttributeId();

		if (value.getClass().equals(Name.class))

		{
			Instance instance= (Instance)this.value;
			//System.out.println(value.toString());
			EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
			Structure structure = es.readName(instance.getId());
			//Structure structure = 
			this.value=structure;
		} else this.value=value.getValue();

	}

	public ValueODR(Value value){
		this.id=value.getId();
		this.attrId=value.getAttributeId();
		this.value=value.getValue();
	}

	public Long getLocalID() {
		return this.id;
	}

	public Object getValue() {
		if (this.value!=null){
			System.out.println(value.getClass());
			if (value.getClass().equals(Name.class))
			{
				Instance instance= (Instance)this.value;
				//System.out.println(value.toString());
				EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
				Structure name = es.readName(instance.getId());
				this.value=name;
			}
			return this.value;}
		else {
			AttributeClient attrClient = new AttributeClient(this.api);
			this.value = attrClient.readValue(this.attrId, this.id,null).getValue();
		}
		return value;
	}

	public void setValue(Object value) {
		this.value= value;
	}

	public Value convertToValue(){
		Value value = new Value();
		if(super.getId()!=null){
			value.setId(super.getId());}
		else
			value.setId(this.id);
		if(super.getValue()!=null){
			value.setValue(super.getValue());
		} else
			value.setValue(this.value);

		value.setAttributeId(super.getAttributeId());
		return value;
	}

}
