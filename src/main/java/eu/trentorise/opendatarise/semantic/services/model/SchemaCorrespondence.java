package eu.trentorise.opendatarise.semantic.services.model;

import java.util.List;

import eu.trentorise.opendatarise.semantic.model.entity.EntityType;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 24 Feb 2014
 * 
 */
public class SchemaCorrespondence {

	EntityType etype;
	Float score;
	List<AttributeCorrespondence> attributeCorrespondences;
	
	
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
	public List<AttributeCorrespondence> getAttributeCorrespondence() {
		return attributeCorrespondences;
	}
	public void setAttributeCorrespondence(
			List<AttributeCorrespondence> attributeCorrespondence) {
		this.attributeCorrespondences = attributeCorrespondence;
	}
	
	
	
}
