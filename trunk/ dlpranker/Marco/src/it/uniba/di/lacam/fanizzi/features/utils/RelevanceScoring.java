package it.uniba.di.lacam.fanizzi.features.utils;

import it.uniba.di.lacam.fanizzi.features.ie.InformationTheoryUtils;

import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

public class RelevanceScoring<I extends Individual> {

	private Individual inference;
	
	public RelevanceScoring(Individual inference,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
//		super(positives, negatives, neutrals);
		this.inference = inference;
	}

	@Override
	public Double score(AbstractModel m) {
		if (!(m instanceof Committee)) {
			throw new IllegalStateException("The model should be an instance of Committee");
		}
		
		Committee c = (Committee) m;
		Set<Description> components = c.getComponents();
		double sS = components.size();
		
		InformationTheoryUtils<I> itu = new InformationTheoryUtils<I>(this.inference);
		double sum = 0.0;
		
		for (Description X : components) {
			sum += itu.I(X, positives, negatives);
		}
		
		double ret = (sS > 0.0 ? sum / sS : Double.NEGATIVE_INFINITY);
		return ret;
	}

}
