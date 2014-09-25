package eu.trentorise.opendata.disiclient.services;

import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.model.entity.AttributeODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityODR;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.entity.StructureODR;
import eu.trentorise.opendata.disiclient.model.entity.ValueODR;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.semantics.NotFoundException;
import eu.trentorise.opendata.semantics.model.entity.IAttribute;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntity;
import eu.trentorise.opendata.semantics.model.entity.IValue;
import eu.trentorise.opendata.semantics.model.knowledge.IDict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.Dict;
import eu.trentorise.opendata.semantics.model.knowledge.impl.SemanticText;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.model.DataTypes;
import eu.trentorise.opendata.semantics.services.model.ISearchResult;
import eu.trentorise.opendata.traceprov.impl.TraceProvUtils;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.VocabularyClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Value;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;
import it.unitn.disi.sweb.webapi.model.kb.vocabulary.Vocabulary;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityService implements IEntityService {

	Logger logger = LoggerFactory.getLogger(EntityService.class);

	private IProtocolClient api;

	public EntityService(IProtocolClient api) {

		this.api = api;
	}

	public EntityService() {
		if (this.api == null) {
			api = WebServiceURLs.getClientProtocol();
		}
	}

	public Long createEntity(IEntity entity) {
                
                EntityODR ent;
                if (entity instanceof EntityODR){
                    ent = (EntityODR) entity;
                } else {
                    ent = EntityODR.disify(entity, true);   
                }
		 
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl = new InstanceClient(this.api);
		logger.info(e.toString());
		EntityTypeService es = new EntityTypeService();
		EntityType etype= es.getEntityType(e.getTypeId());

		List<IAttributeDef> attrDefs=etype.getAttributeDefs();
		Long attrDefClassAtrID = null;
		for(IAttributeDef adef: attrDefs){

			if (adef.getName().getString(Locale.ENGLISH).equalsIgnoreCase("class")){
				attrDefClassAtrID=adef.getGUID();
				break;
			}
		}

		boolean isExistAttrClass=false;

		for (Attribute a : e.getAttributes()){

			if (a.getDefinitionId()==attrDefClassAtrID){
				isExistAttrClass=true;
				break;
			}
		}

		if (!isExistAttrClass){
			Attribute a =createClassAttribute(attrDefClassAtrID, etype.getConceptID());
			e.getAttributes().add(a);
			logger.warn("Default class attribute is assigned");
		}

		//System.out.println("Class exists: truw");


		Long id = null;
		try {
			id = instanceCl.create(e);
		} catch (NotFoundException ex) {
		}
		return id;

	}

	private Attribute createClassAttribute(Long attrDefClassAtrID, Long conceptID) {
		IAttributeDef atrDef =new AttributeDef(attrDefClassAtrID);
		ConceptODR concept = new ConceptODR();
		concept = concept.readConcept(conceptID);
		AttributeODR atrODR= createAttribute(atrDef, concept);
		Attribute a=atrODR.convertToAttribute();
		return a;

	}

	public Long createEntity(Name name) {
		InstanceClient instanceCl = new InstanceClient(this.api);
		Long id = instanceCl.create(name);
		return id;
	}

	public void updateEntity(Name name) {

		//EntityODR ent = (EntityODR) name;
		//Entity en=(Entity)ent;
		InstanceClient instanceCl = new InstanceClient(this.api);
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
		InstanceClient instanceCl = new InstanceClient(this.api);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public void deleteEntity(String entityURL) {
		InstanceClient instanceCl = new InstanceClient(this.api);
		Long entityID = WebServiceURLs.urlToEntityID(entityURL);
		Instance instance = instanceCl.readInstance(entityID, null);
		instanceCl.delete(instance);
	}

	public IEntity readEntity(long entityID) {
		InstanceClient instanceCl = new InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);
		Instance instance = instanceCl.readInstance(entityID, instFilter);
		Entity entity = (Entity) instance;
		EntityODR en = new EntityODR(this.api, entity);
		return en;
	}

	public List<IEntity> readEntities(List<String> entityURLs) {

		if (entityURLs.size() == 0) {
			return new ArrayList<IEntity>();
		}

		List<Long> entityIDs = new ArrayList<Long>();

		for (String entityURL : entityURLs) {                    
                    entityIDs.add(WebServiceURLs.urlToEntityID(entityURL));
                }		
        
		InstanceClient instanceCl = new InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);

		List instances = instanceCl.readInstancesById(entityIDs, instFilter);
		List<Entity> entities = (List<Entity>) instances;
		List<IEntity> ret = new ArrayList<IEntity>();
		for (Entity epEnt : entities) {
			ret.add(new EntityODR(this.api, epEnt));
		}
		return ret;
	}

	public StructureODR readName(long entityID) {
		InstanceClient instanceCl = new InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);

		Instance instance = instanceCl.readInstance(entityID, instFilter);

		Name name = (Name) instance;
		StructureODR structureName = new StructureODR();
		List<Attribute> atrs = name.getAttributes();

		for (Attribute a: atrs){

			if(a.getDataType()==DataType.CONCEPT)
			{
				List<Value> vals = a.getValues();
				List<Value> fixedVals = new ArrayList<Value>();

				for (Value val : vals) {

					if (val.getValue().getClass().equals(ConceptODR.class)) {
						fixedVals.add(val);
						continue;
					}
					Concept c = (Concept) val.getValue();
					ConceptODR codr = new ConceptODR(c);

					ValueODR fixedVal = new ValueODR();
					fixedVal.setId(val.getId());
					// fixedVal.setDataType(IConcept.class);
					fixedVal.setValue(codr);
					fixedVals.add(fixedVal);
				}
				a.setValues(fixedVals);		
			}
		}


		structureName.setAttributes(name.getAttributes());
		structureName.setId(name.getId());
		structureName.setTypeId(name.getTypeId());
		structureName.setEntityBaseId(name.getEntityBaseId());

		return structureName;
	}

	public StructureODR readStructure(long entityID) {
		InstanceClient instanceCl = new InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);

		Instance instance = instanceCl.readInstance(entityID, instFilter);

		it.unitn.disi.sweb.webapi.model.eb.Structure structure = (it.unitn.disi.sweb.webapi.model.eb.Structure) instance;
		StructureODR structureName = new StructureODR();
		structureName.setAttributes(structure.getAttributes());
		structureName.setTypeId(structure.getTypeId());
		structureName.setEntityBaseId(1L);
		structureName.setId(structure.getId());
		//EntityODR en = new EntityODR(this.api,entity);

		return structureName;
	}

	public void addAttribute(IEntity entity, IAttribute attribute) {
		EntityODR ent = (EntityODR) entity;
		ent.addAttribute(attribute);
	}

	public void addAttributeValue(IEntity entity, IAttribute attribute,
			IValue value) {
		AttributeODR atrODr = (AttributeODR) attribute;
		atrODr.addValue(value);
		IAttribute atr = atrODr;
		EntityODR ent = (EntityODR) entity;
		ent.addAttribute(atr);

	}

        /**
         * @param value Note: can be a Collection
         */
	public AttributeODR createAttribute(IAttributeDef attrDef, Object value) {                                    
		AttributeDef ad = (AttributeDef) attrDef;
                                
                
		if (ad.getName(Locale.ENGLISH).equals("Name")) {
			return createNameAttributeODR(attrDef,  value);

		} else
			if (ad.getName(Locale.ENGLISH).equals("Description")) {
				return createDescriptionAttributeODR(attrDef,  value);

			} else if (attrDef.getDataType().equals(DataTypes.STRUCTURE)) {
                                if (value instanceof Collection){ // notice in Java a Map is *NOT* an instance of Collection
                                    return createStructureAttribute(attrDef, (Collection) value);
                                } else {
                                    List<HashMap<IAttributeDef, Object>> hashMaps = new ArrayList();
                                    hashMaps.add((HashMap<IAttributeDef, Object>) value);
                                    return createStructureAttribute(attrDef, hashMaps);
                                }
				
			} 
		//			else if (ad.getName(Locale.ENGLISH).equals("Part copyOf")){
		//				return createRelationalAttribute(attrDef,  value);
		//			}

			else {
                            if (value instanceof Collection){
                                List<ValueODR> valsODR = new ArrayList<ValueODR>();
                                for (Object obj : (Collection) value) {
                                        ValueODR valODR = new ValueODR();
                                        valODR.setValue(obj);
                                        valsODR.add(valODR);
                                }
                                return new AttributeODR(attrDef, valsODR);   
                            } else {
				ValueODR val = new ValueODR();
				val.setValue(value);
				return new AttributeODR(attrDef, val);
                            }                            
                
			}
	}

        /**
         * @param descr either a String or a SemanticText instance
         * @return the description as SemanticText
         * @throws IllegalArgumentException if descr is not of the proper type
         */
        private SemanticText descrToSemText(Object descr){
		if(descr instanceof String){
			/* david there should be only SemanticText 
                        descr= new SemanticString();
			String s = (String) value;
			descr.setText(s); */
			return new SemanticText((String) descr); 
		} else if(descr instanceof SemanticText ) 
		{
			/* david  there should be only SemanticText 
                            SemanticText st= (SemanticText) value;
                            descr = SemanticTextFactory.semanticString(st);
			 */
			return (SemanticText) descr;
		} else {
			throw new IllegalArgumentException("Wrong value for the attribute is given! Accepted values are String and SemanticText."); 
		}                        
        }
        
        /**
         * 
         * @param descr either a String, a SemanticText, or a Collection of String or SemanticText
         * @throws IllegalArgumentException if descr is not of the proper type
         */
	private AttributeODR createDescriptionAttributeODR(IAttributeDef attrDef,
			Object descr) {                
                if (descr instanceof Collection){
                    List<ValueODR> valsODR = new ArrayList<ValueODR>();
                    for (Object obj : (Collection) descr) {
                            ValueODR valODR = new ValueODR();
                            valODR.setValue(descrToSemText(obj));
                            valsODR.add(valODR);
                    }
                    return new AttributeODR(attrDef, valsODR);   
                } else {
                    ValueODR val = new ValueODR();
                    val.setValue(descrToSemText(descr));
                    return new AttributeODR(attrDef, val);
                }                 
        }

        private it.unitn.disi.sweb.webapi.model.eb.Structure createStructure(IAttributeDef attrDef,
			HashMap<IAttributeDef, Object> atributes) {
		List<Attribute> attrs = new ArrayList<Attribute>();
		it.unitn.disi.sweb.webapi.model.eb.Structure attributeStructure = new it.unitn.disi.sweb.webapi.model.eb.Structure();
		logger.warn("Hardcoded entity base id 1");
		attributeStructure.setEntityBaseId(1L);

		AttributeDef adef = (AttributeDef) attrDef;
		attributeStructure.setTypeId(adef.getRangeEntityTypeID());

		Iterator<?> it = atributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			AttributeODR aodr = createAttribute((IAttributeDef) pairs.getKey(), pairs.getValue());
			attrs.add(aodr.convertToAttribute());
			it.remove();
		}
		attributeStructure.setAttributes(attrs);
                return attributeStructure;
        }
        
	private AttributeODR createStructureAttribute(IAttributeDef attrDef,
			Collection<HashMap<IAttributeDef, Object>> structs) {

		Attribute nAtr = new Attribute();
		nAtr.setDefinitionId(attrDef.getGUID());
		List<Value> values = new ArrayList<Value>();            
                
                for (HashMap<IAttributeDef, Object> structMap : structs){
                    values.add(new Value(createStructure(attrDef, structMap)));
                }
		
		nAtr.setValues(values);

		AttributeODR a = new AttributeODR(api, nAtr);

		return a;
	}

	private HashMap<String, Long> getVocabularies(){
		HashMap<String, Long> mapVocabs = new HashMap<String, Long> ();
		VocabularyClient vc = new VocabularyClient(api);
		List<Vocabulary> vocabs =vc.readVocabularies(1L, null, null);
		for (Vocabulary v: vocabs){
			mapVocabs.put(v.getLanguageCode(), v.getId()); 
		}
		return mapVocabs;
	}

	/**
	 * Creates Attribute from Name.class
	 *
	 * @param name
	 */
	public AttributeODR createNameAttribute(IAttributeDef attrDef, Name name) {

		Attribute nAtr = new Attribute();
		nAtr.setDefinitionId(attrDef.getGUID());
		List<Value> values = new ArrayList<Value>();
		values.add(new Value(name));
		nAtr.setValues(values);
		AttributeODR a = new AttributeODR(api, nAtr);
		return a;

	}

        /**
         * 
         * @param name a String or an IDict
         * @return a Value representing the name
         * @throws IllegalArgumentException if name is not of the proper class
         */
        private List<Value> nameToValue(Object name){
            List<Value> nameValues = new ArrayList();
		if (name instanceof String)
		{
			String nameInput = (String) name; 
			logger.warn("No Locale is provided for name"+name+"The vocabulary is set to '1'");
			nameValues.add(new Value(nameInput, 1L)); 
                        return nameValues;
		} else if (name instanceof IDict){
			Dict nameDict=(Dict) name;
			HashMap<String,Long> vocabs = getVocabularies();
			Set<Locale> locs =nameDict.getLocales();
			for (Locale l:locs){
				nameValues.add(new Value(nameDict.getString(l), vocabs.get( TraceProvUtils.localeToLanguageTag(l))));//dav so Java 6 doesn't bother us l.toLanguageTag())));
			}                     
                        return nameValues;
                } else {
                        throw new IllegalArgumentException("Wrong Name object is given. "
					+ "Name object should be an instance of String or IDict classes. Found instead instance of class " + name.getClass().getSimpleName());
                        }
            
            
        }
        
        /**
         * 
         * @param attrDef
         * @param name can be a String, an IDict or a Collection of String or IDict.
         */
	public AttributeODR createNameAttributeODR(IAttributeDef attrDef, Object name) {

		Attribute entityNameAttribute = new Attribute();
		entityNameAttribute.setDefinitionId(attrDef.getGUID());

		Name nameStructure = new Name();
		nameStructure.setEntityBaseId(1L); 
		logger.warn("TODO HARDCODED ENTITY BASE ID TO 1.");
		long etypeID;
//		if(attrDef.getRangeEtypeURL()==null){
//			etypeID=10L;
//		}else 
		 etypeID = WebServiceURLs.urlToEtypeID(attrDef.getRangeEtypeURL());
		nameStructure.setTypeId(etypeID);

		EntityTypeService ets = new EntityTypeService();
		EntityType etype = (EntityType) ets.readEntityType(WebServiceURLs.etypeIDToURL(etypeID));
		List<IAttributeDef> etypeAtrDefs = etype.getAttributeDefs();
		Long atrDefId=null; 
		for (IAttributeDef atrdef : etypeAtrDefs){
			if (atrdef.getName().getString(LocaleUtils.toLocale("en")).equalsIgnoreCase("Name"))
			{
				atrDefId = atrdef.getGUID();
			}
		}


		List<Attribute> nameAttributes = new ArrayList<Attribute>();

		Attribute nameAttribute = new Attribute();
		nameAttribute.setDefinitionId(atrDefId); 
		nameAttribute.setConceptId(attrDef.getConcept().getGUID()); 

		List<Value> nameValues = new ArrayList<Value>();
		//Vocabularies 
                
                if (name instanceof Collection){
                    for (Object n : (Collection) name){
                        nameValues.addAll(nameToValue(n));                        
                    }
                } else {
                     nameValues.addAll(nameToValue(name));
                } 

		nameAttribute.setValues(nameValues);
		nameAttributes.add(nameAttribute);
		nameStructure.setAttributes(nameAttributes);

		List<Value> entityNameValues = new ArrayList<Value>();

		entityNameValues.add(new Value(nameStructure)); // here is your link to the name structure, if you want you can put the id copyOf the name instance (if you created it before) but make sure the data type is COMPLEX_TYPE
		entityNameAttribute.setValues(entityNameValues);
		AttributeODR a = new AttributeODR(api, entityNameAttribute);
		return a;

	}

	public void updateAttributeValue(IEntity entity, IAttribute attribute,
			IValue newValue) {
		AttributeODR attr = (AttributeODR) attribute;
		attr.updateValue(newValue);

	}

        
        
	public void updateEntity(IEntity entity) {
                EntityODR ent;
                if (entity instanceof EntityODR){
                    ent = (EntityODR) entity;
                } else {
                    ent = EntityODR.disify(entity, true);   
                }		
		Entity e = ent.convertToEntity();
		InstanceClient instanceCl = new InstanceClient(this.api);
		try {
			instanceCl.update(e);
		} catch (IllegalArgumentException ex) {
			throw new NotFoundException("Such an entity does not exists.");
		}
	}

	public IEntity readEntity(String URL) {

		Long typeID;
		try {			
                        typeID = WebServiceURLs.urlToEntityID(URL);
		} catch (Exception e) {
			return null;
		}

		return readEntity(typeID);
	}

	public String createEntityURL(IEntity entity) {
		Long id = createEntity(entity);				
                return WebServiceURLs.entityIDToURL(id);
	}

	public void exportToRdf(List<String> entityURLs, Writer writer) {
		throw new UnsupportedOperationException("todo to implement");

	}

	public void exportToJsonLd(List<String> entityURLs, Writer writer) throws DisiClientException  {

		if (entityURLs.isEmpty()) 
		{
			//return;
			throw new DisiClientException("The list of entities for export is empty");
		}



		String filename = "test" + System.currentTimeMillis();
		EntityExportService ees = new EntityExportService();
		List<Long> entitiesID = new ArrayList<Long>();

		for (String entityURL : entityURLs) {
			String s = entityURL.substring(entityURL.indexOf("es/") + 3);
			Long eID = Long.parseLong(s);
			entitiesID.add(eID);
		}

		Long fileId = null;
		try {
			fileId = ees.methodPost(entitiesID, filename);
		} catch (ClientProtocolException e) {
			throw new DisiClientException("Error while getting fileId", e);
		} catch (IOException e) {
			throw new DisiClientException("Error while getting fileId", e);
		}
		InputStream is = null;
		try {
			is = ees.methodGet(fileId, "sem" + filename);
		} catch (ClientProtocolException e) {
			throw new DisiClientException("Error while getting input stream", e);
		} catch (IOException e) {
			throw new DisiClientException("Error while getting input stream", e);
		}
		try {
			ees.convertToJsonLd(is, writer);
		} catch (IOException e) {
			throw new DisiClientException("Error while creating jsonLd", e);
		}

	}

	public void exportToCsv(List<String> entityURLs, Writer writer) {
		//TODO exportToCsv
		throw new UnsupportedOperationException("todo to implement");

	}

	public EntityODR readEntityByGUID(Long guid) {
		InstanceClient instanceCl = new InstanceClient(this.api);

		InstanceFilter instFilter = new InstanceFilter();
		instFilter.setIncludeAttributes(true);
		instFilter.setIncludeAttributesAsProperties(true);
		instFilter.setIncludeSemantics(true);
		Entity entity = instanceCl.readEntityByGloabalId(1L, guid, instFilter);
		//Entity entity =  (Entity)instance; 
		EntityODR en = new EntityODR(this.api, entity);
		return en;
	}

	public List<ISearchResult> searchEntities(String partialName, String etypeURL) {
		List<ISearchResult> entities = new ArrayList<ISearchResult>();
		logger.warn("TRYING TO SEARCH ENTITIES - RETURNING NOTHING. TODO IMPLEMENT THIS");
		return entities;
	}

	/* (non-Javadoc)
	 * @see eu.trentorise.opendata.semantics.services.IEntityService#isTemporaryURL(java.lang.String)
	 */
	public boolean isTemporaryURL(String entityURL) {
		return entityURL.contains("instances/new/");
	}
}
