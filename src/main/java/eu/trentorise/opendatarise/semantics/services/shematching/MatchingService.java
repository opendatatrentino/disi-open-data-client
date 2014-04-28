package eu.trentorise.opendatarise.semantics.services.shematching;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import eu.trentorise.opendatarise.semantics.services.model.AttributeCorrespondence;
import eu.trentorise.opendatarise.semantics.services.model.SchemaCorrespondence;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 25 Feb 2014
 * 
 */

public class MatchingService implements ISemanticMatchingService {


	/** Methods run the process of matching. It gets ColumnConceptCandidate (1) and Etypes (many) as input.
	 *  @return the list of schema corespondences 
	 */


	public List<ISchemaCorrespondence> matchSchemas( IResourceContext resourceContext,ITableResource tableResource){

		List<ColumnConceptCandidate> odrHeaders =
				ColumnRecognizer.computeScoredCandidates(tableResource.getHeaders(), tableResource.getColumns());
		long odrName=ColumnRecognizer.conceptFromText(resourceContext.getResourceName());
		//long odrName=2923L;
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypes = etypeService.getAllEntityTypes();
		List<ISchemaCorrespondence> schemaCorespondences = new ArrayList<ISchemaCorrespondence>();
		for (IEntityType etype: etypes){
			EntityType  et = (EntityType) etype; 
			//System.out.println("etype:"+et.getName(Locale.ENGLISH));
			ISchemaCorrespondence sCorrespondence  =  schemaMatch(et, odrHeaders, odrName);
			if(sCorrespondence.getScore()!=0){
				schemaCorespondences.add( sCorrespondence);
			}
		}
		sort(schemaCorespondences);
		return schemaCorespondences;
	}

	/** Matches two schemas: one contains list of concept candidates of column headers
	 *  another that contains list of concepts for attributes  of a given etype
	 * @param eType entity types with list of attributes
	 * @param columnHeaders column headers from the table
	 * @return
	 */
	public ISchemaCorrespondence schemaMatch(EntityType eType, List<ColumnConceptCandidate> columnHeaders, long odrNameConcept){
		SchemaCorrespondence sCorrespondence = new SchemaCorrespondence();
		sCorrespondence.setEtype(eType);
		if (eType.getAttributeDefs().size()!=0){
			
			List<IAttributeCorrespondence> attrsCor = attributeMatching(eType.getAttributeDefs(),columnHeaders);
			sCorrespondence.setAttributeCorrespondence(attrsCor);
//			ConceptODR etypeConcept = (ConceptODR)eType.getConcept();
			
			float nameMatchScore= getConceptsDistance(odrNameConcept, eType.getConceptID());
			
			float sScore = computeSchemaCorrespondenceScore(sCorrespondence);
			sScore= (sScore+nameMatchScore)/(sCorrespondence.getAttributeCorrespondence().size()+1);
			sCorrespondence.setScore(sScore);
		} else {
			List<IAttributeCorrespondence> attrsCor = new ArrayList<IAttributeCorrespondence>();
			sCorrespondence.setAttributeCorrespondence(attrsCor);
			sCorrespondence.setScore((float) 0);
		}
		return (ISchemaCorrespondence) sCorrespondence;
	}

	/** Sorts the given list of correspondence according to the score of each shema correspondence.
	 * @param schemaCorespondences
	 */
	private void sort(List<ISchemaCorrespondence> schemaCorespondences) {
		Collections.sort(schemaCorespondences, new CustomComparator());

	}

	private float computeSchemaCorrespondenceScore(SchemaCorrespondence sCorrespondence){
		float scoreSum=0;
		List<IAttributeCorrespondence>  attrCorList = sCorrespondence.getAttributeCorrespondence();
		for(IAttributeCorrespondence atrrCor: attrCorList ){
			scoreSum=scoreSum+atrrCor.getScore();
		}
		return scoreSum;
	}

	/**
	 * @param eTypeAttributes
	 * @param columnHeaders
	 * @return
	 */
	public List<IAttributeCorrespondence> attributeMatching(List<IAttributeDef> eTypeAttributes, List<ColumnConceptCandidate> columnHeaders){
		List<IAttributeCorrespondence> attrCorrespondenceList = new ArrayList<IAttributeCorrespondence>();	

		for (ColumnConceptCandidate ccc: columnHeaders){

			ConceptODR codr = new ConceptODR();
			codr = codr.readConceptGlobalID(ccc.getConceptID());

			AttributeCorrespondence attrCor = new AttributeCorrespondence();
			HashMap<IAttributeDef, Float> attrMap = new HashMap<IAttributeDef, Float> (); 

			long sourceConceptID =codr.getId();
			attrCor.setColumnIndex(ccc.getColumnNumber()-1); 
			attrCor.setHeaderConceptID(sourceConceptID);
			List<Entry<Long,Long>> batch =new ArrayList<Entry<Long,Long>>(); 
			for(IAttributeDef attrDef:eTypeAttributes ){
				AttributeDef attr = (AttributeDef)attrDef;
				//ConceptODR attrConcept = (ConceptODR)attr.getConcept();
//				System.out.println(attr.getName(Locale.ENGLISH));
//				System.out.println(attr.getETypeURL());
				long targetConceptID = attr.getConceptId();
				Map.Entry<Long,Long> entry = new AbstractMap.SimpleEntry<Long,Long>(sourceConceptID,targetConceptID);
				batch.add(entry);
			}
			String st = batch.toString();
			List<Integer> distances = getBatchDistance(batch);
			for(int i=0; i<eTypeAttributes.size(); i++  ){
				AttributeDef attr = (AttributeDef)eTypeAttributes.get(i);
				float score = getScore(distances.get(i));
				attrMap.put(attr, score);
			}
			attrCor.setAttrMap(attrMap);
			attrCor.computeHighestAttrCorrespondence(attrCorrespondenceList);

			attrCorrespondenceList.add(attrCor);
		}

		return attrCorrespondenceList;
	}

	private List<Integer> getBatchDistance(List<Entry<Long, Long>> batch) {
		ConceptClient cClient = new ConceptClient(getClientProtocol());
		return cClient.getDistancesUsingLca(batch);
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

	/** Converts distance between two concepts into score 
	 * @param distance between two concepts
	 * @return score of the closeness between attribute concept and header concept 
	 */
	public float getScore( int distance){
		float score  = (float)distance;
		if (score==-1.0) return 0;
		if (score==0) return 1;
		else {
			return score = 1/(score+1);
		}}


	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}

}
