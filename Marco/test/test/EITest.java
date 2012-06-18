package test;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.utils.EIUtils;

import java.util.Collections;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

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

		Set<Description> concetti = reasoner.getSubClasses(Thing.instance);
		EIUtils calc = new EIUtils(cache, reasoner);

		System.out.println("concetti: " + concetti.size());
		for (Description i : concetti) {
			System.out.println("\nH(" + i + "): " + calc.H(i, _films));

			int count = 1;
			
			for (Description y : concetti) {
				if ((count++ % 8) != 0)
					System.out.printf("	" + calc.I(i, y, _films));
				else
					System.out.printf("	" + calc.I(i, y, _films) + "\n");
			}
		}
	}
}
