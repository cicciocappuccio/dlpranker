package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.refinementoperators.RefinementOperator;

public class ClimbingSearch {

	private RefinementOperator r;
	private int maxLength;
	private AbstractScore tScore;

	public ClimbingSearch(RefinementOperator r, int maxLength,
			AbstractScore tScore) {
		super();

		this.r = r;
		this.maxLength = maxLength;
		this.tScore = tScore;
	}

	public Description extraction(Description rootConcept,
			Set<Description> conceptSet, Set<Individual> individuals) {

		Description bestConcept = rootConcept;
		double best;
		if (conceptSet.isEmpty())
			best = 0.0;
		else {
			best = tScore.score(conceptSet, individuals);
		}

		boolean stop = false;
		do {
			stop = true;
			Set<Description> refinements = r.refine(bestConcept, maxLength);

			// System.out.println(refinements.size());

			// ciclo sui figli per sceglierne il migliore
			for (Description refinement : refinements) {

				if (!conceptSet.contains(refinement)) {
					Set<Description> newConceptSet = new HashSet<Description>();
					newConceptSet.addAll(conceptSet);
					newConceptSet.add(refinement);

					double proposed = tScore.score(newConceptSet, individuals);

					if (proposed > best) {
						bestConcept = refinement;

						best = proposed;
						stop = false;
					}
				}
			}
		} while (!stop);
		
		
		
		return bestConcept;
	}
}
