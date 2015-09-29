package eu.trentorise.opendata.disiclient.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geomgraph.index.SweepLineEvent;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.disiclient.Converter;
import eu.trentorise.opendata.disiclient.DisiClientException;
import eu.trentorise.opendata.disiclient.UrlMapper;
import eu.trentorise.opendata.semantics.Checker;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import eu.trentorise.opendata.semantics.model.entity.AStruct;
import eu.trentorise.opendata.semantics.model.entity.Attr;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Entities;
import eu.trentorise.opendata.semantics.model.entity.Entity;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.model.entity.Struct;
import eu.trentorise.opendata.semantics.services.EntityQuery;
import eu.trentorise.opendata.semantics.services.IEntityService;
import eu.trentorise.opendata.semantics.services.SearchResult;
import eu.trentorise.opendata.traceprov.types.Concept;
import it.unitn.disi.sweb.webapi.client.AbstractApiClient;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import it.unitn.disi.sweb.webapi.model.eb.Structure;
import it.unitn.disi.sweb.webapi.model.eb.search.InstanceSearchResult;
import it.unitn.disi.sweb.webapi.model.eb.search.Query;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
import it.unitn.disi.sweb.webapi.model.filters.SearchResultFilter;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;
import it.unitn.disi.sweb.webapi.model.kb.types.EntityType;

