package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.annotations.Beta;
import com.neuralnoise.cache.AbstractConceptCache;

@Beta
public class EntropyScore extends AbstractScore {
	
	private EIUtils calc;

	public EntropyScore(Inference inference, EIUtils calc) {
		super(inference);
		this.calc = calc;
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
