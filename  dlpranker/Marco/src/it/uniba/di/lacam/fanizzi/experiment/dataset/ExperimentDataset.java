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
	 * @return the array with all the individual
	 */
//	public OWLNamedIndividual[] getIndividuals();
	/**
	 * @return the array with all the individual
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
}
