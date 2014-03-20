
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.model.SchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.services.shematching.MatchingService;

public class TestMatchingService {

	String resourceName = "IMPIANTI RISALITA";
	
	String col1 = "nr";
	String col2 = "Comune";
	String col3 = "Insegna";
	String col4 = "Tipo";
	String col5 = "Frazione";
	String col6 = "Indirizio";
	String col7 = "Civico";
	
	List<String> cols = new ArrayList<String>(){
		{add("nr");
		add("Comune");
		add("Insegna");
		add("Tipo");
		add("Frazione");
		add("Indirizio");
		add("Civico");
		}
	};

	List<List<String>> bodies = new ArrayList<List<String>>(){
		{add(new ArrayList<String>(){
			{add("1");
			add("2");
			add("3");}
		});
		add(new ArrayList<String>(){
			{add("ANDALO");
			add("ARCO");
			add("BASELGA DI PINE");}
		});
		add(new ArrayList<String>(){
			{add("AL FAGGIO");
			add("OSTERIA IL RITRATTO");
			add("AI DUE CAMI");}
		});
		add(new ArrayList<String>(){
			{add("Ristorante");
			add("Ristorante-Bar");
			add("Albergo-Ristorante-Bar");}
		});
		add(new ArrayList<String>(){
			{add("ANDALO");
			add("ARCO");
			add("BASELGA DI PINE");}
		});
		add(new ArrayList<String>(){
			{add("Via Fovo");
			add("Via Ferrera");
			add("Via Pontara");}
		});
		add(new ArrayList<String>(){
			{add("11");
			add("30");
			add("352");}
		});
		}
	};


	@Test 
	public void testMatchingService(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();

		List<ColumnConceptCandidate> odrHeaders =
				ColumnRecognizer.computeScoredCandidates(cols, bodies);
		System.out.println(odrHeaders.get(1).toString());
		for (IEntityType etype:etypeList){

			EntityType eType = (EntityType) etype;

			//List<IAttributeDef> attrs = eType.getAttributeDefs();
			long conid = 2923L;
			SchemaCorrespondence scCorr = (SchemaCorrespondence) mService.schemaMatch(eType,odrHeaders, conid);
			
			System.out.println(scCorr.getScore());
			assertNotNull(scCorr.getScore());
		//	assertNotNull(scCorr.getAttributeCorrespondence());
			assertNotNull(scCorr.getEtype());}
		
	}

	//@Test 
	public void testGetConceptDistance(){
		MatchingService mService = new MatchingService();
		float scoreDist = mService.getConceptsDistance(33292L,2L);
		System.out.println(scoreDist);
		assertEquals(0,scoreDist,0.1);
	}
//
//	@Test 
//	public void testAttributeMatching(){
//		MatchingService mService = new MatchingService();
//		EntityTypeService etypeService = new EntityTypeService();
//		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
//			{
//				add(new ColumnConceptCandidate(1, 51191L ));//name
//				add(new ColumnConceptCandidate(2, 31361L ));//typeEN (class)
//				add(new ColumnConceptCandidate(3, 34210L ));//orari
//				add(new ColumnConceptCandidate(4, 45422L ));//latitude
//				add(new ColumnConceptCandidate(5, 45427L ));//longitude
//			}
//		};
//		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
//		List<AttributeCorrespondence> attrCorr = null;
//		for(IEntityType  eType:etypeList){
//			//	System.out.println(eType.toString());
//			//EntityType eType = (EntityType) etypeService.getEntityType(4L);//location
//			List<IAttributeDef> attrs = eType.getAttributeDefs();
//			//		List<IAttributeDef> testAttrs = new ArrayList<IAttributeDef>(); 
//			//		for (int i=0; i<testAttrs.size(); i++){
//			//			testAttrs.add(attrs.get(i));
//			//		}
//			attrCorr = mService.attributeMatching(attrs,cccList);
//			//	System.out.println(attrCorr.get(0).getAttrMap().toString());
//			//			for (AttributeCorrespondence attrc: attrCorr){
//			//				System.out.println(attrc.getAttrMap().toString());
//			//			}
//			//			if(attrs.size()!=0){
//			//			System.out.println(attrCorr.toString());
//			//			}
//		}
//		assertNotNull(attrCorr.get(0));
//	}
//
//	@Test 
//	public void testSchemaMatching(){
//		MatchingService mService = new MatchingService();
//		EntityTypeService etypeService = new EntityTypeService();
//		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
//
//		List<ColumnConceptCandidate> cccList = new ArrayList<ColumnConceptCandidate>(){
//			{
//				add(new ColumnConceptCandidate(1, 51191L ));//name
//				add(new ColumnConceptCandidate(2, 31361L ));//typeEN (class)
//				add(new ColumnConceptCandidate(3, 34210L ));//orari
//				add(new ColumnConceptCandidate(4, 45422L ));//latitude
//				add(new ColumnConceptCandidate(5, 45427L ));//longitude
//			}
//		};
//
//		//EntityType eType = (EntityType) etypeService.getEntityType(4L);//location etype
//		for (IEntityType etype:etypeList){
//
//			EntityType eType = (EntityType) etype;
//
//			List<IAttributeDef> attrs = eType.getAttributeDefs();
//			SchemaCorrespondence scCorr = mService.schemaMatch(eType,cccList);
//			//System.out.println(scCorr.toString());
//			System.out.println(scCorr.getScore());
//
//
//
//			assertNotNull(scCorr.getScore());
//			assertNotNull(scCorr.getAttributeCorrespondence());
//			assertNotNull(scCorr.getEtype());
//		}
//
//
//	}
}
