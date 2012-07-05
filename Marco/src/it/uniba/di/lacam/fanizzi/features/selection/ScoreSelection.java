package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;

import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Thing;
import org.dllearner.utilities.owl.ConceptComparator;

import com.google.common.collect.Sets;

public class ScoreSelection {

	public Set<Description> estrazione(Set<Description> initialSet, Set<Individual> individuals, AbstractScore tScore) {
		Set<Description> ret = Sets.newTreeSet(new ConceptComparator());

		double score = 0.0;
		double previousScore = 0.0;

		{
			previousScore = score;
			Description bestConcept = Thing.instance;

			for (Description i : initialSet) {

				double scoreI = 0.0;

				Set<Description> temp = Sets.newHashSet(ret);
				temp.add(i);
				scoreI = tScore.score(temp, individuals);
				if (scoreI > score) {
					bestConcept = i;
					score = scoreI;
				}
			}
			if (bestConcept != Thing.instance)
				ret.add(bestConcept);
		}
		while (score > previousScore);

		return ret;
	}
}
