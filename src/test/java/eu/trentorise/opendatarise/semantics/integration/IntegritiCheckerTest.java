package eu.trentorise.opendatarise.semantics.integration;

import static org.junit.Assert.assertNotNull;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.semantics.IntegrityChecker;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendata.semantics.services.model.IIDResult;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.entity.Structure;
import eu.trentorise.opendatarise.semantics.model.facade.ImpiantoDiRisalitaFacade;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
import eu.trentorise.opendatarise.semantics.services.Ekb;
import eu.trentorise.opendatarise.semantics.services.EntityService;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import eu.trentorise.opendatarise.semantics.services.IdentityService;
import eu.trentorise.opendatarise.semantics.services.WebServiceURLs;
import eu.trentorise.opendatarise.semantics.services.model.IDRes;
import eu.trentorise.opendatarise.semantics.services.model.SchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.services.shematching.MatchingService;

public class IntegritiCheckerTest {
	String resourceName = "IMPIANTI RISALITA";
	private IntegrityChecker iChecker;

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


	/**Check the integration 
	 * 
	 */
	//@Test 
	public void testCheckSchemaCorrespondence(){
		MatchingService mService = new MatchingService();
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();

		List<ColumnConceptCandidate> odrHeaders =
				ColumnRecognizer.computeScoredCandidates(cols, bodies);
		for (IEntityType etype:etypeList){

			EntityType eType = (EntityType) etype;

			if (etype.getName().getString(Locale.ENGLISH).equals("Name")){
				//System.out.println(etype.getName().getString(Locale.ENGLISH));
			}
			long conid = 2923L;
			SchemaCorrespondence scCorr = (SchemaCorrespondence) mService.schemaMatch(eType,odrHeaders, conid);
			iChecker.checkSchemaCorrespondence(scCorr);
			iChecker.checkDict(etype.getName());
			iChecker.checkDict(etype.getConcept().getDescription());
			iChecker.checkDict(etype.getConcept().getName());
			assertNotNull(scCorr.getScore());
			assertNotNull(scCorr.getAttributeCorrespondence());
			assertNotNull(scCorr.getEtype());}
	}

	//@Test
	public void testCheckEtypesWithAttrDef(){
		EntityTypeService ets = new EntityTypeService();
		List<IEntityType> etypes= ets.getAllEntityTypes();
		for(IEntityType etype:etypes){
			iChecker.checkEntityType(etype);
			iChecker.checkURL(etype.getURL());
			List<IAttributeDef>atdefs=etype.getAttributeDefs();
			for (IAttributeDef ad:atdefs){
				iChecker.checkAttributeDef(ad);
				iChecker.checkDict(ad.getName());
				iChecker.checkDict(ad.getConcept().getDescription());
				iChecker.checkDict(ad.getConcept().getName());
				iChecker.checkURL(ad.getURL());
				iChecker.checkURL(ad.getEtypeURL());
				if (ad.getDataType().equals(DataTypes.STRUCTURE)){
					iChecker.checkURL(ad.getRangeEtypeURL());
				}
			} 
		}
		assertNotNull(etypes.get(0));
	}

	//@Test 
	public void testCheckEntity(){
		EntityService es= new EntityService(WebServiceURLs.getClientProtocol());
		IEntity entity = es.readEntity(15001L);
		iChecker.checkEntity(entity);
		List<IAttribute> attributes = entity.getStructureAttributes();

		for (IAttribute attr:attributes){
			iChecker.checkValue(attr.getFirstValue(), attr.getAttributeDefinition()); 
		}

	}

	@Test 
	public void testCheckIDResults(){
		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
		IdentityService idServ = new IdentityService();

		IEntity entity1 = entityForReuseResults();
		IEntity entity2 = entityForNewResults();
		IEntity entity3=entityForMissingResults();

		List<IEntity> entities = new ArrayList<IEntity>();
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);

		List<IIDResult> results=  idServ.assignGUID(entities);
		for (IIDResult res: results){
			System.out.println(res.getAssignmentResult().toString());
		
		
			iChecker.checkIDResult(res);
			//IEntity ent = res.getResultEntity();
			
			//System.out.println(ent.getURL());
			
		}
	}

	private IEntity entityForReuseResults(){
		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();
		for (Attribute atr : attrs){


			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
				attrs1.add(atr);
			}
			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				attrs1.add(atr);
			} else 
				if (atr.getName().get("en").equalsIgnoreCase("Class")){
					attrs1.add(atr);
				}
		}
		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		return ent;
	}

	private IEntity entityForNewResults(){
		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());

		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();
		for (Attribute atr : attrs){


			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
				AttributeDef ad = new AttributeDef(atr.getDefinitionId());
				AttributeODR attr = enServ.createAttribute(ad,12.123F);
				Attribute a=attr.convertToAttribute();
				attrs1.add(a);
				
			}
			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				attrs1.add(atr);
			} 
			else 
				if (atr.getName().get("en").equalsIgnoreCase("Class")){
					attrs1.add(atr);
				}
		}
		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		return ent;
	}

	private  IEntity entityForMissingResults(){

		EntityService enServ = new EntityService(WebServiceURLs.getClientProtocol());
		EntityODR entity = (EntityODR)enServ.readEntity(64000L);
		List<Attribute> attrs = entity.getAttributes();
		List<Attribute> attrs1 = new ArrayList<Attribute>();

		for (Attribute atr : attrs){


			if (atr.getName().get("en").equalsIgnoreCase("Latitude")){
				attrs1.add(atr);
			}
			else if (atr.getName().get("en").equalsIgnoreCase("Longitude")){
				attrs1.add(atr);
			} 
//			else 
//				if (atr.getName().get("en").equalsIgnoreCase("Class")){
//					attrs1.add(atr);
//				}
		}

		Entity en = new Entity();
		en.setEntityBaseId(1L);
		en.setTypeId(12L);
		en.setAttributes(attrs1);

		IEntity ent = new EntityODR(WebServiceURLs.getClientProtocol(),en);
		return ent;
	}

	private  Attribute createAttributeEntity(Object value){
		EntityService es = new EntityService(WebServiceURLs.getClientProtocol());
		EntityTypeService ets = new EntityTypeService();
		EntityType etype = ets.getEntityType(12L);

		List<IAttributeDef>attrDefList=etype.getAttributeDefs();
		List<Attribute> attrs = new ArrayList<Attribute>();

		Attribute a = null;
		for (IAttributeDef atd: attrDefList){
			if (atd.getName().getString(Locale.ENGLISH).equals("Foursquare ID")){
				AttributeODR attr = es.createAttribute(atd, (String)value);
				a = attr.convertToAttribute();
				attrs.add(a);
			}
		}
		return a;
	}


	//@Test
	public void testCheckConcepts(){

		ConceptODR concept = new ConceptODR();
		concept=concept.readConcept(1L);
		iChecker.checkConcept(concept);

	}

	//	@Test
	//	public void testCheckEKB(){
	//		IEkb ekb = new Ekb(); 
	//	iChecker.checkEkbQuick(ekb);
	//	}

}
