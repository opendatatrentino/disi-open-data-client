package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.io.Writer;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;

public class EntityService implements IEntityService {

	private IProtocolClient api;

	public EntityService(IProtocolClient api){

		this.api=api;
	}

	public Long createEntity(IEntity entity) {
		EntityODR ent = (EntityODR) entity;
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Long id = instanceCl.create(e);
		//System.out.println("Created entity ID: "+id);
		return id;


	}

	public void updateEntity(IEntity entity) {

		EntityODR ent = (EntityODR) entity;
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(ent.getLocalID(), null);

		instance.setTypeId(ent.getEtype().getGUID());
		instance.setId(entity.getLocalID());
		List<IAttribute> attrs = entity.getStructureAttributes();
		List<Attribute> attributes = ent.convertToAttributes(attrs);
		instance.setAttributes(attributes);
		instanceCl.update(instance);
	}

	public void deleteEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		IEntity entity = new EntityODR(api, instance); 
		return entity;
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

}
