package it.uniba.di.lacam.fanizzi.experiment.dataset;

import it.uniba.di.lacam.fanizzi.OntologyModel;
import it.uniba.di.lacam.fanizzi.utils.XMLFilmRatingStream;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.Table;


public class ExperimentRatingW implements ExperimentDataset {

	
	private Table<Individual, Individual, Integer> ratings;							// rating, film, ratingValue,   row, column, cell

	
	public ExperimentRatingW(String urlOWLFile) {
		Table<Individual, Individual, Integer> prov = XMLFilmRatingStream.leggi();
		if (prov == null)
		{
			System.out.println("Lettura del file fallita, creazione di ExperimentRating e scrittura del file");
			OntologyModel om = new OntologyModel(urlOWLFile);
			ExperimentRating er = new ExperimentRating(om);
			prov = er.createTable();
			XMLFilmRatingStream.scrivi(prov);
		}
		else
			System.out.println("Lettura del file avvenuta con successo");
		ratings = prov;
	}

	public int maxRating()
	{
		return Collections.max(ratings.values());
	}

	/**
	 * @param rating rating di cui si vuol conoscere il valore
	 * @return retituisce il valore numerico del rating
	 */

	public int getRatingValue (Individual rating)
	{	
		return ratings.rowMap().get(rating).entrySet().iterator().next().getValue();
	}

	public Individual getIndividual (Individual rating)
	{
		return ratings.rowMap().get(rating).entrySet().iterator().next().getKey();
	}
	
	public int size()
	{
		return ratings.size();
	}

	public Set<Individual> getIndividuals()
	{
		return ratings.columnKeySet();
	}

	public Set<Individual> getRatings()
	{
		return ratings.rowKeySet();
	}
	
	public Set<Individual> getRatings(Individual individual)
	{
		return ratings.columnMap().get(individual).keySet();
	}
}
