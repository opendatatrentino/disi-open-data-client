package eu.trentorise.opendata.disiclient.services.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;

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

		this.attrDef=(AttributeDef) maxEntry.getKey();
		this.score = maxEntry.getValue();
	}}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public void setColumnIndex(int columnNumber) {
		this.columnIndex = columnNumber;
		
	}

	public String getHeaderConceptURL() {
		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/concepts/"+this.headerConceptID;
		return url;
	}

}
