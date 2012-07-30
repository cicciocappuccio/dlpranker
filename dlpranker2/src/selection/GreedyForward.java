package selection;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.refinementoperators.RefinementOperator;

import scoring.AbstractScore;
import utils.Inference;

import com.google.common.collect.Sets;

public class GreedyForward {

	protected Inference inference;
	private RefinementOperator r;
	private int maxLength;
	
	public static final double EPS = 1e-8;
	private double eps = EPS;
	private int nfeatures;
	
	public GreedyForward(Inference inference, RefinementOperator r, int maxLength, double eps, int nfeatures) {
		super();
		this.inference = inference;
		this.r = r;
		this.maxLength = maxLength;
		this.eps = eps;
		this.nfeatures = nfeatures;
	}

	public GreedyForward(Inference inference, RefinementOperator r, int maxLength, double eps) {
		this(inference, r, maxLength, eps, -1);
	}
	
	public Set<Description> estrazione(Description rootConcept, Set<Individual> individuals, AbstractScore tScore) {

		ClimbingSearch scalatore = new ClimbingSearch(r, maxLength, tScore);
		Set<Description> conceptSet = Sets.newHashSet();
		double score = 0.0, previousScore = score;
		Description candidate = null;
		
		boolean stop = false;
		
		do {
			previousScore = score;
			
			if (candidate != null)
				conceptSet.add(candidate);
			
			Set<Description> newConceptSet = Sets.newHashSet(conceptSet);
			
			Description _candidate = scalatore.extraction(rootConcept, newConceptSet, individuals);
			newConceptSet.add(_candidate);
			
			score = tScore.score(_candidate, newConceptSet, individuals);
			System.out.println("estrazione: " + _candidate + " with score: " + score + " previousScore: " + previousScore);
			
			candidate = _candidate;
			
			stop = (this.nfeatures < 0 ? score - previousScore <= eps : conceptSet.size() == this.nfeatures);
		} while (!stop);

		return conceptSet;
	}

}
