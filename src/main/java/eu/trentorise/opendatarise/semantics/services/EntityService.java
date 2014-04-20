package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.ValueODR;

public class EntityService implements IEntityService {

	private IProtocolClient api;

	public EntityService(IProtocolClient api){

		this.api=api;
	}

	public Long createEntity(IEntity entity) {
		EntityODR ent = (EntityODR) entity;
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl= new  InstanceClient(this.api);
		System.out.println(e.toString());
		for (Attribute a : e.getAttributes()){
			System.out.println(a.getConceptId());
			System.out.println(a.getDataType());
			System.out.println(a.getDefinitionId());
			//	System.out.println(a.getValues().get(0).ge);
		}

		Long id = instanceCl.create(e);
		return id;
	}

	public Long createEntity(Name name) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Long id = instanceCl.create(name);
		return id;
	}

	public void updateEntity(Name name) {

		//EntityODR ent = (EntityODR) name;
		//Entity en=(Entity)ent;

		InstanceClient instanceCl= new  InstanceClient(this.api);
		//	Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
		//
		//		instance.setTypeId(ent.getEtype().getGUID());
		//		instance.setId(entity.getLocalID());
		//		List<IAttribute> attrs = entity.getStructureAttributes();
		//		List<Attribute> attributes = ent.convertToAttributes(attrs);
		//		instance.setAttributes(attributes);
		//Entity e = ent.convertToEntity();
		instanceCl.update(name);
	}

	public void deleteEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);
		Entity entity =  (Entity)instance; 
		EntityODR en = new EntityODR(this.api,entity);
		return en;
	}

	public void addAttribute(IEntity entity, IAttribute attribute) {
		EntityODR ent =(EntityODR) entity;
		ent.addAttribute(attribute);
	}

	public void addAttributeValue(IEntity entity, IAttribute attribute,
			IValue value) {
		AttributeODR atrODr = (AttributeODR) attribute;
		atrODr.addValue(value);
		IAttribute atr = atrODr;
		EntityODR ent =(EntityODR) entity;
		ent.addAttribute(atr);

	}


	public IAttribute createAttribute(IAttributeDef attrDef, List<Object> values){

		ValueODR value = new ValueODR();
		value.setValue(value);

		AttributeODR attribute = new AttributeODR(attrDef, value);

		return attribute;

	}


	public IAttribute createAttribute(IAttributeDef attrDef, Object value){
		AttributeDef ad = (AttributeDef) attrDef;

		if (ad.getName(Locale.ENGLISH).equals("Name"))
		{
			return createNameAttribute(attrDef, value);
		} else 
		{
			ValueODR val = new ValueODR();
			val.setValue(value);
			AttributeODR attribute = new AttributeODR(attrDef, val);
			return attribute;
		}

	}

	private IAttribute createNameAttribute(IAttributeDef attrDef, Object value){

		Name nameStructure = new Name();
		nameStructure.setEntityBaseId(1L);
		nameStructure.setTypeId(10L);
		Attribute nameAttribute = new Attribute();

		AttributeDef ad = (AttributeDef) attrDef; 

		List<Attribute> nameAttributes = new ArrayList<Attribute>();
		nameAttribute.setDefinitionId(attrDef.getGUID());
		List<Value> nameValues=new ArrayList<Value>();
		ValueODR val = new ValueODR();
		String input = (String) value;
		Value v = new Value(input, 1L);

		nameValues.add(v);
		nameAttribute.setValues(nameValues);
		nameAttributes.add(nameAttribute);

		List<Attribute>nameAttrs=new ArrayList<Attribute>();
		nameStructure.setAttributes(nameAttrs);

		EntityService es = new EntityService(api);
		long id =es.createEntity(nameStructure);
		//create entity as a client object


		AttributeODR a = new AttributeODR();

		a.setAttributeDefinition(attrDef);
		ValueODR valueNam = new ValueODR();
		valueNam.setValue(id);


		a.addValue(valueNam);

		return a;
	}


	public void updateAttributeValue(IEntity entity, IAttribute attribute,
			IValue newValue) {
		// TODO Auto-generated method stub

	}

	public void exportToRdf(List<Long> entityIds, Writer writer) {
		// TODO Auto-generated method stub

	}

	public void exportToJsonLd(List<Long> entityIds, Writer writer) {
		// TODO Auto-generated method stub

	}

	public void exportToCsv(List<Long> entityIds, Writer writer) {
		// TODO Auto-generated method stub

	}

	public void updateEntity(IEntity entity) {
		// TODO Auto-generated method stub

	}

	public void deleteEntity(String arg0) {
		// TODO Auto-generated method stub

	}

	public IEntity readEntity(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createEntityURL(IEntity entity) {
		Long id = createEntity(entity);

		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/instances/"+id;
		return url;
	}

}
