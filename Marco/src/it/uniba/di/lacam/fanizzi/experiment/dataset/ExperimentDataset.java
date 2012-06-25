/**
 * 
 */
package it.uniba.di.lacam.fanizzi.experiment.dataset;

import java.util.Set;

import org.dllearner.core.owl.Individual;


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
	public int getRatingValue (Individual rating);
	
	/**
	 * @param indice
	 * @return the OWLNamedIndividual of the indice-th example
	 */
//	public OWLNamedIndividual getIndividual (int indice);
	public Individual getIndividual (Individual rating);
	
	/**
	 * @return a Set with all the individuals
	 */
	public Set<Individual> getIndividuals();
	
	/**
	 * @param indice
	 * @return 
	 */
//	public int indexIndividual(int indice);
	
	
	/**
	 * @return a set with all the ratings
	 */
	public Set<Individual> getRatings ();
	
	/**
	 * @param individual film di cui si vuole conoscere l'insieme di rating
	 * @return insieme di rating appartenenti al film
	 */
	public Set<Individual> getRatings(Individual individual);
}
