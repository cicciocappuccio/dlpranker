package test;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class EITest {
	public static void main(String[] args) throws Exception {
		String file = "res/fragmentOntology10.owl";

		int maxLength = 3;

		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		reasoner.init();

		AbstractConceptCache cache = new AsynchronousHibernateConceptCache(file);

		Psi2DownWrapper r = new Psi2DownWrapper(reasoner);
		r.init();

		System.out.println("Reasoner creato");

		Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
		Set<Individual> _films = reasoner.getIndividuals(Film);

		Set<Individual> films = Sets.newHashSet();
		int m = 0;
		Iterator<Individual> it = _films.iterator();
		while (it.hasNext() && m++ < 10)
			films.add(it.next());

		Set<Description> concetti = reasoner.getSubClasses(Thing.instance);
		Inference inference = new Inference(cache, reasoner);
		EIUtils calc = new EIUtils(inference);

		System.out.println("concetti: " + concetti.size());
		/*
		 * int y = 0; for (Description i : concetti) {
		 * 
		 * Description negatedNormalised = ReasonerUtils .normalise(new
		 * Negation(i)); System.out.println("\nH(" + i + "): " + calc.H(i,
		 * _films)); for (Individual n : films) {
		 * System.out.println(cache.get(i, n));
		 * System.out.println(cache.get(negatedNormalised, n));
		 * 
		 * }
		 * 
		 * System.in.read();
		 * 
		 * }
		 */

		Set<Individual> usali = new HashSet<Individual>(_films);
		
		for (Description i : concetti) {
			System.out.println("\nH(" + i + "): " + calc.H(i, usali));

			int count = 1;

			for (Description y : concetti) {
				if ((count++ % 8) != 0)
					System.out.printf("	" + calc.I(i, y, usali));
				else
					System.out.printf("	" + calc.I(i, y, usali) + "\n");
			}
		}

	}
}
