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
		List<IConcept> concepts = kserv.getConcepts(guids);
		Assert.assertEquals(3, concepts.size());
		Assert.assertNotNull(concepts.get(0));
		Assert.assertNotNull(concepts.get(1));
		Assert.assertNotNull(concepts.get(2));
	}

}
