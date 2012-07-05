package test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;

public class MHMRTest {

	public static void main(String[] srgs) throws Exception {
		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(urlOwlFile);
		// AbstractConceptCache cache = new
		// AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);

		Set<Individual> films = dati.getIndividuals();

		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);

		List<Description> init = Lists.newArrayList(_fg.getFilmSubClasses());
		// _fg.getFilmSubClasses();
		System.out.println(init.size());
		Set<Description> inisieme = Sets.newHashSet(init.get(0));
		
		//inisieme.add(init.get(1));
		System.out.println(inisieme + " " + inisieme.size());
		
		AbstractScore tScore = new MHMRScore(inference.getCache(), inference.getReasoner(), 1.0);
		
		System.out.println(tScore.score(inisieme, films));
		
	}
}
