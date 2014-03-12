package eu.trentorise.opendatarise.semantic.services.shematching;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import eu.trentorise.opendata.semantics.services.model.ICorrespondence;
import eu.trentorise.opendatarise.semantic.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantic.model.entity.EntityType;
import eu.trentorise.opendatarise.semantic.services.EntityTypeService;
import eu.trentorise.opendatarise.semantic.services.model.AttributeCorrespondence;
import eu.trentorise.opendatarise.semantic.services.model.SchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 25 Feb 2014
 * 
 */

public class MatchingService implements ISemanticMatchingService {


	/** Methods run the process of matching. It gets ColumnConceptCandidate (1) and Etypes (many) as input.
	 *  @return the list of schema corespondences 
	 */


	public List<SchemaCorrespondence> matchSchemas(){

		ITableResource tableResource= new TableResource();
		//TODO fix it with David 
		List<ColumnConceptCandidate> odrHeaders =
				ColumnRecognizer.computeScoredCandidates(tableResource.getHeaders(), tableResource.getColumns());

		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypes = etypeService.getAllEntityTypes();
		List<SchemaCorrespondence> schemaCorespondences = new ArrayList<SchemaCorrespondence>();
		for (IEntityType etype: etypes){
			EntityType  et = (EntityType) etype; 
			SchemaCorrespondence sCorrespondence  = schemaMatch(et, odrHeaders);
			schemaCorespondences.add(sCorrespondence);
		}
		sort(schemaCorespondences);
		return schemaCorespondences;
	}

	/** Matches two shemas: one contains list of concept candidates of column headers
	 *  another that contains list of concepts for attributes  of a given etype
	 * @param eType entity types with list of attributes
	 * @param columnHeaders column headers from the table
	 * @return
	 */
	public SchemaCorrespondence schemaMatch(EntityType eType, List<ColumnConceptCandidate> columnHeaders){
		SchemaCorrespondence sCorrespondence = new SchemaCorrespondence();
		sCorrespondence.setEtype(eType);
		List<AttributeCorrespondence> attrsCor = attributeMatching(eType.getAttributeDefs(),columnHeaders);
		sCorrespondence.setAttributeCorrespondence(attrsCor);
		float sScore = computeSchemaCorrespondenceScore(sCorrespondence);
		sCorrespondence.setScore(sScore);

		return sCorrespondence;
	}



	/** Sorts the given list of correspondence according to the score of each shema correspondence.
	 * @param schemaCorespondences
	 */
	private void sort(List<SchemaCorrespondence> schemaCorespondences) {
		Collections.sort(schemaCorespondences, new CustomComparator());

	}



	private float computeSchemaCorrespondenceScore(SchemaCorrespondence sCorrespondence){
		float scoreSum=0;
		float scScore=0;
		List<AttributeCorrespondence>  attrCorList = sCorrespondence.getAttributeCorrespondence();
		for(AttributeCorrespondence atrrCor: attrCorList ){
			scoreSum=+atrrCor.getScore();
		}
		scScore = scoreSum/attrCorList.size();

		return scScore;
	}

	/**
	 * @param eTypeAttributes
	 * @param columnHeaders
	 * @return
	 */
	public List<AttributeCorrespondence> attributeMatching(List<IAttributeDef> eTypeAttributes, List<ColumnConceptCandidate> columnHeaders){
		List<AttributeCorrespondence> attrCorrespondenceList = new ArrayList<AttributeCorrespondence>();		
		IProtocolClient api = getClientProtocol();
		for (ColumnConceptCandidate ccc: columnHeaders){
			AttributeCorrespondence attrCor = new AttributeCorrespondence();
			HashMap<AttributeDef, Float> attrMap = new HashMap<AttributeDef, Float> (); 
			long sourceConceptID = ccc.getConceptID();
			attrCor.setHeaderConceptID(sourceConceptID);
			for(IAttributeDef attrDef:eTypeAttributes ){
				AttributeDef attr = (AttributeDef)attrDef;
				ConceptODR attrConcept = (ConceptODR)attr.getConcept();
				long targetConceptID = attrConcept.getId();
				float attrMatchScore = getConceptsDistance(sourceConceptID,targetConceptID);
				attrMap.put(attr, attrMatchScore);
			}
			
			attrCor.setAttrMap(attrMap);
		
			attrCor.computeHighestAttrCorrespondence();
			attrCorrespondenceList.add(attrCor);
		}

		return attrCorrespondenceList;
	}

	/** Returns the distance between two concept. The method uses LCA approach. 
	 * @param source source concept
	 * @param target target concept
	 * @return
	 */
	public float getConceptsDistance( long source, long target){
		ConceptClient cClient = new ConceptClient(getClientProtocol());
		float score  = (float)cClient.getDistanceUsingLca(source,target);
		if (score==-1) return 0;
		if ((score-1)!=0){
			return score = 1/(score-1);
		}
		else return 0;
	}

	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}

	public List<ICorrespondence> matchSchemas(
			List<IConcept> sourceHeaderConcepts, List<String> sourceTypes) {
		// TODO Auto-generated method stub
		return null;
	}

}
