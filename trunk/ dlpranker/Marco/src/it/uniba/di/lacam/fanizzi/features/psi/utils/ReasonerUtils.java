package it.uniba.di.lacam.fanizzi.features.psi.utils;

import java.io.File;
import java.util.Comparator;
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

/**
 * @author Marco
 *
 */
/**
 * @author Marco
 *
 */
public class ReasonerUtils {
	
	public static AbstractReasonerComponent getReasoner(File fowl) throws Exception {
		return getReasoner(fowl, false);
	}
	
	public static AbstractReasonerComponent getReasoner(File fowl, boolean extended) throws Exception {
		ComponentManager cm = ComponentManager.getInstance();
		AbstractKnowledgeSource source = new OWLFile(fowl.toURI().toURL());
		Class<? extends AbstractReasonerComponent> clazz = OWLAPIReasoner.class;
		AbstractReasonerComponent reasoner = cm.reasoner(clazz, source);
		reasoner.init();
		return reasoner;
	}

/*	public static Logic.Value cover(AbstractReasonerComponent reasoner, Description concept, Individual individual) {
		Logic.Value ret = Logic.Value.UNKNOWN;
		if (reasoner.hasType(concept, individual)) {
			ret = Logic.Value.TRUE;
		} else if (reasoner.hasType(negate(concept), individual)) {
			ret = Logic.Value.FALSE;
			
		}
		return ret;
	}*/
	
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
	
/*	public static SortedSet<Individual> covered(AbstractReasonerComponent reasoner, Description concept, Set<Individual> individuals) {
		return(covered(reasoner, concept, individuals, null));
	}*/
	
/*	public static SortedSet<Individual> covered(AbstractReasonerComponent reasoner, Description concept, Set<Individual> individuals, AbstractConceptCache cache) {
		SortedSet<Individual> ret = null;
		if (cache == null) {
			ret = reasoner.hasType(concept, individuals);
		} else {
			ret = new TreeSet<Individual>();
			SortedSet<Individual> unk = new TreeSet<Individual>();
			for (Individual i : individuals) {
				boolean c = cache.contains(concept, i);
				if (c == false) {
					unk.add(i);
				} else if (c == true) {
					ret.add(i);
				}
			}
			if (unk.size() > 0) {
				ret.addAll(reasoner.hasType(concept, unk));
			}
		}
		return ret;
	}
	*/
	public static Description negate(Description concept) {
		return new Negation(concept);
	}

	public static Description normalise(Description concept) {
		Description ret = null;
		try {
			ret = ConceptTransformation.transformToNegationNormalForm(concept);
		} catch (RuntimeException re) {
			//
		}	
		if (ret == null) {
			ret = concept;
		}
		Comparator<Description> c = new ConceptComparator();
		ConceptTransformation.transformToOrderedForm(ret, c);
		return ret;
	}
	
}