public class EntityService implements IEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(EntityService.class);

    private DisiEkb ekb;
    private UrlMapper um;
    private EtypeService ets;

    @Nullable
    private InstanceClient instanceClient;

    private InstanceClient getInstanceClient() {
	if (instanceClient == null) {
	    instanceClient = new InstanceClient(SwebConfiguration.getClientProtocol());
	}
	return instanceClient;
    }

    EntityService(DisiEkb ekb) {
	checkNotNull(ekb);
	this.ekb = ekb;
	this.um = SwebConfiguration.getUrlMapper();
	this.ets = ekb.getEtypeService();
    }

    /**
     * Assigns etype class if class attr is not present
     */
    private Entity fixClassAttr(Entity entity) {

	@Nullable
	AttrDef classAttrDef = null;
	Etype etype = ekb.getEtypeService().readEtype(entity.getEtypeId());

	for (AttrDef ad : etype.getAttrDefs().values()) {
	    // todo probably this should be global...
	    if (KnowledgeService.CLASS_CONCEPT_ID == um.conceptUrlToId(ad.getConceptId())) {
		classAttrDef = ad;
		break;
	    }
	}

	if (classAttrDef == null) {
	    LOG.warn("Found etype " + etype.getId() + "with no class attribute!!");
	} else {
	}

	try {
	    entity.attr(classAttrDef.getId());
	    return entity;
	} catch (OpenEntityNotFoundException ex) {

	    LOG.info("Assigning default class attribute " + etype.getConceptId() + " to entity " + entity.getId());

	    Attr conceptAttr = Attr.ofObject(classAttrDef,
		    Concept.builder()
			    .setId(classAttrDef.getConceptId())
			    .build());
	    return Entity.builder().from(entity)
		    .putAttrs(classAttrDef.getId(), conceptAttr).build();
	}

    }

    public Long createEntity(Name name) {
	Long id = getInstanceClient().create(name);
	return id;
    }

    public void updateEntity(Name name) {

	// EntityODR ent = (EntityODR) name;
	// Entity en=(Entity)ent;
	// Instance instance = instanceCl.readInstance(ent.getLocalID(), null);
	//
	// instance.setTypeId(ent.getEtype().getGUID());
	// instance.setId(entity.getLocalID());
	// List<Attr> attrs = entity.getStructureAttributes();
	// List<Attribute> attributes = ent.convertToAttributes(attrs);
	// instance.setAttributes(attributes);
	// Entity e = ent.convertToEntity();
	getInstanceClient().update(name);
    }

    @Override
    public void deleteEntity(String entityUrl) {
	Instance instance = getInstanceClient().readInstance(SwebConfiguration.getUrlMapper().entityUrlToId(entityUrl),
		null);
	if (instance == null) {
	    throw new OpenEntityNotFoundException(
		    "Tried to delete entity with url " + entityUrl + ", but it doesn't exist.");
	}
	getInstanceClient().delete(instance);
    }

    /**
     *
     *
     * @param entityId
     * @return
     * @throws OpenEntityNotFoundException
     */
    public it.unitn.disi.sweb.webapi.model.eb.Entity readSwebEntity(long entityId) {
	InstanceClient instanceCl = new InstanceClient(SwebConfiguration.getClientProtocol());

	InstanceFilter instFilter = new InstanceFilter();
	instFilter.setIncludeAttributes(true);
	instFilter.setIncludeAttributesAsProperties(true);
	instFilter.setIncludeSemantics(true);
	Instance instance = instanceCl.readInstance(entityId, instFilter);
	if (instance == null) {
	    throw new OpenEntityNotFoundException("Couldn't find entity with URL " + um.entityIdToUrl(entityId));
	} else {
	    return (it.unitn.disi.sweb.webapi.model.eb.Entity) instance;
	}

    }

    @Override
    public List<Entity> readEntities(Iterable<String> entityUrls) {

	List<Long> requestedInstanceIds = new ArrayList();

	for (String entityURL : entityUrls) {
	    try {
		requestedInstanceIds.add(um.entityUrlToId(entityURL));
	    } catch (Exception ex) {
		throw new OpenEntityNotFoundException("Tried to read entity with ill formatted url: " + entityURL, ex);
	    }
	}

	if (requestedInstanceIds.isEmpty()) {
	    return new ArrayList();
	}

	List<Instance> foundInstances = readInstances(requestedInstanceIds);
	if (Sets.newHashSet(foundInstances).size() < Sets.newHashSet(requestedInstanceIds).size()) {
	    throw new OpenEntityNotFoundException(Converter.swebIdsToOEIds(requestedInstanceIds),
		    Converter.swebInstancesToOEIds(foundInstances));
	}
	List<Entity> ret = new ArrayList();

	for (Instance epEnt : foundInstances) {
	    EntityType swebEntityType = ets.readSwebEntityType(epEnt.getTypeId());
	    Entity en = ekb.getConverter().swebEntityToOeEntity((it.unitn.disi.sweb.webapi.model.eb.Entity) epEnt,
		    swebEntityType);
	    Checker.of(ekb).checkEntity(en);
	    ret.add(en);
	}
	return ret;
    }

    public List<Instance> readInstances(Iterable<Long> instanceIds) {

	InstanceFilter instFilter = new InstanceFilter();
	instFilter.setIncludeAttributes(true);
	instFilter.setIncludeAttributesAsProperties(true);
	instFilter.setIncludeSemantics(true);

	List<Instance> instances = getInstanceClient().readInstancesById(Lists.newArrayList(instanceIds), instFilter);

	List<Entity> ret = new ArrayList();
	List<String> etypeOeIds = new ArrayList();
	List<Long> structIds = new ArrayList();
	for (Instance epEnt : instances) {
	    etypeOeIds.add(um.etypeIdToUrl(epEnt.getTypeId()));
	}

	// so we cache needed etypes
	Entities.resolveEtypesById(etypeOeIds, ekb.getEtypeService());

	return instances;

    }

    public Struct readName(long nameInstanceId) {

	InstanceFilter instFilter = new InstanceFilter();
	instFilter.setIncludeAttributes(true);
	instFilter.setIncludeAttributesAsProperties(true);
	instFilter.setIncludeSemantics(true);

	Instance instance = getInstanceClient().readInstance(nameInstanceId, instFilter);

	Name name = (Name) instance;

	ComplexType swebComplexType = ets.readSwebComplexType(name.getTypeId());
	return Struct.copyOf(ekb.getConverter().swebInstanceToOeStruct(name, swebComplexType));
    }

    public Structure readSwebStructure(long entityID) {

	InstanceFilter instFilter = new InstanceFilter();
	instFilter.setIncludeAttributes(true);
	instFilter.setIncludeAttributesAsProperties(true);
	instFilter.setIncludeSemantics(true);

	Instance instance = getInstanceClient().readInstance(entityID, instFilter);

	return (it.unitn.disi.sweb.webapi.model.eb.Structure) instance;
    }

    @Override
    public void updateEntity(Entity entity) {
	throw new UnsupportedOperationException(
		"Tried to update entity " + entity.getId() + ", but update is  not implemented yet");
    }

    @Override
    public Entity readEntity(String URL) {

	return readEntities(Lists.newArrayList(URL)).get(0);

    }

    @Override
    public Entity createEntity(Entity entity) {
	Entity fixedEntity = fixClassAttr(entity);
	it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity = ekb.getConverter().oeEntityToSwebEntity(fixedEntity,
		true, true);
	Long retId = getInstanceClient().create(swebEntity);
	return readEntity(um.entityIdToUrl(retId));
    }

    @Override
    public void exportToRdf(Iterable<String> entityURLs, Writer writer) {

	UrlMapper um = SwebConfiguration.getUrlMapper();

	String filename = "test" + System.currentTimeMillis();
	EntityExportService ees = ekb.getEntityExportService();
	List<Long> entitiesID = new ArrayList();

	for (String entityURL : entityURLs) {

	    Long eID = um.entityUrlToId(entityURL);
	    entitiesID.add(eID);
	}

	if (entitiesID.isEmpty()) {
	    throw new IllegalArgumentException("The list of entities for export is empty");
	}

	Long fileId = null;
	try {
	    fileId = ees.methodPostRDF(entitiesID, filename);
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

	BufferedWriter bw = new BufferedWriter(writer);
	int letter;
	try {
	    while ((letter = is.read()) != -1) {
		bw.write((char) letter);
		bw.flush();
	    }
	} catch (IOException ex) {

	    throw new DisiClientException("Error while writing file!", ex);
	}

    }

    @Override
    public void exportToJsonLd(Iterable<String> entityURLs, Writer writer) throws DisiClientException {

	UrlMapper um = SwebConfiguration.getUrlMapper();

	String filename = "test" + System.currentTimeMillis();
	EntityExportService ees = ekb.getEntityExportService();
	List<Long> entitiesID = new ArrayList();

	for (String entityURL : entityURLs) {
	    entitiesID.add(um.entityUrlToId(entityURL));
	}

	if (entitiesID.isEmpty()) {
	    throw new IllegalArgumentException("The list of entities to export is empty");
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

    @Override
    public void exportToCsv(Iterable<String> entityURLs, Writer writer) {
	// TODO exportToCsv
	throw new UnsupportedOperationException("todo to implement");

    }

    public it.unitn.disi.sweb.webapi.model.eb.Entity readEntityByGlobalId(Long globalId) {

	InstanceFilter instFilter = new InstanceFilter();
	instFilter.setIncludeAttributes(true);
	instFilter.setIncludeAttributesAsProperties(true);
	instFilter.setIncludeSemantics(true);
	LOG.warn("TODO - USING FIXED ENTITYBASE WITH ID 1");
	it.unitn.disi.sweb.webapi.model.eb.Entity entity = getInstanceClient().readEntityByGloabalId(1L, globalId,
		instFilter);
	return entity;
    }

    /**
     * Calculates a full url out of the provided params
     *
     * @param path
     *            something like /api/3/package_show
     * @param params
     *            list of key, value parameters. They must be not be url
     *            encoded. i.e. "id","laghi-monitorati-trento"
     * @return the full url to be called.
     * @throws JackanException
     *             if there is any error building the url
     */
    private String calcFullUrl(String path, Object... params) {
	checkNotNull(path);

	try {
	    StringBuilder sb = new StringBuilder().append(SwebConfiguration.getBaseUrl()).append(path);
	    for (int i = 0; i < params.length; i += 2) {
		sb.append(i == 0 ? "?" : "&")
			.append(URLEncoder.encode(params[i].toString(), "UTF-8"))
			.append("=")
			.append(URLEncoder.encode(params[i + 1].toString(),
				"UTF-8"));
	    }
	    return sb.toString();
	} catch (Exception ex) {
	    throw new DisiClientException("Error while building url to perform GET! \n path: " + path + " \n params: "
		    + Arrays.toString(params), ex);
	}
    }

    
    private class SearchEntityNameClient extends AbstractApiClient<SwebEntitySearchResultWrapper> {

	public SearchEntityNameClient() {
	    super(SwebConfiguration.getClientProtocol(),
		    SwebEntitySearchResultWrapper.class,
		    "/search/byName",
		    "SwebEntitySearchResult");
	}

	public List<SearchResult> searchEntitiesByName(EntityQuery query) {
	    // BASE_URL/search/byName?query=borgo%20valsu&isPrefix=true&entityBase=1&includeCount=false&idsOnly=false&pageIndex=1&pageSize=10&maxDepth=1&includeSemantics=false&maxValues=10&includeAttributes=false&createAttributeMap=false&attributeFilterType=ATTRIBUTE_DEF_ID&includeAttributesAsProperties=true&includeTimestamps=false&locale=all

	    Map<String, String> params = new HashMap();
	    params.put("query", query.getPartialName());

	    LOG.warn("TODO - SETTING ENTITY BASE TO 1 IN SEARCH");

	    params.put("isPrefix", "true");
	    params.put("entityBase", "1");
	    params.put("pageIndex", String.valueOf(query.getPageIndex() + 1));
	    params.put("pageSize", String.valueOf(query.getPageSize()));
	    params.put("includeAttributesAsProperties", "true");

	    if (!query.getEtypeId().isEmpty()) {
		params.put("type", String.valueOf(um.etypeUrlToId(query.getEtypeId())));
	    }

	    LOG.warn("TODO - SETTING LOCALE TO 'all' IN SEARCH");
	    params.put("locale", "all");

	    SwebEntitySearchResultWrapper results = this.read("/search/byName", params);
	    
	    List<SearchResult> ret = new ArrayList();
	    
	    Dict dict;
	    for (SwebEntitySearchResult swebSr : results.results){
		 
		if (swebSr.names == null) {
		    dict = Dict.of();
		} else {
		    Dict.Builder dictBuilder = Dict.builder();
		    for (SwebNameResult name : swebSr.names) {
			dictBuilder.put(Converter.multimapToDict(name.names));
		    }
		    dict = dictBuilder.build();
		}
		
		ret.add(SearchResult.of(um.entityIdToUrl(swebSr.id), dict));
	    }
	    
	    return ret;
	    
	}

	@Override
	protected String getIdPath(long id) {
	    // TODO Auto-generated method stub
	    throw new UnsupportedOperationException("Not supported yet.");
	}

    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class SwebNameResult {
	public Map<String, List<String>> names;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class SwebEntitySearchResultWrapper {
	public List<SwebEntitySearchResult> results;
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class SwebEntitySearchResult {
	public long id;
	public long entityBaseId;
	public long typeId;
	public List<SwebNameResult> names;
	public long globalId;
    }

    @Override
    public List<SearchResult> searchEntities(EntityQuery query) {
	
	LOG.warn("TODO - SETTING ENTITY PARTIAL NAME TO LOWERCASE");
	Locale locale;
	if (query.getLocale().equals(Locale.ROOT)) {
	    List<Locale> defaultLocales = ekb.getDefaultLocales();
	    locale = defaultLocales.get(0);
	} else {
	    locale = query.getLocale();
	}
	String lowerCasedPartialName = query.getPartialName().toLowerCase(locale).trim();

	List<SearchResult> ret = new ArrayList();
	SearchResultFilter srf = new SearchResultFilter();
	srf.setLocale(locale);
	srf.setIncludeAttributesAsProperties(true);
	Long swebEtypeId = null;
	if (!query.getEtypeId().isEmpty()) {
	    swebEtypeId = um.etypeUrlToId(query.getEtypeId());
	}

	List<Instance> instances;

	Pagination page = new Pagination();

	page.setPageSize((int) query.getPageSize());
	page.setPageIndex((int) (query.getPageIndex() + 1));

	if (lowerCasedPartialName.isEmpty()) {
	    LOG.warn("TODO - USING HARD CODED ENTITY BASE '1' IN SEARCH");
	    instances = getInstanceClient().readInstances(1L, swebEtypeId, null, null, page);
	    List<it.unitn.disi.sweb.webapi.model.eb.Entity> swebEntities = ekb.getConverter()
		    .swebInstancesToSwebEntities(instances);

	    for (it.unitn.disi.sweb.webapi.model.eb.Entity swebEntity : swebEntities) {
		SearchResult res = ekb.getConverter().makeSearchResult(swebEntity);
		ret.add(res);
	    }

	} else {
	    return new SearchEntityNameClient().searchEntitiesByName(query);
	}

	return ret;
    }

    /**
     *
     * @see eu.trentorise.opendata.semantics.services.EntityService#isTemporaryURL(java.lang.String)
     */
    @Override
    public boolean isTemporaryURL(String entityURL) {
	return entityURL.contains("instances/new/");
    }

    @Override
    public AStruct readStruct(String URL) {
	Long structureId;
	try {
	    structureId = SwebConfiguration.getUrlMapper().entityUrlToId(URL);
	} catch (Exception ex) {
	    throw new OpenEntityNotFoundException("Tried to read entity with ill formatted url: " + URL, ex);
	}

	it.unitn.disi.sweb.webapi.model.eb.Structure swebStructure = readSwebStructure(structureId);

	ComplexType swebComplexType = ets.readSwebComplexType(swebStructure.getTypeId());

	return ekb.getConverter().swebInstanceToOeStruct(swebStructure, swebComplexType);
    }

    @Override
    public List<? extends AStruct> readStructs(Iterable<String> structUrls) {

	List<Long> instanceIds = new ArrayList();

	for (String structURL : structUrls) {
	    try {
		instanceIds.add(um.entityUrlToId(structURL));
	    } catch (Exception ex) {
		throw new OpenEntityNotFoundException("Tried to read entity with ill formatted url: " + structURL, ex);
	    }
	}

	if (instanceIds.isEmpty()) {
	    return new ArrayList();
	}

	List<Instance> instances = readInstances(instanceIds);
	List<AStruct> ret = new ArrayList();

	for (Instance swebInstance : instances) {
	    EntityType swebEntityType = ets.readSwebEntityType(swebInstance.getTypeId());
	    AStruct struct = ekb.getConverter().swebInstanceToOeStruct(
		    (it.unitn.disi.sweb.webapi.model.eb.Instance) swebInstance,
		    swebEntityType);
	    Checker.of(ekb).checkStruct(struct, false);
	    ret.add(struct);
	}
	return ret;
    }

}
