package eu.trentorise.opendatarise.semantics.services.shematching;

import java.util.Comparator;

import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;
import eu.trentorise.opendatarise.semantics.services.model.SchemaCorrespondence;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 4 Mar 2014
 * 
 */

	public class CustomComparator implements Comparator<ISchemaCorrespondence> {
	    public int compare(ISchemaCorrespondence sc1, ISchemaCorrespondence sc2) {
	        return sc2.getScore().compareTo(sc1.getScore());
	    }
	}
