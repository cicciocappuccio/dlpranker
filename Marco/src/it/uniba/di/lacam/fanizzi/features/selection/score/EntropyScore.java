package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.annotations.Beta;
import com.neuralnoise.cache.AbstractConceptCache;

@Beta
public class EntropyScore extends AbstractScore {
	
	private EIUtils calc;
	
	public EntropyScore(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner, EIUtils calc2) {
		super(cache, reasoner);
		this.calc = calc2;
	}

	@Override
	public Double score (Set<Description> descriptions, Set<Individual> individuals)
	{
		double sum = 0.0;
		
		
		
		for (Description x : descriptions)
		{
			//System.out.println(x);
			sum += calc.H(x, individuals);
		}
		
		//double dS =	descriptions.size();		
		//sum = (1.0 / dS) * sum;
		
		return sum;
	}
}
