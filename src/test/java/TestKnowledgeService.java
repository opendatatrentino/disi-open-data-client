import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import eu.trentorise.opendata.semantics.model.knowledge.IConcept;
import eu.trentorise.opendatarise.semantics.services.KnowledgeService;


public class TestKnowledgeService {

	List<Long> guids = new ArrayList(){
		{add(132L);
		add(46263L);
		add(46270L);
		}
	};

	@Test
	public void testGetConcept(){
		KnowledgeService kserv= new KnowledgeService();
		String url = "http://opendata.disi.unitn.it:8080/odr/concepts/120";
		IConcept con = kserv.getConcept(url);
	}

	@Test
	public void testGetRootConcept(){
		KnowledgeService kserv= new KnowledgeService();
		IConcept concept = kserv.getRootConcept();
		System.out.println(concept.getURL());
	}
}
