package it.uniba.di.lacam.fanizzi.experiment.dataset;

import it.uniba.di.lacam.fanizzi.OntologyModel;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ExperimentRatingW extends ExperimentRating implements ExperimentDataset {

	public ExperimentRatingW(OntologyModel ontologyModel) {
		super(ontologyModel);
		// TODO Auto-generated constructor stub
	}

	public int maxRating()
	{
		return super.maxRating();
	}

	/**
	 * @param rating rating di cui si vuol conoscere il valore
	 * @return retituisce il valore numerico del rating
	 */

	public int getRatingValue (Individual rating)
	{
		OWLNamedIndividual ratingONI = (OWLNamedIndividual) OWLAPIConverter.getOWLAPIIndividual(rating); 
				
		return super.getRatingValue(ratingONI);
		
	}
	

	public Individual getIndividual (Individual rating)
	{
		OWLNamedIndividual ratingONI = (OWLNamedIndividual) OWLAPIConverter.getOWLAPIIndividual(rating); 
		
		return OWLAPIConverter.convertIndividual(super.getIndividual(ratingONI));
	}
	
	public int size()
	{
		return super.size();
	}

	public Set<Individual> getIndividuals()
	{
		Set<Individual> individui = new HashSet<Individual>();
		
		for (OWLNamedIndividual i : super.getIndividuals(0))
			individui.add(OWLAPIConverter.convertIndividual(i));
		
		return individui;
	}
	
	public Individual random()
	{
		return OWLAPIConverter.convertIndividual(super.random(0));
	}
	
	public Set<Individual> getRatings()
	{
		Set<Individual> individui = new HashSet<Individual>();
		
		for (OWLNamedIndividual i : super.getRatings(0))
			individui.add(OWLAPIConverter.convertIndividual(i));
		
		return individui;
	}
	
	public Set<Individual> getRatings(Individual individual)
	{
		Set<Individual> individui = new HashSet<Individual>();
		
		for (OWLNamedIndividual i : super.getRatings((OWLNamedIndividual)OWLAPIConverter.getOWLAPIIndividual(individual)))
			individui.add(OWLAPIConverter.convertIndividual(i));
		
		return individui;
	}
}
