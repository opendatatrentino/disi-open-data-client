
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantic.model.entity.EntityType;
import eu.trentorise.opendatarise.semantic.services.EntityTypeService;
import eu.trentorise.opendatarise.semantic.services.model.AttributeCorrespondence;
import eu.trentorise.opendatarise.semantic.services.model.SchemaCorrespondence;
import eu.trentorise.opendatarise.semantic.services.shematching.MatchingService;


public class TestMatchingService {

	@Test 
	public void testGetConceptDistance(){
		MatchingService mService = new MatchingService();
		float scoreDist = mService.getConceptsDistance(33292L,2L);
		System.out.println(scoreDist);
		assertEquals(0.5,scoreDist,0.1);
	}

	@Test 
	public void testAttributeMatching(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();
		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
			{
				add(new ColumnConceptCandidate(1, 51191L ));//name
				add(new ColumnConceptCandidate(2, 31361L ));//typeEN (class)
				add(new ColumnConceptCandidate(3, 34210L ));//orari
				add(new ColumnConceptCandidate(4, 45422L ));//latitude
				add(new ColumnConceptCandidate(5, 45427L ));//longitude
			}
		};
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		List<AttributeCorrespondence> attrCorr = null;
		for(IEntityType  eType:etypeList){
			System.out.println(eType.toString());
			//EntityType eType = (EntityType) etypeService.getEntityType(4L);//location
			List<IAttributeDef> attrs = eType.getAttributeDefs();
			//		List<IAttributeDef> testAttrs = new ArrayList<IAttributeDef>(); 
			//		for (int i=0; i<testAttrs.size(); i++){
			//			testAttrs.add(attrs.get(i));
			//		}
			 attrCorr = mService.attributeMatching(attrs,cccList);
			//	System.out.println(attrCorr.get(0).getAttrMap().toString());
			for (AttributeCorrespondence attrc: attrCorr){
				System.out.println(attrc.getAttrMap().toString());
			}
			if(attrs.size()!=0){
			System.out.println(attrCorr.toString());
			}
		}
		assertNotNull(attrCorr.get(0));
	}

	@Test 
	public void testSchemaMatching(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		
		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
			{
				add(new ColumnConceptCandidate(1, 51191L ));//name
				add(new ColumnConceptCandidate(2, 31361L ));//typeEN (class)
				add(new ColumnConceptCandidate(3, 34210L ));//orari
				add(new ColumnConceptCandidate(4, 45422L ));//latitude
				add(new ColumnConceptCandidate(5, 45427L ));//longitude
			}
		};

		//EntityType eType = (EntityType) etypeService.getEntityType(4L);//location etype
	for (IEntityType etype:etypeList){
		
		EntityType eType = (EntityType) etype;
		
		List<IAttributeDef> attrs = eType.getAttributeDefs();
		SchemaCorrespondence scCorr = mService.schemaMatch(eType,cccList);
		//System.out.println(scCorr.toString());
		System.out.println(scCorr.getScore());
		
		

		assertNotNull(scCorr.getScore());
		assertNotNull(scCorr.getAttributeCorrespondence());
		assertNotNull(scCorr.getEtype());
	}
	}
}
