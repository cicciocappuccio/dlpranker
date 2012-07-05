package it.uniba.di.lacam.fanizzi.features.selection.score;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class MHMR2Score extends AbstractScore {
	private EntropyScore entropy;
	private MutualInformationScore mutualInformation;
	
	private double alpha;

	public MHMR2Score(AbstractConceptCache cache, AbstractReasonerComponent reasoner, double alpha) {
		super(cache, reasoner);

		this.entropy =  new EntropyScore(cache, reasoner);
		this.mutualInformation = new MutualInformationScore(cache, reasoner);
		this.alpha = alpha;
		
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
				double ret1 = entropy.score(descriptions, individuals);
				double ret2 = alpha * mutualInformation.score(descriptions, individuals);
				//System.out.println("ret1: " + ret1 + "                     ret2: " + ret2);	
				Double ret = ret1 - ret2;
				return (Double.isInfinite(ret) || Double.isNaN(ret) ? null : ret);
	}

}
