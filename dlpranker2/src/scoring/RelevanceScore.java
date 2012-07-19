package scoring;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.EIUtils;
import utils.Inference;

import com.google.common.collect.Multimap;

public class RelevanceScore extends AbstractScore  {

	private Inference inference;
	private Multimap<Integer, Individual> h;
	private EIUtils calc;
	
	public RelevanceScore(Inference inference, Multimap<Integer, Individual> h, EIUtils calc2) {
		super(inference);
		this.inference = inference;
		this.h = h;
		this.calc = calc2;
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
		double s = descriptions.size();				// che devo fare? devo dividere per s o no?
		
		double sum = 0.0;
		
		for (Description i : descriptions) {
			sum += calc.I(h, i, individuals);
		}
		
		double ret = sum;
		
		return ret;
	}

	@Override
	public Double score(Description current, Set<Description> descriptions, Set<Individual> individuals) {
		return calc.I(h, current, individuals);
	}
	
}
