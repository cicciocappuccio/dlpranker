package scripts;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;
import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import perceptron.BatchKernelPerceptronRanker;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import test.KFolder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class SimpleRank {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new AsynchronousHibernateConceptCache(
				urlOwlFile);

		Inference inference = new Inference(cache, reasoner);

		FeaturesGenerator fg = new FeaturesGenerator(inference, null);

		// Set<Description> features = fg.getAtomicFeatures();
		Set<Description> features = XMLConceptStream.leggi(1);

		Set<Individual> film = dati.getIndividuals();

		Table<Description, Individual, Double> Pi = HashBasedTable.create();

		Inference a = new Inference(cache, reasoner);

		for (Description feature : features) {
			for (Individual individual : film) {
				LogicValue b = a.cover(feature, individual);
				Pi.put(feature, individual, (b == LogicValue.TRUE ? 0
						: (b == LogicValue.FALSE ? 1 : 0.5)));
			}
		}

		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = new HashSet<Individual>(film);

		double featuresWeight = ((double) 1) / ((double) features.size());

		for (Individual i : film) {
			for (Individual j : toCheck) {
				double sum = 0;
				for (Description feature : features)
					sum += Math.pow(1 - Math.abs((Pi.get(feature, i) - Pi.get(
							feature, j))), 2);
				sum = (Math.sqrt(sum));
				K.put(i, j, sum);
				K.put(j, i, sum);
			}
			toCheck.remove(i);
		}

		// BatchKernelPerceptronRanker<Individual> m = new
		// BatchKernelPerceptronRanker<Individual>(film, K, 5);
		OnLineKernelPerceptronRanker<Individual> m = new OnLineKernelPerceptronRanker<Individual>(
				film, K, 5);

		List<Individual> filmList = new ArrayList<Individual>(film);
		KFolder<Individual> folder = new KFolder<Individual>(filmList, 10);

		List<ObjectRank<Individual>> lista = new ArrayList<ObjectRank<Individual>>();

		for (int j = 0; j < 10; j++) {
			for (Individual i : (List<Individual>) folder.getOtherFolds(j)) {
				for (Individual y : dati.getRatings(i)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i,
							dati.getRatingValue(y));
					lista.add(ii);

				}

			}
/**/
			for (ObjectRank i : lista)
				m.feed(i);
			// m.kernelPerceptronRank(lista);

			for (Individual i : (List<Individual>) folder.getFold(j)) {
				System.out.println(i + " - " + m.rank(i) + " - "
						+ dati.getRatingMode(i));
			}
		}
	}
}
