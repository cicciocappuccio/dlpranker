package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.selection.EIUtils;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class MutualInformationScore extends Score {

//	private double alpha;
	
	public MutualInformationScore(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner, double alpha) {
		super(cache, reasoner);
		// TODO Auto-generated constructor stub
		
//		this.alpha = alpha;
	}

	@Override
	public double score(Set<Description> descriptions, Set<Individual> individuals) {
		
		EIUtils calc = new EIUtils(cache, reasoner);
		
		double sum = 0.0;
		
		for (Description i : descriptions)
			for (Description j : descriptions)
				sum += calc.I(i, j, individuals);
				
		
		return sum;
	}

}
