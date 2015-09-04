package eu.trentorise.opendata.disiclient.model.knowledge;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendata.disiclient.DictFactory;
import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptODR implements IConcept {

    private static final Logger LOG = LoggerFactory.getLogger(ConceptODR.class.getName());

    private long id;
    private String label;
    private long globalId;
    private Map<String, String> name;
    private Map<String, String> description;
    private IProtocolClient api;

    public ConceptODR() {
    }

    public ConceptODR(Concept con) {
        this.label = con.getLabel();
        this.id = con.getId();
        this.globalId = con.getGlobalId();
        this.name = con.getName();
        this.description = con.getDescription();
    }

    public String getLabel() {
        return label;
    }

    public IProtocolClient getApi() {
        return api;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public String getURL() {
        return SwebConfiguration.getUrlMapper().conceptIdToUrl(this.id);        
    }

    public Long getGUID() {
        return globalId;
    }

    @Override
    public Dict getName() {
        return DictFactory.mapToDict(this.name);
    }

    @Override
    public Dict getDescription() {
        return DictFactory.mapToDict(this.description);
    }

}
