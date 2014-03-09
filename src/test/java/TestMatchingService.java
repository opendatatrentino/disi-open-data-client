
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

	//@Test 
	public void testGetConceptDistance(){
		MatchingService mService = new MatchingService();
		float scoreDist = mService.getConceptsDistance(33292L,2L);
		System.out.println(scoreDist);
		assertEquals(3.0, scoreDist, 0.5);
	}

	//@Test 
	public void testAttributeMatching(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();

		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
			{
				add(new ColumnConceptCandidate(1, 51191L ));//name
				add(new ColumnConceptCandidate(2, 27517L ));//position
				add(new ColumnConceptCandidate(3, 42806L ));//class
			}
		};

		EntityType eType = (EntityType) etypeService.getEntityType(4L);//location
		List<IAttributeDef> attrs = eType.getAttributeDefs();
		List<IAttributeDef> testAttrs = new ArrayList<IAttributeDef>(); 
		for (int i=0; i<3; i++){
			testAttrs.add(attrs.get(i));
		}
		List<AttributeCorrespondence> attrCorr = mService.attributeMatching(testAttrs,cccList);
		//	System.out.println(attrCorr.get(0).getAttrMap().toString());
		for (AttributeCorrespondence attrc: attrCorr){
			System.out.println(attrc.getAttrMap().toString());
		}

		System.out.println(attrCorr.toString());

		assertNotNull(attrCorr.get(0));
	}

	@Test 
	public void testSchemaMatching(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();

		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
			{
				add(new ColumnConceptCandidate(1, 51191L ));//name
				add(new ColumnConceptCandidate(2, 27517L ));//position
				add(new ColumnConceptCandidate(3, 42806L ));//class
			}
		};

		EntityType eType = (EntityType) etypeService.getEntityType(4L);//location etype
		List<IAttributeDef> attrs = eType.getAttributeDefs();
		List<IAttributeDef> testAttrs1 = new ArrayList<IAttributeDef>(); 
		for (int i=0; i<3; i++){
			testAttrs1.add(attrs.get(i));

		}
		List<IAttributeDef> testAttrs2 = new ArrayList<IAttributeDef>(); 
		for (int i=3; i<6; i++){
			testAttrs2.add(attrs.get(i));

		}
		List<IAttributeDef> testAttrs3 = new ArrayList<IAttributeDef>(); 
		for (int i=6; i<9; i++){
			testAttrs3.add(attrs.get(i));

		}
		EntityType testEtype1 =  new EntityType();
		EntityType testEtype2 =  new EntityType();
		EntityType testEtype3 =  new EntityType();
		testEtype1.setAttrs(testAttrs1);
		testEtype2.setAttrs(testAttrs2);
		testEtype3.setAttrs(testAttrs3);

		List<IEntityType> etypeList = new ArrayList<IEntityType>(); 
		etypeList.add(testEtype1);
		etypeList.add(testEtype2);
		etypeList.add(testEtype3);

		SchemaCorrespondence scCorr = mService.schemaMatch(testEtype1,cccList);
		//System.out.println(scCorr.toString());
		System.out.println(scCorr.getScore());
		//		
		SchemaCorrespondence scCorr1 = mService.schemaMatch(testEtype2,cccList);
		//System.out.println(scCorr.toString());
		System.out.println(scCorr1.getScore());

		SchemaCorrespondence scCorr2 = mService.schemaMatch(testEtype3,cccList);
		//System.out.println(scCorr.toString());
		System.out.println(scCorr2.getScore());

		assertNotNull(scCorr.getScore());
		assertNotNull(scCorr.getAttributeCorrespondence());
		assertNotNull(scCorr.getEtype());



	}
}
