package it.uniba.di.lacam.fanizzi.features.selection.score;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public abstract class AbstractScore {

	protected AbstractConceptCache cache;
	protected AbstractReasonerComponent reasoner;

	public AbstractScore(AbstractConceptCache cache, AbstractReasonerComponent reasoner) {
		super();
		this.cache = cache;
		this.reasoner = reasoner;
	}

	public abstract double score(Set<Description> descriptions, Set<Individual> individuals);
}
