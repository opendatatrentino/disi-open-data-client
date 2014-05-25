package eu.trentorise.opendatarise.semantics.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Structure;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IStructure;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.entity.ValueODR;

public class EntityService implements IEntityService {

	private IProtocolClient api;

	public EntityService(IProtocolClient api){

		this.api=api;
	}

	public Long createEntity(IEntity entity) {
		EntityODR ent = (EntityODR) entity;
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl= new  InstanceClient(this.api);
		System.out.println(e.toString());
		for (Attribute a : e.getAttributes()){
			System.out.println(a.getConceptId());
			System.out.println(a.getDataType());
			System.out.println(a.getDefinitionId());
			//	System.out.println(a.getValues().get(0).ge);
		}

		Long id = instanceCl.create(e);
		return id;
	}

	public Long createEntity(Name name) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Long id = instanceCl.create(name);
		return id;
	}

	public void updateEntity(Name name) {

		//EntityODR ent = (EntityODR) name;
		//Entity en=(Entity)ent;

		InstanceClient instanceCl= new  InstanceClient(this.api);
		//	Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
		//
		//		instance.setTypeId(ent.getEtype().getGUID());
		//		instance.setId(entity.getLocalID());
		//		List<IAttribute> attrs = entity.getStructureAttributes();
		//		List<Attribute> attributes = ent.convertToAttributes(attrs);
		//		instance.setAttributes(attributes);
		//Entity e = ent.convertToEntity();
		instanceCl.update(name);
	}

	public void deleteEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);
		Entity entity =  (Entity)instance; 
		EntityODR en = new EntityODR(this.api,entity);
		return en;
	}
	

	public void addAttribute(IEntity entity, IAttribute attribute) {
		EntityODR ent =(EntityODR) entity;
		ent.addAttribute(attribute);
	}

	public void addAttributeValue(IEntity entity, IAttribute attribute,
			IValue value) {
		AttributeODR atrODr = (AttributeODR) attribute;
		atrODr.addValue(value);
		IAttribute atr = atrODr;
		EntityODR ent =(EntityODR) entity;
		ent.addAttribute(atr);

	}


	public AttributeODR createAttribute(IAttributeDef attrDef, List<Object> values){

		List<ValueODR> vals= new ArrayList<ValueODR>(); 
		for (Object obj: values	){
			ValueODR value = new ValueODR();
			value.setValue(obj);
			vals.add(value);
		}
		AttributeODR attribute = new AttributeODR(attrDef, vals);
		return attribute;
	}
	
	

	public AttributeODR createAttribute(IAttributeDef attrDef, Object value){
		AttributeDef ad = (AttributeDef) attrDef;
		System.out.println(attrDef.getDataType());
		if (ad.getName(Locale.ENGLISH).equals("Name"))
		{
			return createNameAttributeODR(attrDef, (String)value);
		} else 
			if (attrDef.getDataType().equals("oe:structure")){
				return createStructureAttribute(attrDef, (HashMap<IAttributeDef, Object>) value);
			} else 

			{
				ValueODR val = new ValueODR();
				val.setValue(value);
				AttributeODR attribute = new AttributeODR(attrDef, val);
				return attribute;
			}
	}

	private AttributeODR createStructureAttribute(IAttributeDef attrDef,
			HashMap<IAttributeDef, Object> atributes) {
		List<Attribute> attrs = new ArrayList<Attribute>();
		Structure attributeStructure = new Structure();
		attributeStructure.setEntityBaseId(1L);

		AttributeDef adef =(AttributeDef)attrDef;
		attributeStructure.setTypeId(adef.getRangeEntityTypeID());

		Iterator it = atributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			AttributeODR aodr = createAttribute((IAttributeDef)pairs.getKey(), pairs.getValue());
			attrs.add(aodr.convertToAttribute());
			it.remove();
		}
		attributeStructure.setAttributes(attrs);


		Attribute nAtr =new Attribute();
		nAtr.setDefinitionId(attrDef.getGUID());
		List<Value>values=new ArrayList<Value>();
		values.add(new Value(attributeStructure)); 
		nAtr.setValues(values);

		AttributeODR a = new AttributeODR(api, nAtr);

		return a;
	}

	private AttributeODR createNameAttributeODR(IAttributeDef attrDef, String name){

		EntityTypeService ets = new EntityTypeService();
		//get Name Etype
		EntityType etpe =	ets.getEntityType(attrDef.getRangeEType().getURL());
		Name nameStructure = new Name();
		List<Attribute> entityNameattributes = new ArrayList<Attribute>();
		nameStructure.setEntityBaseId(1L);
		Attribute nameAttribute = new Attribute();

		List<IAttributeDef>atsd = etpe.getAttributeDefs();
		// here we take the only one attribute definition from Name etype 
		nameAttribute.setDefinitionId(atsd.get(0).getGUID());
		List<Value>nameValues=new ArrayList<Value>();
		//BE CAREFULL WITH VOCABULARY
		nameValues.add(new Value(name, 1L));
		nameAttribute.setValues(nameValues);
		//AttributeODR nameAttributeODR = new AttributeODR(api,nameAttribute);

		entityNameattributes.add(nameAttribute);
		nameStructure.setAttributes(entityNameattributes);
		AttributeODR atODR = new  AttributeODR();
		Attribute nAtr =new Attribute();
		nAtr.setDefinitionId(attrDef.getGUID());
		List<Value>values=new ArrayList<Value>();
		values.add(new Value(nameStructure)); 
		nAtr.setValues(values);
		AttributeODR a = new AttributeODR(api, nAtr);
		return a;
	}
	//
	//	private Attribute createNameAttribute(IAttributeDef attrDef, String name){
	//
	//		InstanceClient  ic = new InstanceClient(api);
	//		//Name nameStructure = new Name();
	//		//List<Attribute> nameAttributes = new ArrayList<Attribute>();
	//		//nameStructure.setEntityBaseId(1L);
	//		Attribute nameAttribute = new Attribute();
	//		nameAttribute.setDefinitionId(attrDef.getGUID());
	//		//	nameAttributes.add(nameAttribute);
	//		List<Value>nameValues=new ArrayList<Value>();
	//		nameValues.add(new Value(name, 1L));
	//		//BE CAREFULL WITH VOCABULARY
	//		nameAttribute.setValues(nameValues);
	//		return nameAttribute;
	//
	//	}


	public void updateAttributeValue(IEntity entity, IAttribute attribute,
			IValue newValue) {
		// TODO Auto-generated method stub

	}



	public void updateEntity(IEntity entity) {
		// TODO Auto-generated method stub

	}

	public void deleteEntity(String arg0) {
		// TODO Auto-generated method stub

	}

	public IEntity readEntity(String URL) {

		String s = URL.substring(URL.indexOf("es/") + 3);
		Long typeID = Long.parseLong(s);
		return readEntity(typeID);
	}

	public String createEntityURL(IEntity entity) {
		Long id = createEntity(entity);

		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/instances/"+id;
		return url;
	}

	public void exportToRdf(List<String> entityURLs, Writer writer) {
		// TODO Auto-generated method stub

	}

	public void exportToJsonLd(List<String> entityURLs, Writer writer) {
		String filename= "test"+System.currentTimeMillis();
		EntityExportService ees = new EntityExportService();
		List<Long> entitiesID = new ArrayList<Long>();
		
		for (String entityURL : entityURLs){
			String s = entityURL.substring(entityURL.indexOf("es/") + 3);
			Long eID = Long.parseLong(s);
			entitiesID.add(eID);
		}

		Long fileId = null;
		try {
			fileId = ees.methodPost(entitiesID,filename);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = ees.methodGet(fileId, "sem"+filename);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ees.convertToJsonLd(is,writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exportToCsv(List<String> entityURLs, Writer writer) {
		// TODO Auto-generated method stub

	}

}
