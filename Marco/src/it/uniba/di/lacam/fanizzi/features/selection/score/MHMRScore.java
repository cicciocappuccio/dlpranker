package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class MHMRScore extends AbstractScore {

	private EntropyScore entropy;
	private MutualInformationScore mutualInformation;
	
	private double alpha;

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public MHMRScore(Inference inference, double alpha) {
		super(inference);

		EIUtils calc = new EIUtils(inference);
		
		this.entropy =  new EntropyScore(inference, calc);
		this.mutualInformation = new MutualInformationScore(inference, calc);
		this.alpha = alpha;
		
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
				double ret1 = entropy.score(descriptions, individuals);
				double ret2 = alpha * mutualInformation.score(descriptions, individuals);
				//System.out.println("ret1: " + ret1 + " ret2: " + ret2);	
				Double ret = ret1 - ret2;
				return (Double.isInfinite(ret) || Double.isNaN(ret) ? null : ret);
	}

}
