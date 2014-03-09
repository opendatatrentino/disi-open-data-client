package eu.trentorise.opendatarise.semantic.services.shematching;

import java.util.Comparator;

import eu.trentorise.opendatarise.semantic.services.model.SchemaCorrespondence;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 4 Mar 2014
 * 
 */

	public class CustomComparator implements Comparator<SchemaCorrespondence> {
	    public int compare(SchemaCorrespondence sc1, SchemaCorrespondence sc2) {
	        return sc1.getScore().compareTo(sc2.getScore());
	    }
	}

