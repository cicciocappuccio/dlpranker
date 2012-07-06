package it.uniba.di.lacam.fanizzi.features.selection.score;

import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public abstract class AbstractScore {

	protected Inference inference;

	public AbstractScore(Inference inference) {
		this.inference = inference;
	}

	public abstract Double score(Set<Description> descriptions, Set<Individual> individuals);
}
