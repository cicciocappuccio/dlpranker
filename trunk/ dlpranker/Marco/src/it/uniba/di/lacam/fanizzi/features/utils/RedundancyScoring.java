package it.uniba.di.lacam.fanizzi.features.utils;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

public class RedundancyScoring<I extends Individual> {

	private Individual inference;
	
	public RedundancyScoring(Individual inference,
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
		
		Set<I> individuals = new HashSet<I>();
		if (positives != null)
			individuals.addAll(positives);
		if (negatives != null)
			individuals.addAll(negatives);
		if (neutrals != null)
			individuals.addAll(neutrals);
		
		double sS = components.size();
		
		InformationTheoryUtils<I> itu = new InformationTheoryUtils<I>(this.inference);
		double sum = 0.0;
		
		for (Description X : components) {
			for (Description Y : components) {
				sum += itu.I(X, Y, individuals);
			}
		}
		
		double ret = (sS > 0.0 ? sum / Math.pow(sS, 2) : Double.POSITIVE_INFINITY);
		return ret;
	}

}
