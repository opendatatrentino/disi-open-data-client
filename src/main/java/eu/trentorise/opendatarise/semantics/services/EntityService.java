package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.io.Writer;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.model.entity.Entity;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;

public class EntityService implements IEntityService {
	
	private IProtocolClient api;


	public void createEntity(IEntity entity) {
		
		
	}

	public void updateEntity(IEntity entity) {
		
		Entity ent = (Entity) entity;
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
		
		instance.setTypeId(ent.getEtype().getGUID());
		instance.setId(entity.getLocalID());
		List<IAttribute> attrs = entity.getAttributes();
		List<Attribute> attributes = convertToAttributes(attrs);
		instance.setAttributes(attributes);
		instanceCl.update(instance);
				
		
	}

	private List<Attribute> convertToAttributes(List<IAttribute> attrs) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		IEntity entity = new Entity(api, instance); 
		return entity;
	}

	public void addAttribute(IEntity entity, IAttribute attribute) {
		// TODO Auto-generated method stub
		
	}

	public void addAttributeValue(IEntity entity, IAttribute attribute,
			IValue value) {
		// TODO Auto-generated method stub
		
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
