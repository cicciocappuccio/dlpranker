package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.selection.EIUtils;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class EntropyScore extends Score {
	
	

	public EntropyScore(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner) {
		super(cache, reasoner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double score (Set<Description> descriptions, Set<Individual> individuals)
	{
		double sum = 0.0;
		
		EIUtils calc = new EIUtils(cache, reasoner);
		
		for (Description x: descriptions)
			sum += calc.H(x, individuals);
			
		return sum;
	}
}
