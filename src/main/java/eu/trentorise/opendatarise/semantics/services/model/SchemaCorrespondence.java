package eu.trentorise.opendatarise.semantics.services.model;

import java.util.List;

import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 24 Feb 2014
 * 
 */
public class SchemaCorrespondence implements ISchemaCorrespondence {

	EntityType etype;
	Float score;
	List<IAttributeCorrespondence> attributeCorrespondences;
	
	
	@Override
	public String toString() {
		return "SchemaCorrespondence [etype=" + etype + ", score=" + score
				+ ", attributeCorrespondences=" + attributeCorrespondences
				+ "]";
	}
	public EntityType getEtype() {
		return etype;
	}
	public void setEtype(EntityType etype) {
		this.etype = etype;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	public List<IAttributeCorrespondence> getAttributeCorrespondence() {
		return attributeCorrespondences;
	}
	public void setAttributeCorrespondence(
			List<IAttributeCorrespondence> attributeCorrespondence) {
		this.attributeCorrespondences = attributeCorrespondence;
	}
	
	
	
}
