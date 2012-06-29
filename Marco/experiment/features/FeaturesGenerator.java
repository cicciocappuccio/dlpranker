package features;

import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.refinementoperators.RefinementOperator;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.ReasonerUtils;

public class FeaturesGenerator {

	private Inference inference;
	private RefinementOperator refinement;
	
	public FeaturesGenerator(Inference inference, RefinementOperator refinement) {
		this.inference = inference;
		this.refinement = refinement;
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

	public Set<Description> getFilmSubclasses() {
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
	
}
