/**
 * 
 */
package it.uniba.di.lacam.fanizzi.experiment.dataset;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.BiMap;


/**
 * @author Marco
 *
 */

public interface ExperimentDataset {

	/**
	 * @return return the number of the rating
	 */
	
	public int size();
	
	/**
	 * @return the max rating value
	 */
	public int maxRating();
	/**
	 * return the value of the indice-th rating example
	 * 
	 * @param indice
	 * @return the value of the indice-th rating example
	 */
//	public int getRatingValue (int indice);
	public int getRatingValue (OWLNamedIndividual rating);
	
	/**
	 * @param indice
	 * @return the OWLNamedIndividual of the indice-th example
	 */
//	public OWLNamedIndividual getIndividual (int indice);
	public OWLNamedIndividual getIndividual (OWLNamedIndividual rating);
	
	/**
	 * @return a Set with all the individuals
	 */
	public Set<OWLNamedIndividual> getIndividuals();
	
	/**
	 * @param indice
	 * @return 
	 */
//	public int indexIndividual(int indice);
	
	/**
	 * @return return random rating
	 */
	public OWLNamedIndividual random();
	
	/**
	 * @return a set with all the ratings
	 */
	public Set<OWLNamedIndividual> getRatings ();
}
