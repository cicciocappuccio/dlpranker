package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.refinementoperators.RefinementOperator;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.AbstractConceptCache;

public class GreedyForward {

	protected Inference inference;
	private RefinementOperator r;
	private int maxLength;
	
	public static final double EPS = 1e-8;
	private double eps = EPS;
	
	public GreedyForward(Inference inference, RefinementOperator r, int maxLength, double eps) {
		super();
		this.inference = inference;
		this.r = r;
		this.maxLength = maxLength;
		this.eps = eps;
	}

	public Set<Description> estrazione(Description rootConcept, Set<Individual> individuals, AbstractScore tScore) {

		ClimbingSearch scalatore = new ClimbingSearch(r, maxLength, tScore);

		Set<Description> conceptSet = new HashSet<Description>();

		double score = 0.0, previousScore;

		System.out.println("Inizia ciclo");

		Description candidate = null;
		do {
			previousScore = score;
			
			if (candidate != null)
				conceptSet.add(candidate);
			
			Set<Description> newConceptSet = Sets.newHashSet(conceptSet);
			Description _candidate = scalatore.extraction(rootConcept, newConceptSet, individuals);
			newConceptSet.add(_candidate);
			
			score = tScore.score(newConceptSet, individuals);
			System.out.println("estrazione: " + _candidate + " with score: " + score + " previousScore: " + previousScore);
			
			candidate = _candidate;
		} while (score - previousScore > eps);

		return conceptSet;
	}

}
