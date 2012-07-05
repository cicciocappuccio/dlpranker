package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;

import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Thing;
import org.dllearner.utilities.owl.ConceptComparator;

import com.google.common.collect.Sets;

public class ScoreSelection {

	public static final double EPS = 1e-8;
	
	public Set<Description> estrazione(Set<Description> initialSet, Set<Individual> individuals, AbstractScore tScore) {
		Set<Description> ret = Sets.newTreeSet(new ConceptComparator());

		double score = 0.0;
		double previousScore = 0.0;

		do {
			previousScore = score;
			Description bestConcept = Thing.instance;

			for (Description i : initialSet) {
				Set<Description> temp = Sets.newHashSet(ret);
				temp.add(i);
				Double scoreI = tScore.score(temp, individuals);
				if (scoreI != null && scoreI - score > EPS) {
					bestConcept = i;

					System.out.println(scoreI + " - " + score);
					score = scoreI;
//
				}
			}
			if (bestConcept != Thing.instance) {
				ret.add(bestConcept);
				initialSet.remove(bestConcept);
			}
			System.out.println(ret.size() + ": " + bestConcept);
		} while (score - previousScore > EPS);

		return ret;
	}
}
