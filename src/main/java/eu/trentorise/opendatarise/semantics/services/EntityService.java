package eu.trentorise.opendatarise.semantics.services;

import java.io.Writer;
import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;

public class EntityService implements IEntityService {

	public void createEntity(IEntity entity) {
		// TODO Auto-generated method stub
		
	}

	public void updateEntity(IEntity entity) {
		// TODO Auto-generated method stub
		
	}

	public void deleteEntity(long entityID) {
		// TODO Auto-generated method stub
		
	}

	public IEntity readEntity(long entityID) {
		// TODO Auto-generated method stub
		return null;
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
