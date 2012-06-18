/**
 * 
 */
package it.uniba.di.lacam.fanizzi.experiment.dataset;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * @author Marco
 *
 */
public class FilmAVG
{

	private OWLNamedIndividual individual;
	private Integer ratingAVG;
	
	public FilmAVG(OWLNamedIndividual individual, Integer ratingAVG)
	{
		super();
		this.individual = individual;
		this.ratingAVG = ratingAVG;
	}
	
	public OWLNamedIndividual getIndividual()
	{
		return individual;
	}
	
	public Integer getRating()
	{
		return ratingAVG;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((individual == null) ? 0 : individual.hashCode());
		result = prime * result
				+ ((ratingAVG == null) ? 0 : ratingAVG.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilmAVG other = (FilmAVG) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual))
			return false;
		if (ratingAVG == null) {
			if (other.ratingAVG != null)
				return false;
		} else if (!ratingAVG.equals(other.ratingAVG))
			return false;
		return true;
	}
	
	
	
	
}
