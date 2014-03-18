import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.services.EntityTypeService;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 3 Mar 2014
 * 
 */
public class TestEntityTypeService {

	//@Test
	public void testGetEntityTypeByID(){
		EntityTypeService ets = new EntityTypeService();
		EntityType etype =(EntityType)ets.getEntityType(1L);
		assertEquals(etype.getName(Locale.ENGLISH) ,"Position");
	}

	@Test
	public void testGetAllEntityTypes(){
		EntityTypeService ets = new EntityTypeService();
		List<IEntityType> etypes= ets.getAllEntityTypes();
		assertNotNull(etypes.get(0));
	}

	//@Test
	public void testAddAttributeDefToEtype(){
		EntityTypeService ets = new EntityTypeService();
		EntityType etypeEntity =(EntityType)ets.getEntityType(7L);
		EntityType etypeLoc =(EntityType)ets.getEntityType(4L);
		List<IAttributeDef> attrs = etypeLoc.getAttributeDefs();
		
		String attrName= attrs.get(0).getName(Locale.ENGLISH);
		System.out.println(attrName);
		ets.addAttributeDefToEtype(etypeEntity, attrs.get(0));
		EntityType etypeEntityUpdated =(EntityType)ets.getEntityType(7L);
		String addedAttrName =etypeEntityUpdated.getAttributeDefs().get(0).getName(Locale.ENGLISH);
		System.out.println(addedAttrName);
		assertEquals(attrName,addedAttrName);
		
	}

	

}
