package eu.trentorise.opendata.disiclient.services.shematching;

import java.util.Comparator;

import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 *
 */
public class CustomComparator implements Comparator<ISchemaCorrespondence> {

    public int compare(ISchemaCorrespondence sc1, ISchemaCorrespondence sc2) {
        Float score1 = sc1.getScore();
        Float score2 = sc2.getScore();
        return score2.compareTo(score1);
    }
}
