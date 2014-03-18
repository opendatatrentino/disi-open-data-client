package eu.trentorise.opendatarise.semantics.model.entity;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.AttributeClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IValue;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 13 Mar 2014
 * 
 */
public class AttributeODR implements IAttribute {

	private IProtocolClient api;
	private List<IValue> values; 
	private long id;
	private IAttributeDef attrDef;
	private long attrDefId;
	private long conceptId;
	private long instanceID;

	public AttributeODR(IProtocolClient api, Attribute attribute){
		this.api=api;
		this.id=attribute.getId();
		this.attrDefId = attribute.getDefinitionId();
		this.conceptId =attribute.getConceptId();
		this.instanceID=attribute.getInstanceId();
		this.values =convertToValueODR(attribute.getValues());
	}

	public Long getGUID() {
		return id;
	}

	public IAttributeDef getAttributeDefinition() {
		if(this.attrDef==null){
			IAttributeDef attrDef = new AttributeDef(attrDefId);
			return attrDef;}
		else return this.attrDef;
	}

	public void setAttributeDefinition(IAttributeDef ad) {
		//client side
		this.attrDef=ad;
		//server side
//		InstanceClient inClient = new InstanceClient(api);
//		inClient.update(arg0);
		
	}

	public void addValue(IValue value) {
		//client side
		this.values.add(value);
		//server side
		ValueODR valODR = (ValueODR)value;
		Value val = valODR.convertToValue();
		AttributeClient attrCl = new AttributeClient(api);
		Attribute attr = attrCl.readAttribute(this.id, null);
		List<Value> vals = attr.getValues();
		vals.add(val);
		attr.setValues(vals);
	}                                                                    

	public void removeValue(long valueID) {
		//remove from server side
		AttributeClient attrCl = new AttributeClient(api);
		Attribute attr = attrCl.readAttribute(this.id, null);
		Value atrVal = attrCl.readValue(attr.getId(), valueID, null);
		attrCl.delete(atrVal);
		//client side
		ArrayList<IValue> values = (ArrayList<IValue>) this.values;
		for(IValue val: values){
			if(val.getGUID()==valueID){
				values.remove(val);
				return;
			}

		}
	}

	public List<IValue> getValues() {
		return this.values;
	}

	public IValue getFirstValue() {
		return this.values.get(0);
	}

	public Long getValuesCount() {
		return (long) values.size();
	}

	private List<IValue> convertToValueODR(List<Value> vals){
		List<IValue> values = new ArrayList<IValue>();
		for(Value val:vals){
			ValueODR value = new ValueODR(val);
			values.add(value);
		}
		return values;
	}
	
	public Attribute convertToAttribute(){
		
		Attribute attribute = new Attribute();
		attribute.setConceptId(this.conceptId);
		attribute.setId(this.id);
		attribute.setInstanceId(this.instanceID);
		attribute.setDefinitionId(this.attrDefId);
		attribute.setValues(this.convertValuesList());
		
		return attribute;
	}
	
	private List<Value> convertValuesList(){
		List<IValue> valuesODR = this.values;
		List<Value> values = new ArrayList<Value>();
		for(IValue val: valuesODR){
			ValueODR valODR = (ValueODR) val;
			Value value = valODR.convertToValue();
			values.add(value);
		}
		return values;
	}

}
