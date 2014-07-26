package eu.trentorise.opendatarise.semantics.services;

import eu.trentorise.opendatarise.semantics.DisiClientException;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantics.model.entity.AttributeODR;
import eu.trentorise.opendatarise.semantics.model.entity.EntityType;
import eu.trentorise.opendatarise.semantics.model.entity.Structure;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
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

import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.entity.ValueODR;

public class EntityService implements IEntityService {

	private IProtocolClient api;

	public EntityService(IProtocolClient api){

		this.api=api;
	}

	public EntityService(){
		if(this.api==null){
			api=WebServiceURLs.getClientProtocol();
		}
	}

	public Long createEntity(IEntity entity) {
		EntityODR ent = (EntityODR) entity;
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl= new  InstanceClient(this.api);
		//System.out.println(e.toString());
		//for (Attribute a : e.getAttributes()){

		//			System.out.println("Concept:"+a.getConceptId());
		//			System.out.println("DataType:"+a.getDataType());
		//			System.out.println("Definition:"+a.getDefinitionId());
		//	System.out.println(a.getValues().get(0).ge);
		//	}
		Long id = null;
		try{
			id = instanceCl.create(e);

		} catch (NotFoundException ex){
		}
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

	public void deleteEntity(String entityURL) {
		InstanceClient instanceCl= new  InstanceClient(this.api);
		Long entityID = getEntityIdFromURL(entityURL);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);
		Entity entity =  (Entity)instance; 
		EntityODR en = new EntityODR(this.api,entity);
		return en;
	}
        
	public List<IEntity> readEntities(List<String> entityURLs) {
                List<Long> entityIDs = new ArrayList();
            
                for (String entityURL : entityURLs){
                    entityIDs.add(SemanticTextFactory.entitypediaURLToEntityID(entityURL));
                }
                
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);
                
		List instances = instanceCl.readInstancesById(entityIDs, instFilter);
		List<Entity> entities =  (List<Entity>)instances; 
                List<IEntity> ret = new ArrayList();
                for (Entity epEnt : entities){
                    ret.add(new EntityODR(this.api,epEnt));
                }
		return ret;
	}        
        
        
	public Structure readName(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);

		Name name =  (Name)instance; 
		Structure structureName = new Structure();
		structureName.setAttributes(name.getAttributes());
		//EntityODR en = new EntityODR(this.api,entity);

		return structureName;
	}

	public Structure readStructure(long entityID) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);

		it.unitn.disi.sweb.webapi.model.eb.Structure name =  (it.unitn.disi.sweb.webapi.model.eb.Structure)instance; 
		Structure structureName = new Structure();
		structureName.setAttributes(name.getAttributes());
		//EntityODR en = new EntityODR(this.api,entity);

		return structureName;
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
		//	System.out.println(attrDef.getDataType());
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

	public AttributeODR createStructureAttribute(IAttributeDef attrDef,
			HashMap<IAttributeDef, Object> atributes) {
		List<Attribute> attrs = new ArrayList<Attribute>();
		it.unitn.disi.sweb.webapi.model.eb.Structure  attributeStructure = new it.unitn.disi.sweb.webapi.model.eb.Structure();
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

	private AttributeODR createNameAttributeODR1(IAttributeDef attrDef, String name){

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
		nameAttribute.setConceptId(atsd.get(0).getConcept().getGUID());
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


	/** Creates Attribute from Name.class 
	 * @param name
	 */
	public AttributeODR createNameAttribute(IAttributeDef attrDef, Name name){

		AttributeODR atODR = new  AttributeODR();
		Attribute nAtr =new Attribute();
		nAtr.setDefinitionId(attrDef.getGUID());
		List<Value>values=new ArrayList<Value>();
		values.add(new Value(name)); 
		nAtr.setValues(values);
		AttributeODR a = new AttributeODR(api, nAtr);
		return a;

	}

	public AttributeODR createNameAttributeODR(IAttributeDef attrDef, String name){

		Attribute entityNameAttribute = new Attribute();
		entityNameAttribute.setDefinitionId(attrDef.getGUID());

		Name nameStructure = new Name();
		nameStructure.setEntityBaseId(1L);
		nameStructure.setTypeId(10L); //NOTE HARCODED TODO change

		List<Attribute> nameAttributes = new ArrayList<Attribute>();

		Attribute nameAttribute = new Attribute();
		nameAttribute.setDefinitionId(55L); //NOTE HARCODED TODO change
		nameAttribute.setConceptId(2L);

		List<Value>nameValues=new ArrayList<Value>();

		nameValues.add(new Value(name, 1L));
		nameAttribute.setValues(nameValues);
		nameAttributes.add(nameAttribute);
		nameStructure.setAttributes(nameAttributes);

		List<Value>entityNameValues=new ArrayList<Value>();

		entityNameValues.add(new Value(nameStructure)); // here is your link to the name structure, if you want you can put the id of the name instance (if you created it before) but make sure the data type is COMPLEX_TYPE
		entityNameAttribute.setValues(entityNameValues);
		AttributeODR a = new AttributeODR(api, entityNameAttribute);
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
		AttributeODR attr = (AttributeODR) attribute;
		attr.updateValue(newValue);

	}



	public void updateEntity(IEntity entity) {

		EntityODR ent = (EntityODR) entity;
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl= new  InstanceClient(this.api);
		try {
			instanceCl.update(e);
		} catch (IllegalArgumentException ex){
			throw new NotFoundException("Such an entity does not exists.");
		}	
	}


	public IEntity readEntity(String URL) {

		String s;
		try {
			s = URL.substring(URL.indexOf("es/") + 3);
		} catch (Exception e) {
			return null;
		}

		Long typeID;
		try {
			typeID = Long.parseLong(s);
		} catch (Exception e) {
			return null;				
                }

		return readEntity(typeID);
	}

	public Long getEntityIdFromURL(String URL) {

		String s = URL.substring(URL.indexOf("es/") + 3);
		Long typeID = Long.parseLong(s);
		return typeID;
	}

	public String createEntityURL(IEntity entity) {
		Long id = createEntity(entity);

		String fullUrl = WebServiceURLs.getURL();
		String url  = fullUrl+"/instances/"+id;
		return url;
	}

	public void exportToRdf(List<String> entityURLs, Writer writer) {
		throw new UnsupportedOperationException("todo to implement");

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
			throw new DisiClientException("Error while getting fileId", e);
		} catch (IOException e) {
			throw new DisiClientException("Error while getting fileId", e);
		}
		InputStream is = null;
		try {
			is = ees.methodGet(fileId, "sem"+filename);
		} catch (ClientProtocolException e) {
			throw new DisiClientException("Error while getting input stream", e);
		} catch (IOException e) {
			throw new DisiClientException("Error while getting input stream", e);
		}
		try {
			ees.convertToJsonLd(is,writer);
		} catch (IOException e) {
			throw new DisiClientException("Error while creating jsonLd", e);
		}

	}

	public void exportToCsv(List<String> entityURLs, Writer writer) {
		throw new UnsupportedOperationException("todo to implement");

	}

	public EntityODR readEntityByGUID(Long guid) {
		InstanceClient instanceCl= new  InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);
		Entity entity= instanceCl.readEntityByGloabalId(1L, guid, instFilter) ;
		//Entity entity =  (Entity)instance; 
		EntityODR en = new EntityODR(this.api,entity);
		return en;
	}

}
