package it.uniba.di.lacam.fanizzi.features.incrementalforwardfeatureselection;

import it.uniba.di.lacam.fanizzi.features.ie.inf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.refinementoperators.RefinementOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class HillClimbingSearch<I extends AbstractInstance> extends AbstractLocalSearch<I> {

	private static final Logger log = LoggerFactory.getLogger(HillClimbingSearch.class);
	
	private RefinementOperator refinementOperator;
	
	private AbstractScoring<I> scoringFunction;
	private AbstractLearning<I> learning;
	
	// Introduced feature concepts may take any set of values in this set.
	private Set<Set<inf>> possibleValueSets;
	
	private int depth;
	private int maxLength;
	
	public HillClimbingSearch(RefinementOperator refinementOperator, 
			AbstractScoring<I> scoringFunction, AbstractLearning<I> learning, int depth, int maxLength) {
		this(refinementOperator, scoringFunction, learning, depth, maxLength, null);
	}
	
	public HillClimbingSearch(RefinementOperator refinementOperator, 
			AbstractScoring<I> scoringFunction, AbstractLearning<I> learning,
			int depth, int maxLength, Set<Set<inf>> possibleValueSets) {
		this.refinementOperator = refinementOperator;
		this.scoringFunction = scoringFunction;
		this.learning = learning;
		this.depth = depth;
		this.maxLength = maxLength;
		this.possibleValueSets = possibleValueSets;
		if (this.possibleValueSets == null) {
			this.possibleValueSets = Sets.newHashSet();
			Set<inf> defvs = Sets.newHashSet();
			defvs.add(inf.TRUE);
			defvs.add(inf.FALSE);
			possibleValueSets.add(defvs);
		}
	}
	
	public ContentsScore search(int arity, Set<AbstractFeatureContent> contents, Description start) throws Exception {
		// Best concept ever found!
		Description bestConcept = start;
		ContentsScore best = scoreConcept(arity, contents, bestConcept);
		// What to refine in next iteration
		List<Description> knownRefinements = Lists.newLinkedList();
		boolean stop = false;
		do {
			int _length = bestConcept.getLength() + depth;
			stop = true;
			Set<Description> refinements = this.refinementOperator.refine(bestConcept, Math.min(_length, maxLength), knownRefinements);
			// Let's iterate over refinements to pick the best
			for (Description refinement : refinements) {
				// Score of this individual refinement
				ContentsScore proposed = scoreConcept(arity, contents, refinement);
				log.debug("Locally proposed concept: " + proposed.getContents() + ", score: " + proposed.getScore());
				// Let's compare it against the best concept ever found
				if (proposed.getScore() > best.getScore()) {
					bestConcept = refinement;
					best = proposed;
					stop = false;
				}
			}
		} while (!stop);		
		return best;
	}

	public ContentsScore searchOrig(int arity, Set<AbstractFeatureContent> contents, Description start) throws Exception {
		
		// Best concept ever found!
		Description bestConcept = start;
		ContentsScore best = scoreConcept(arity, contents, bestConcept);
		
		// What to refine in next iteration
		Description refConcept = bestConcept;
		ContentsScore ref = best;
		
		List<Description> knownRefinements = Lists.newLinkedList();
		
		do {
			int _length = refConcept.getLength() + depth;
			Set<Description> refinements =
					this.refinementOperator.refine(refConcept, Math.min(_length, maxLength), knownRefinements);
			
			// I still don't know what to refine next
			refConcept = null;
			ref = null;
			
			// Let's iterate over refinements to pick the best
			for (Description refinement : refinements) {
				// Score of this individual refinement
				ContentsScore proposed = scoreConcept(arity, contents, refinement);
				
				log.debug("Locally proposed concept: " + proposed.getContents() + ", score: " + proposed.getScore());
				
				// Let's compare it against the best concept ever found
				if (proposed.getScore() > best.getScore()) {
					bestConcept = refinement;
					best = proposed;
				}
				
				// Let's compare it with the best concept to refine next
				if (refConcept == null || proposed.getScore() > ref.getScore()) {
					refConcept = refinement;
					ref = proposed;
				}
			}
		} while (refConcept != null);
		
		return best;
	}
	
	private ContentsScore scoreConcept(int arity, Set<AbstractFeatureContent> originalContents, Description concept) throws Exception {
		ContentsScore best = null;
		for (int position = 0; position < arity; ++position) {
			for (Set<inf> pv : this.possibleValueSets) {
				AbstractFeatureContent fcs = new FeatureConcept(pv, position, concept);
				Set<AbstractFeatureContent> contents = new HashSet<AbstractFeatureContent>(originalContents);
				contents.add(fcs);
				ContentsScore score = scoreContents(arity, contents);
				if (best == null || score.getScore() > best.getScore()) {
					best = score;
				}
			}
		}
		return best;
	}
	
	private ContentsScore scoreContents(int arity, Set<AbstractFeatureContent> contents) throws Exception {
		AbstractModel newModel = this.learning.learn(arity, contents,
				this.scoringFunction.getPositives(), this.scoringFunction.getNegatives(), this.scoringFunction.getNeutrals());
		Double score = this.scoringFunction.score(newModel);
		ContentsScore ret = new ContentsScore(contents, score);
		return ret;
	}

	public AbstractScoring<I> getScoringFunction() {
		return scoringFunction;
	}

	public AbstractLearning<I> getLearning() {
		return learning;
	}
	
}
