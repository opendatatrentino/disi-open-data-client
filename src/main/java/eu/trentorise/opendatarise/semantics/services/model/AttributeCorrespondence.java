package eu.trentorise.opendatarise.semantics.services.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 24 Feb 2014
 * 
 */
public class AttributeCorrespondence implements IAttributeCorrespondence {

	float score;
	HashMap<IAttributeDef,Float> attrMap;
	AttributeDef attrDef;
	long headerConceptID;
	int columnIndex;

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
	public HashMap<IAttributeDef, Float> getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(HashMap<IAttributeDef, Float> attrMap) {
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

	public void computeHighestAttrCorrespondence( List<IAttributeCorrespondence> attrCorrespondenceList  ){

		Map<IAttributeDef,Float> atributes = this.attrMap;
		
		System.out.println(atributes.size());
		if (atributes.size()==0){
			this.attrDef= null;
			this.score = 0;

		} else{
		Map.Entry<IAttributeDef, Float> maxEntry = null;

		for (Map.Entry<IAttributeDef,Float> entry:  atributes.entrySet() ){
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			{
				maxEntry = entry;
			}
		}
		System.out.println("MaxEntry:"+maxEntry.getKey());
	//	System.out.println(maxEntry.getValue());

		this.attrDef=(AttributeDef) maxEntry.getKey();
		this.score = maxEntry.getValue();
	}}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public void setColumnIndex(int columnNumber) {
		this.columnIndex = columnNumber;
		
	}

}
