package eu.trentorise.opendatarise.semantics.model.facade;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Structure;
import it.unitn.disi.sweb.webapi.model.eb.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendatarise.semantics.model.entity.EntityODR;
import eu.trentorise.opendatarise.semantics.model.knowledge.ConceptODR;
//import Structure;

/**
 *
 * @author enrico
 */
public class ImpiantoDiRisalitaFacade extends EntityODR {

	IProtocolClient api;

	public static final Long ATTR_TYPE_LATITUDE = 69L;
	public static final Long ATTR_TYPE_LONGITUDE = 68L;
	public static final Long ATTR_TYPE_OPENING_HOUR = 30L;
	public static final Long ATTR_TYPE_CLOSING_HOUR = 31L;
	public static final Long ATTR_TYPE_OPENING_HOURS = 66L;
	public static final Long CLASS = 58L;

	public ImpiantoDiRisalitaFacade(Locale locale, String host, int port) {
		api = ProtocolFactory.getHttpClient(locale, host, port);
	}

	public ImpiantoDiRisalitaFacade(IProtocolClient api) {
		this.api = api;
	}

	//	public ImpiantoDiRisalitaFacade(IProtocolClient api, String csv) {
	//		this.api = api;
	//		String[] split = csv.split(",");
	//		if (split.length < 5) {
	//			throw new IllegalArgumentException("Expecting at least five values in the csv string."
	//					+ "They should be separated by commas. This is the not working readed value: " + csv);
	//		}
	//		setName(split[0]);
	//		setLocation(split[1], split[2]);
	//		setOpeningHours(split[3], split[4]);
	//	}

	public Long createEntity(String name, String claz, float latitude, float longitude, String openHour, String closeHour){
		//brutforce to be fixed 
		
		long classConcept = 0;
		if (claz.equals("Cabinovia")){
			classConcept=15797L;

		} else 
			if (claz.equals("Seggiovia ad agganciamento automatico")){
				classConcept=120783L;

			}else
				if (claz.equals("Sciovia")){
					classConcept=23390;

				}else
					if (claz.equals("Seggiovia")){
						classConcept=16193;

					}
					else 
						classConcept=15797L;
		ConceptODR codr =new ConceptODR();
		
		ConceptODR c = codr.readConceptGlobalID(classConcept);
		long cid =c.getId();

		InstanceClient  ic = new InstanceClient(api);
		List<Attribute> attributes  = new ArrayList<Attribute>();
		this.setAttributes(attributes);
		setName(name);
		setClass(cid);
		setLatitude(latitude);
		setLongitude(longitude);
		setOpeningHours(closeHour,openHour);
		Entity facility = new Entity();
		facility.setEntityBaseId(1L);
		facility.setTypeId(12L);
		facility.setAttributes(this.getAttributes());
		System.out.println(this.getAttributes().size());
		Long id  =ic.create(facility);
		return id;
	}

	public EntityODR createEmptyEntity (String name, String claz, float latitude, float longitude, String openHour, String closeHour){
		long classConceptGUID = 0;

		ConceptODR con = new ConceptODR();
		if (claz.equals("Cabinovia")){
			classConceptGUID=15797L;

		} else
			if (claz.equals("Seggiovia ad agganciamento automatico")){
				classConceptGUID=120783L;

			} else
				if (claz.equals("Sciovia")){
					classConceptGUID=23390L;

				} else
					if (claz.equals("Seggiovia")){
						classConceptGUID=16193L;
					}
					else classConceptGUID=15797L;

		long classConcept = con.readConceptGUID(classConceptGUID);


		InstanceClient  ic = new InstanceClient(api);
		List<Attribute> attributes  = new ArrayList<Attribute>();
		this.setAttributes(attributes);
		setName(name);
		setClass(classConcept);
		System.out.println("CLASS CONCEPT: "+classConcept);
		System.out.println("CLASS LABEL: "+claz);

		setLatitude(latitude);
		setLongitude(longitude);
		setOpeningHours(openHour,closeHour);
		Entity facility = new Entity();
		facility.setEntityBaseId(1L);
		facility.setTypeId(12L);
		facility.setAttributes(this.getAttributes());
		//System.out.println(this.getAttributes().size());
		EntityODR facilityEntity = new EntityODR(api,facility);

		return facilityEntity;
	}


