package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;

import java.io.IOException;
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

			// ciclo sui figli per sceglierne il migliore
			for (Description refinement : refinements) {
				Set<Description> newConceptSet = new HashSet<Description>();
				newConceptSet.addAll(conceptSet);
				newConceptSet.add(refinement);

				if (refinement.toString() == "(NOT http://dbpedia.org/class/yago/English-languAgeFilms)") {
					System.out.println("conceptSet: " + conceptSet);
					System.out.println("individuals: " + individuals);

					try {
						System.in.read();
						System.in.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				double proposed = tScore.score(newConceptSet, individuals);

				// System.out.println("refinement: " + refinement +
				// " with score: " + proposed + " best: " + best + " S: " +
				// (newConceptSet.size()));

				if (proposed > best) {
					bestConcept = refinement;

					// System.out.println("    True, best concept is: " +
					// bestConcept);
					// System.out.println("Press <Enter> to continue =)))");

					best = proposed;
					stop = false;
				}

			}

		} while (!stop);

		return bestConcept;

	}
}
