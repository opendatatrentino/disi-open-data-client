package eu.trentorise.opendatarise.semantics.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
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
	//	System.out.println(odrHeaders.get(1).toString());
		for (IEntityType etype:etypeList){

			EntityType eType = (EntityType) etype;

			//List<IAttributeDef> attrs = eType.getAttributeDefs();
			long conid = 2923L;
			SchemaCorrespondence scCorr = (SchemaCorrespondence) mService.schemaMatch(eType,odrHeaders, conid);
			
			//System.out.println(scCorr.getScore());
			assertNotNull(scCorr.getScore());
		//	assertNotNull(scCorr.getAttributeCorrespondence());
			assertNotNull(scCorr.getEtype());}
		
	}

	@Test 
	public void testGetConceptDistance(){
		MatchingService mService = new MatchingService();
		float scoreDist = mService.getConceptsDistance(33292L,2L);
	//	System.out.println(scoreDist);
		assertEquals(0,scoreDist,0.1);
	}

}
