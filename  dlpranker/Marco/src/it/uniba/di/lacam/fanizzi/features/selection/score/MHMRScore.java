package it.uniba.di.lacam.fanizzi.features.selection.score;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class MHMRScore extends Score {

	private EntropyScore entropy;
	private MutualInformationScore mutualInformation;
	
	private double alpha;

	public MHMRScore(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner, EntropyScore entropy,
			MutualInformationScore mutualInformation, double alpha) {
		super(cache, reasoner);
		this.entropy = entropy;
		this.mutualInformation = mutualInformation;
		this.alpha = alpha;
	}

	@Override
	public double score(Set<Description> descriptions,
			Set<Individual> individuals) {
				
		return entropy.score(descriptions, individuals)-alpha*mutualInformation.score(descriptions, individuals);
	}

}
