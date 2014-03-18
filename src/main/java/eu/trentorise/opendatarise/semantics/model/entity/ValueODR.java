package eu.trentorise.opendatarise.semantics.model.entity;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import eu.trentorise.opendata.semantics.model.entity.IValue;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 13 Mar 2014
 * 
 */
public class ValueODR implements IValue {
	private long id;
	private long attrId;
	private Object value;
	IProtocolClient api;

	public ValueODR(IProtocolClient api, Value value ){
		this.id=value.getId();
		this.attrId=value.getAttributeId();
		this.value=value.getValue();

	}

	public ValueODR(Value value){
		this.id=value.getId();
		this.attrId=value.getAttributeId();
		this.value=value.getValue();
	}

	public Long getGUID() {
		return this.id;
	}

	public Object getValue() {
		if (this.value!=null){
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
		value.setId(this.id);
		value.setValue(this.value);
		value.setAttributeId(this.attrId);
		return value;
	}

}
