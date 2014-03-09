package eu.trentorise.opendatarise.semantic.services.model;

import java.util.HashMap;
import java.util.Map;

import eu.trentorise.opendatarise.semantic.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 24 Feb 2014
 * 
 */
public class AttributeCorrespondence {

	float score;
	HashMap<AttributeDef,Float> attrMap;
	AttributeDef attrDef;
	long headerConceptID;
	
	public float getScore() {
		return score;
	}
	
	@Override
	public String toString() {
		return "AttributeCorrespondence [score=" + score + ", attrDef="
				+ attrDef.getId() + ", headerConceptID=" + headerConceptID + "]";
	}
	
	



	public void setScore(float score) {
		this.score = score;
	}
	public HashMap<AttributeDef, Float> getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(HashMap<AttributeDef, Float> attrMap) {
		this.attrMap = attrMap;
	}
	public AttributeDef getAttrDef() {
		return attrDef;
	}
	public void setAttrDef(AttributeDef attrDef) {
		this.attrDef = attrDef;
	}
	public long getHeaderConceptID() {
		return headerConceptID;
	}
	public void setHeaderConceptID(long headerConceptID) {
		this.headerConceptID = headerConceptID;
	}

	public void computeHighestAttrCorrespondence(){
		Map<AttributeDef,Float> atributes = this.attrMap;
		Map.Entry<AttributeDef, Float> maxEntry = null;

		for (Map.Entry<AttributeDef,Float> entry:  atributes.entrySet() ){
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		        maxEntry = entry;
		    }
		}
		this.attrDef=maxEntry.getKey();
		this.score = maxEntry.getValue();
	}
}
