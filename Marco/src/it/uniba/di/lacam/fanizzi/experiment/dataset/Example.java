package it.uniba.di.lacam.fanizzi.experiment.dataset;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Example {
	
	private OWLNamedIndividual individual;
	private OWLNamedIndividual rating;
//	public int value;
	
	public Example(OWLNamedIndividual individual, OWLNamedIndividual rating)
	{
		super();
		this.individual = individual;
		this.rating = rating;
//		this.value = value;
	}
	
	public OWLNamedIndividual getIndividual()
	{
		return individual;
	}
	
	public OWLNamedIndividual getRating()
	{
		return rating;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((individual == null) ? 0 : individual.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
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
		Example other = (Example) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual))
			return false;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		return true;
	}
		


	
}
