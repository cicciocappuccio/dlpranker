package com.neuralnoise.cache;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.core.AbstractKnowledgeSource;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.utilities.owl.ConceptComparator;
import org.dllearner.utilities.owl.ConceptTransformation;

import com.google.common.collect.Maps;

public class ReasonerUtils {
	
	public static AbstractReasonerComponent getReasoner(File fowl) throws Exception {
		ComponentManager cm = ComponentManager.getInstance();
		AbstractKnowledgeSource source = new OWLFile(fowl.toURI().toURL());
		Class<? extends AbstractReasonerComponent> clazz = OWLAPIReasoner.class;
		AbstractReasonerComponent reasoner = cm.reasoner(clazz, source);
		reasoner.init();
		return reasoner;
	}
	
	public static Set<String> individuals(Set<Individual> individuals) {
		Set<String> ret = new TreeSet<String>();
		for (Individual i : individuals) {
			ret.add(i.toString());
		}
		return ret;
	}
	
	public static Set<String> individuals(AbstractReasonerComponent reasoner, Description concept) {
		SortedSet<Individual> _is = reasoner.getIndividuals(concept);
		Set<String> ret = new TreeSet<String>();
		for (Individual _i : _is) {
			ret.add(_i.toString());
		}
		return ret;
	}
	
	public static Description negate(Description concept) {
		return new Negation(concept);
	}

	private static final Map<String, Description> map = Maps.newHashMap();
	
	public static Description normalise(Description concept) {
		Description ret = null;
		String sconcept = concept.toString();
		if (!map.containsKey(sconcept)) {
			try {
				ret = ConceptTransformation.transformToNegationNormalForm(concept);
				ConceptComparator c = new ConceptComparator();
				ConceptTransformation.transformToOrderedForm(ret, c);
				ret = ConceptTransformation.getShortConceptNonRecursive(ret, c);
			} catch (RuntimeException re) {
				//
			}
			if (ret == null) {
				ret = concept;
			}
			map.put(sconcept, ret);
		} else {
			ret = map.get(sconcept);
		}
		return ret;
	}
	
}
