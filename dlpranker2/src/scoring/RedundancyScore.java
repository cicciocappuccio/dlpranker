package scoring;


import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.EIUtils;
import utils.Inference;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

@Beta
public class RedundancyScore extends AbstractScore {

	private EIUtils calc;
	
	public RedundancyScore(Inference inference, EIUtils calc2) {
		super(inference);
		this.calc = calc2;
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
		double sum = 0.0;
		for (Description i : descriptions) {
			for (Description j : descriptions) {
				sum += calc.I(i, j, individuals);
			}
		}
		double dS = descriptions.size();
		sum = (dS <= 1.0 + 1e-12 ? 0.0 : (1.0 / (dS)) * sum);
		return sum;
	}

	@Override
	public Double score(Description current, Set<Description> descriptions, Set<Individual> individuals) {
		HashSet<Description> others = Sets.newHashSet(descriptions);
		if (others.contains(current))
			others.remove(current);
		double sum = 0.0;
		for (Description i : others) {
			sum += calc.I(i, current, individuals);
		}
		double dS = others.size();
		sum = (dS <= 0.0 + 1e-12 ? 0.0 : (1.0 / (dS)) * sum);
		return sum;
	}

}