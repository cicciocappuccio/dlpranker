package features;

import it.uniba.di.lacam.fanizzi.features.selection.GreedyForward;
import it.uniba.di.lacam.fanizzi.features.selection.ScoreSelection;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.utilities.owl.ConceptComparator;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.ReasonerUtils;

public class FeaturesGenerator {

	private Inference inference;
	private RefinementOperator refinement;
	private EIUtils ei;
	
	public FeaturesGenerator(Inference inference, RefinementOperator refinement) {
		this.inference = inference;
		this.refinement = refinement;
		this.ei =  new EIUtils(inference.getCache(), inference.getReasoner());;
	}
	
	public Set<Description> getExistentialFeatures() {
		return _getExistentialFeatures(Thing.instance, refinement, new HashSet<String>());
	}
	
	private Set<Description> _getExistentialFeatures(Description concept, RefinementOperator r, Set<String> _seen) {
		//System.out.println(".. Refining: " + concept);
		Set<Description> ret = Sets.newHashSet();
		Set<Description> refinements = r.refine(concept, 3);
		Set<Description> toRemove = Sets.newHashSet();
		for (Description c : refinements) {
			Description nc = ReasonerUtils.normalise(c);
			String str = c.toString();
			if (_seen.contains(nc.toString()) || c.getLength() > 3 || str.contains(" OR ") || str.contains(" AND ") || str.contains("BOTTOM"))
				toRemove.add(c);
			_seen.add(nc.toString());
		}
		refinements.removeAll(toRemove);
		for (Description c : refinements) {
			ret.addAll(_getExistentialFeatures(c, r, _seen));
			ret.add(c);
		}
		return ret;
	}
	
	public Set<Description> getAtomicFeatures() {
		List<NamedClass> tmp = inference.getReasoner().getAtomicConceptsList();
		Set<Description> ret = Sets.newHashSet();
		Set<String> sret = Sets.newHashSet();
		for (Description t : tmp) {
			Description nt = ReasonerUtils.normalise(t);
			if (!sret.contains(nt.toString())) {
				ret.add(nt);
				sret.add(nt.toString());
			}
		}
		System.out.println("Atomic features were " + tmp.size() + ", now are " + ret.size());
		//ret.addAll(tmp);
		return ret;
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
		
		Set<Description> newRet = Sets.newHashSet();
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
	
	
	public Set<Description> getFilteredEntropyFilmSubClasses(Set<Individual> individui, double minEntropy) {
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
		
		Set<Description> newRet = Sets.newHashSet();
		
		for (Description f : ret) {
			double h = ei.H(f, individui);
			if (h >= minEntropy)
				newRet.add(f);
		}
		
		System.out.println("Filtered Film subclasses: " + newRet.size() + " with minProbability: " + minEntropy);
		return newRet;
	}
	
	public Set<Description> getMHMRFeatures(Set<Individual> individui, double lambda) {
		GreedyForward gf = new GreedyForward(inference.getCache(), inference.getReasoner(), refinement, 1);
		AbstractScore tScore = new MHMRScore(inference.getCache(), inference.getReasoner(), lambda);
		
		Set<Description> ret = gf.estrazione(Thing.instance, individui, tScore);
		
		return ret;
	}

	public Set<Description> getMHMRFeatures(Set<Individual> individui, Set<Description> initialSet, double alpha) {
		ScoreSelection ss = new ScoreSelection();
		AbstractScore tScore = new MHMRScore(inference.getCache(), inference.getReasoner(), alpha);
		
		Set<Description> ret = ss.estrazione(initialSet, individui, tScore);
		
		return ret;
	}
}
