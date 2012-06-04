package test;

import it.uniba.di.lacam.fanizzi.features.SpecializeProbable;
import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.HibernateConceptCache;

public class ReasonerTest {

	public static void main(String[] args) throws Exception {

		// PelletOptions.CACHE_RETRIEVAL = true;
		// PelletOptions.KEEP_ABOX_ASSERTIONS = true;
		// PelletOptions.USE_CD_CLASSIFICATION

		Set<Description> mio = rhoDownTest("res/fragmentOntology10.owl");
		// Set<OWLClassExpression> mio = rhoDownTest("test/test/leo.owl");

		String[] mioArray = new String[mio.size()];
		int i = 0;
		for (Description m : mio) {
			mioArray[i++] = m.toString();
			System.out.println(m.toString());
		}
		System.out.println(i);
	}

	public static Set<Description> rhoDownTest(String file) throws Exception {
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		reasoner.init();

		AbstractConceptCache cache = new HibernateConceptCache(file);

		Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
		SortedSet<Individual> films = reasoner.getIndividuals(Film);
		// films.
		List<Individual> filmsList = new ArrayList<Individual>(films);

		KFolder<Individual> kf = new KFolder<Individual>(filmsList, 5,
				new Random(0));

		System.out.println("REASONER LOADED.");
		/*
		 * RhoDRDown op2 = new RhoDRDown(); op2.setUseAllConstructor(false);
		 * op2.setUseNegation(false); op2.setDropDisjuncts(false);
		 * op2.setApplyAllFilter(true); op2.setInstanceBasedDisjoints(false);
		 * op2.setUseCardinalityRestrictions(true);
		 * 
		 * 
		 * op2.setReasoner(reasoner);
		 * op2.setSubHierarchy(reasoner.getClassHierarchy());
		 * op2.setObjectPropertyHierarchy
		 * (reasoner.getObjectPropertyHierarchy());
		 * op2.setDataPropertyHierarchy(
		 * reasoner.getDatatypePropertyHierarchy());
		 * 
		 * op2.init();
		 * 
		 * 
		 * PsiDown op3 = new PsiDown(null, reasoner); op3.init();
		 */
		Description start = Thing.instance;
		Psi2DownWrapper op4 = new Psi2DownWrapper(reasoner);
		op4.init();

		Set<Description> _mio = SpecializeProbable.specialize(reasoner, cache,
				start, new HashSet<Individual>(kf.getFold(1)), op4, 3, 0);

		System.out.println("DONE REFINING.");

		Set<Description> mio = new HashSet<Description>();
		for (Description c : _mio) {
			String str = c.toString();
			if (!str.contains(" AND ") && !str.contains(" OR ")
					&& !str.contains("ALL ")) {
				mio.add(c);
				System.out.println(c);
			}
		}

		// System.out.println(mio);
		System.out.println(mio.size());

		return mio;

	}

}