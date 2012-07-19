package scoring;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.Inference;

public abstract class AbstractScore {

	protected Inference inference;

	public abstract Double score(Set<Description> descriptions, Set<Individual> individuals);
	
	public abstract Double score(Description current, Set<Description> descriptions, Set<Individual> individuals);

	public AbstractScore(Inference inference) {
		this.inference = inference;
	}
}
