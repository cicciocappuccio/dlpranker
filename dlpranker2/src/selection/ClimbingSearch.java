package selection;


import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.refinementoperators.RefinementOperator;

import scoring.AbstractScore;

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
		
		Set<Description> _newConceptSet = new HashSet<Description>();
		_newConceptSet.addAll(conceptSet);
		_newConceptSet.add(rootConcept);
		double best = tScore.score(rootConcept, _newConceptSet, individuals);
		
		boolean stop = false;
		do {
			stop = true;
			Set<Description> refinements = r.refine(bestConcept, maxLength);
			
			// ciclo sui figli per sceglierne il migliore
			for (Description refinement : refinements) {
				String str = refinement.toString();
				
				if(str.contains(" OR ") || str.contains("ALL ") || str.contains(" AND ") || str.contains("BOTTOM"))
					continue;
				
				if (!conceptSet.contains(refinement)) {
					
					Set<Description> newConceptSet = new HashSet<Description>();
					newConceptSet.addAll(conceptSet);
					newConceptSet.add(refinement);

					double proposed = tScore.score(refinement, newConceptSet, individuals);

					System.out.println("proposed (" + refinement + ") : " + proposed + ", best (" + bestConcept + ") : " + best);
					
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
