package it.uniba.di.lacam.fanizzi.features.incrementalforwardfeatureselection;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neuralnoise.example.AbstractInstance;
import com.neuralnoise.feature.AbstractFeatureContent;
import com.neuralnoise.search.AbstractLearning;
import com.neuralnoise.search.AbstractModel;
import com.neuralnoise.search.local.AbstractLocalSearch;
import com.neuralnoise.search.utils.ContentsScore;
import com.neuralnoise.search.utils.ModelScore;

public class GreedyForwardSelection<I extends Individual> extends AbstractGlobalSearch<I> {
	
	private static final Logger log = LoggerFactory.getLogger(GreedyForwardSelection.class);
	
	protected AbstractLocalSearch<I> searcher;
	
	public GreedyForwardSelection(AbstractLocalSearch<I> searcher,
			int arity, Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		super(arity, positives, negatives, neutrals);
		this.searcher = searcher;
	}
	
	public ModelScore learn() throws Exception {
		AbstractLearning<I> learner = searcher.getLearning();
		
		Set<Description> newFeatures = new HashSet<Description>();
		AbstractModel newModel = learner.learn(arity, newFeatures, positives, negatives, neutrals);
		ModelScore newModelScore = new ModelScore(newModel, searcher.getScoringFunction().score(newModel));

		Set<Description> prevFeatures = null;
		ModelScore prevModelScore = null;
		
		Double gain = 0.0;
		
		do {
			prevFeatures = newFeatures;
			prevModelScore = newModelScore;
			
			ContentsScore found = searcher.search(arity, prevFeatures, Thing.instance);
			newFeatures = found.getContents();
			
			newModel = learner.learn(arity, newFeatures, positives, negatives, neutrals);
			newModelScore = new ModelScore(newModel, searcher.getScoringFunction().score(newModel));
			
			log.info("Proposed feature set: " + newFeatures + ", score: " + newModelScore.getScore());
			
			gain = newModelScore.getScore() - prevModelScore.getScore();
		} while (gain > 0.0);
		
		return prevModelScore;
	}
	
}
