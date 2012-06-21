package test;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.GreedyForward;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;

import java.util.Collections;
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

public class ClimbingSearchTest {

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
		int i = 0;
		Iterator<Individual> it = _films.iterator();
		while (it.hasNext() && i++ < 10)
			films.add(it.next());

		for (Individual y : films)
			System.out.println(y);

		System.out.println("Films selezionati\nchiamo GreedyForward");

		AbstractScore tScore = new MHMRScore(cache, reasoner, 0.8);

		GreedyForward pioniere = new GreedyForward(cache, reasoner, r,
				maxLength);

		Set<Description> risultato = pioniere.estrazione(Thing.instance, films,
				tScore);

		for (Description j : risultato)
			System.out.println(j);

	
	}
}
