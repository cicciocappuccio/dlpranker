package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class MutualInformationScore extends AbstractScore {

	
	public MutualInformationScore(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner) {
		super(cache, reasoner);
	}

	@Override
	public double score(Set<Description> descriptions, Set<Individual> individuals) {
		
		EIUtils calc = new EIUtils(cache, reasoner);
		
		double sum = 0.0;
		
		for (Description i : descriptions)
			for (Description j : descriptions)
				sum += calc.I(i, j, individuals);
			
		sum = (1.0/((double)descriptions.size()*(double)descriptions.size()))*sum;
		return sum;
	}

}
