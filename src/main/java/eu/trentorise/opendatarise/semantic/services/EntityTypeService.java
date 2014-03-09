package eu.trentorise.opendatarise.semantic.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
import it.unitn.disi.sweb.webapi.client.kb.AttributeDefinitionClient;
import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.client.kb.KbClient;
import it.unitn.disi.sweb.webapi.model.kb.KnowledgeBase;
import it.unitn.disi.sweb.webapi.model.kb.types.AttributeDefinition;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.entity.IUniqueIndex;
import eu.trentorise.opendata.semantics.services.IEntityTypeService;
import eu.trentorise.opendatarise.semantic.model.entity.AttributeDef;
import eu.trentorise.opendatarise.semantic.model.entity.EntityType;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 27 Mar 2014
 * 
 */
public class EntityTypeService implements IEntityTypeService {

	public List<IEntityType> getAllEntityTypes() {
		KbClient kbClient = new KbClient(getClientProtocol());
		//TODO decide what to do with knowledge base id which knowldege base id to take the first one? 
		List<KnowledgeBase> kbList = kbClient.readKnowledgeBases(null);
		long kbId =  kbList.get(0).getId();
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		List<ComplexType> complexTypeList= ctc.readComplexTypes(kbId, null,null,null);
		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		List<IEntityType> etypes = new ArrayList<IEntityType>();

		for(ComplexType cType: complexTypeList){
			EntityType eType = new EntityType(cType);
			List<AttributeDefinition>  attrDefList = attrDefs.readAttributeDefinitions(cType.getId(), null, null, null);

			List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();
			for (AttributeDefinition attrDef: attrDefList){
				IAttributeDef attributeDef = new AttributeDef(attrDef);
				attributeDefList.add(attributeDef);
			}
			eType.setAttrs(attributeDefList);
			System.out.println(eType.toString());

			etypes.add(eType);
		}
		return etypes;
	}

	public IEntityType getEntityType(long id){
		ComplexTypeClient ctc = new ComplexTypeClient(getClientProtocol());
		ComplexType complexType = ctc.readComplexType(id, null);
		EntityType eType = new EntityType(complexType);
		AttributeDefinitionClient attrDefs = new AttributeDefinitionClient(getClientProtocol());
		List<AttributeDefinition>  attrDefList = attrDefs.readAttributeDefinitions(id, null, null, null);
		List<IAttributeDef> attributeDefList = new ArrayList<IAttributeDef>();
		for (AttributeDefinition attrDef: attrDefList){
			IAttributeDef attributeDef = new AttributeDef(attrDef);
			attributeDefList.add(attributeDef);
		}
		eType.setAttrs(attributeDefList);
		return eType;
	}

	public void addAttributeDefToEtype(IEntityType entityType,
			IAttributeDef attrDef) {
	    EntityType eType = (EntityType)entityType;
	    
		eType.addAttributeDef(attrDef);
		
	}

	public void addUniqueIndexToEtype(IEntityType entityType,
			IUniqueIndex uniqueIndex) {
		// TODO Auto-generated method stub
	}

	/** The method returns client protocol 
	 * @return returns an instance of ClientProtocol that contains information where to connect(Url adress and port) and locale
	 */
	
	private IProtocolClient getClientProtocol(){
		IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
		return api;
	}

}
