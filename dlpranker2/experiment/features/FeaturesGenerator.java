package features;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.utilities.owl.ConceptComparator;

import scoring.MHMRScore;
import scoring.MRMRScore;
import selection.GreedyForward;
import utils.EIUtils;
import utils.Inference;
import utils.Inference.LogicValue;

import com.google.common.collect.Sets;

public class FeaturesGenerator {

	private Inference inference;
	private RefinementOperator refinement;
	
	public FeaturesGenerator(Inference inference, RefinementOperator refinement) {
		this.inference = inference;
		this.refinement = refinement;
	}
	

	public Set<Description> getFilmSubClasses() {
		Set<Description> ret = Sets.newHashSet();
		Set<String> seen = Sets.newHashSet();
		Queue<Description> queue = new LinkedList<Description>();
		queue.add(new NamedClass("http://dbpedia.org/ontology/Film"));
		queue.add(new NamedClass("http://dbpedia.org/class/yago/Movie106613686"));
		queue.add(new NamedClass("http://schema.org/Movie"));
		while (!queue.isEmpty()) {
			Description p = queue.poll();
			if (!seen.contains(p.toString())) {
				ret.add(p);
				seen.add(p.toString());
				queue.addAll(inference.getReasoner().getSubClasses(p));
			}
		}
		System.out.println("Film subclasses: " + ret.size());
		return ret;
	}
	
	public Set<Description> getFilteredProbabilityFilmSubClasses(Set<Individual> individui, double minProbability) {
		Set<Description> ret = Sets.newTreeSet(new ConceptComparator());
		Set<String> seen = Sets.newHashSet();
		Queue<Description> queue = new LinkedList<Description>();
		queue.add(new NamedClass("http://dbpedia.org/ontology/Film"));
		queue.add(new NamedClass("http://dbpedia.org/class/yago/Movie106613686"));
		queue.add(new NamedClass("http://schema.org/Movie"));
		while (!queue.isEmpty()) {
			Description p = queue.poll();
			if (!seen.contains(p.toString())) {
				ret.add(p);
				seen.add(p.toString());
				queue.addAll(inference.getReasoner().getSubClasses(p));
			}
		}
		
		Set<Description> newRet = Sets.newTreeSet(new ConceptComparator());
		double total = individui.size();
		
		for (Description f : ret) {
			double covered = 0.0;
			for (Individual film : individui)
				if (inference.cover(f, film) == LogicValue.TRUE)
					covered += 1.0;

			double p = covered / total;
			if (p >= minProbability)
				newRet.add(f);
		}
		
		System.out.println("Filtered Film subclasses: " + newRet.size() + " with minProbability: " + minProbability);
		return newRet;
	}
	
	
	public Set<Description> getFilteredEntropyFilmSubClasses(Set<Individual> individui, double minEntropy, EIUtils ei) {
		Set<Description> ret = Sets.newTreeSet(new ConceptComparator());
		Set<String> seen = Sets.newHashSet();
		Queue<Description> queue = new LinkedList<Description>();
		queue.add(new NamedClass("http://dbpedia.org/ontology/Film"));
		queue.add(new NamedClass("http://dbpedia.org/class/yago/Movie106613686"));
		queue.add(new NamedClass("http://schema.org/Movie"));
		while (!queue.isEmpty()) {
			Description p = queue.poll();
			if (!seen.contains(p.toString())) {
				ret.add(p);
				seen.add(p.toString());
				queue.addAll(inference.getReasoner().getSubClasses(p));
			}
		}
		
		Set<Description> newRet = Sets.newTreeSet(new ConceptComparator());
		
		for (Description f : ret) {
			double h = ei.H(f, individui);
			if (h >= minEntropy)
				newRet.add(f);
		}
		
		System.out.println("Filtered Film subclasses: " + newRet.size() + " with minProbability: " + minEntropy);
		return newRet;
	}
	
	public Set<Description> getMHMRFeatures(Set<Individual> individui, MHMRScore tScore, double lambda, int nfeatures) {
		GreedyForward gf = new GreedyForward(inference, refinement, 1, 0.0, nfeatures);
		//AbstractScore tScore = new MHMRScore(inference.getCache(), inference.getReasoner(), lambda);
		tScore.setAlpha(lambda);
		Set<Description> ret = gf.estrazione(Thing.instance, individui, tScore);
		
		return ret;
	}

	public Set<Description> getMRMRFeatures(Set<Individual> individui, MRMRScore tScore, double lambda, int nfeatures) {
		GreedyForward gf = new GreedyForward(inference, refinement, 1, 0.0, nfeatures);
		tScore.setLambda(lambda);
		Set<Description> ret = gf.estrazione(Thing.instance, individui, tScore);
		
		return ret;
	}

}
