package scoring;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.EIUtils;
import utils.Inference;

import com.google.common.annotations.Beta;

@Beta
public class EntropyScore extends AbstractScore {
	
	private EIUtils calc;

	public EntropyScore(Inference inference, EIUtils calc) {
		super(inference);
		this.calc = calc;
	}

	@Override
	public Double score (Set<Description> descriptions, Set<Individual> individuals) {
		double sum = 0.0;
		for (Description x : descriptions) {
			sum += calc.H(x, individuals);
		}
		return sum;
	}

	@Override
	public Double score(Description current, Set<Description> descriptions, Set<Individual> individuals) {
		return calc.H(current, individuals);
	}

}
