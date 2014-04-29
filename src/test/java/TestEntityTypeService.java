import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.IEtypeSearchResult;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 3 Mar 2014
 * 
 */
public class TestEntityTypeService {

	//@Test
	public void testGetEntityTypeByID(){
		EntityTypeService ets = new EntityTypeService();
		EntityType etype =(EntityType)ets.getEntityType(12L);
		List<IAttributeDef>atdefs=etype.getAttributeDefs();
		for (IAttributeDef ad:atdefs){
			System.out.println(ad.getName());
			System.out.println(ad.getDataType());
			System.out.println("AttributeDef Etype URL:"+ad.getEtypeURL());
		} 
		//		System.out.println("URL:"+etype.getURL());
		//		System.out.println(etype.getName1());
		//	assertEquals(etype.getName1().get("it") ,"Infrastruttura");
	}

	//@Test
	public void testGetAllEntityTypes(){
		EntityTypeService ets = new EntityTypeService();
		List<IEntityType> etypes= ets.getAllEntityTypes();
		for(IEntityType etype:etypes){
			List<IAttributeDef>atdefs=etype.getAttributeDefs();
			for (IAttributeDef ad:atdefs){
				System.out.println("AttributeDef Name:"+ad.getName().getString(Locale.ENGLISH));
				//System.out.println("AttributeDef URL:"+ad.getURL());
				System.out.println("AttributeDef Etype URL:"+ad.getEtypeURL());
			} 
		}
		assertNotNull(etypes.get(0));
	}

	//@Test
	public void testGetEntityTypeByURL(){
		EntityTypeService ets = new EntityTypeService();
		List<IEntityType> etypes= ets.getAllEntityTypes();
		for(IEntityType etype:etypes){
			
				//System.out.println(etype.getName());
				//System.out.println(etype.getURL());
				IEntityType et =ets.getEntityType(etype.getURL());
			//	System.out.println(et.getName());
				assertNotNull(et);
		}
	}

	
	@Test
	public void testFuzzySearchEtype(){
		EntityTypeService ets = new EntityTypeService();
		List<IEtypeSearchResult> searchEtypes = ets.searchEntityTypes("Lcalit");
		assertEquals("Location",searchEtypes.get(0).getName().getString(Locale.ENGLISH));

	}
	
	//@Test
	//	public void testAddAttributeDefToEtype(){
	//		EntityTypeService ets = new EntityTypeService();
	//		EntityType etypeEntity =(EntityType)ets.getEntityType(7L);
	//		EntityType etypeLoc =(EntityType)ets.getEntityType(4L);
	//		List<IAttributeDef> attrs = etypeLoc.getAttributeDefs();
	//
	//		String attrName= attrs.get(0).getName(new Locale("all"));
	//		System.out.println(attrName);
	//		ets.addAttributeDefToEtype(etypeEntity, attrs.get(0));
	//		EntityType etypeEntityUpdated =(EntityType)ets.getEntityType(7L);
	//		String addedAttrName =etypeEntityUpdated.getAttributeDefs().get(0).getName(Locale.ENGLISH);
	//		System.out.println(addedAttrName);
	//		assertEquals(attrName,addedAttrName);
	//
	//	}



}
