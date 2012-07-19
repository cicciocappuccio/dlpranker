package scoring;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.EIUtils;
import utils.Inference;

public class MHMRScore extends AbstractScore {

	private EntropyScore entropy;
	private RedundancyScore mutualInformation;
	
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
		this.mutualInformation = new RedundancyScore(inference, calc);
		this.alpha = alpha;
		
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
		double ret1 = entropy.score(descriptions, individuals);
		double ret2 = alpha * mutualInformation.score(descriptions, individuals);
		// System.out.println("ret1: " + ret1 + " ret2: " + ret2);
		Double ret = ret1 - ret2;
		return (Double.isInfinite(ret) || Double.isNaN(ret) ? null : ret);
	}

	@Override
	public Double score(Description current, Set<Description> descriptions, Set<Individual> individuals) {
		double ret1 = entropy.score(current, descriptions, individuals);
		double ret2 = alpha * mutualInformation.score(current, descriptions, individuals);
		// System.out.println("ret1: " + ret1 + " ret2: " + ret2);
		Double ret = ret1 - ret2;
		return (Double.isInfinite(ret) || Double.isNaN(ret) ? null : ret);
	}

}
