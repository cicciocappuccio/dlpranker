package it.uniba.di.lacam.fanizzi.features.utils;

import java.util.Set;

import org.dllearner.core.owl.Individual;

public class MEMRScoring<I extends Individual> {

	private Individual inference;
	private double lambda;
	
	public MEMRScoring(Individual inference, double lambda,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		//super(positives, negatives, neutrals);
		this.inference = inference;
		this.lambda = lambda;
	}
	
	public MEMRScoring(Individual inference,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		this(inference, 1.0, positives, negatives, neutrals);
	}
	
	@Override
	public Double score(AbstractModel m) {
		RedundancyScoring<I> red = new RedundancyScoring<I>(inference, positives, negatives, neutrals);
		EntropyScoring<I> ent = new EntropyScoring<I>(inference, positives, negatives, neutrals);
		
		Double ret = ent.score(m) - (lambda * red.score(m));
		return ret;
	}
}