	public void setName(String name) {
		//InstanceClient  ic = new InstanceClient(api);
		Name nameStructure = new Name();
		List<Attribute> nameAttributes = new ArrayList<Attribute>();
		nameStructure.setEntityBaseId(1L);
		
		Attribute nameAttribute = new Attribute();
		nameAttribute.setDefinitionId(55L);
		nameAttributes.add(nameAttribute);
		List<Value>nameValues=new ArrayList<Value>();
		nameValues.add(new Value(name, 1L));
		//BE CAREFULL WITH VOCABULARY
		nameAttribute.setValues(nameValues);
		this.getAttributes().add(nameAttribute);
		nameStructure.setAttributes(nameAttributes);
		//	long id  =ic.create(nameStructure);
		//System.out.println("Name ID:"+id);
	}

	public void setLatitude(float latitude) {
		Attribute attribute = new Attribute();
		attribute.setDefinitionId(ATTR_TYPE_LATITUDE);
		List<Value> vals=new ArrayList<Value>();
		vals.add(new Value(latitude));
		attribute.setValues(vals);
		this.getAttributes().add(attribute);
	}

	public void setClass(long id ){

		Attribute attribute = new Attribute();
		attribute.setDefinitionId(CLASS);
		List<Value> vals=new ArrayList<Value>();
		vals.add(new Value(id));
		attribute.setValues(vals);
		this.getAttributes().add(attribute);


	}

	public void setLongitude(float longitude) {
		Attribute attribute = new Attribute();
		attribute.setDefinitionId(ATTR_TYPE_LONGITUDE);
		List<Value> vals=new ArrayList<Value>();
		vals.add(new Value(longitude));
		attribute.setValues(vals);
		this.getAttributes().add(attribute);

	}

	public Attribute setOpeningTime(String openHour) {
		Attribute attribute = new Attribute();
		attribute.setDefinitionId(ATTR_TYPE_OPENING_HOUR);
		List<Value> vals=new ArrayList<Value>();
		vals.add(new Value(openHour));
		attribute.setValues(vals);
		return attribute;
	}

	public Attribute setClosingTime(String closeHour) {
		Attribute attribute = new Attribute();
		attribute.setDefinitionId(ATTR_TYPE_CLOSING_HOUR);
		List<Value> vals=new ArrayList<Value>();
		vals.add(new Value(closeHour));
		attribute.setValues(vals);
		return attribute;
	}

	public void setOpeningHours(String openHour, String closeHour){
		Attribute attribute = new Attribute();
		attribute.setDefinitionId(ATTR_TYPE_OPENING_HOURS);

		Attribute attrClose = setClosingTime(closeHour);
		Attribute attrOpen = setOpeningTime(openHour);
		InstanceClient  ic = new InstanceClient(api);
		//
		Structure hoursStructure = new Structure();
		hoursStructure.setEntityBaseId(1L);
		hoursStructure.setTypeId(7L);
		List<Attribute> hoursAttributes = new ArrayList<Attribute>();
		hoursAttributes.add(attrClose);
		hoursAttributes.add(attrOpen);
		hoursStructure.setAttributes(hoursAttributes);

		long id  =ic.create(hoursStructure);
		//
		//		Attribute strAttributes = new Attribute();
		//		strAttributes.setDefinitionId(ATTR_TYPE_OPENING_HOURS);

		List<Value> values=new ArrayList<Value>();
		values.add(new Value(id));

		attribute.setValues(values);
		this.getAttributes().add(attribute);
	}


}
