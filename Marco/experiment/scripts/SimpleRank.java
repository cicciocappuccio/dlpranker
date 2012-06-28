package scripts;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

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
		// TODO Auto-generated method stub

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

		Set<Description> features = fg.getAtomicFeatures();
		Set<Individual> film = dati.getIndividuals();

		Table<Description, Individual, Double> Pi = HashBasedTable.create();

		Inference a = new Inference(cache, reasoner);
		
		for (Description feature : features) {
			for (Individual individual : film) {
				LogicValue b = a.cover(feature, individual);
				Pi.put(feature, individual, (b == LogicValue.TRUE ? 0 : (b == LogicValue.FALSE ? 1 : 0.5)));
			}
		}
		
		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = new HashSet<Individual>(film);
		for (Individual i : film) {
			for (Individual j : toCheck) {
				double sum = 0;
				for (Description feature : features)
					sum += Math.pow(1 - Math.abs(Pi.get(feature, i) - Pi.get(feature, j)), 2);
				sum = Math.sqrt(sum);
				K.put(i, j, sum);
				K.put(j, i, sum);
			}
			toCheck.remove(i);
		}
		
		OnLineKernelPerceptronRanker<Individual> m = new OnLineKernelPerceptronRanker<Individual>(film, K, 5);
		
		List<Individual> filmList = new ArrayList<Individual>(film);
		KFolder<Individual> folder = new KFolder<Individual>(filmList, 10);
		
		for (Individual i : (List<Individual>)folder.getOtherFolds(0))
		{
			for (Individual y : dati.getRatings(i))
			{
				ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingValue(y));
				m.feed(ii);
			}
		}
		
		for (Individual i : (List<Individual>)folder.getFold(0))
		{
			System.out.println(m.rank(i) + " - " + dati.getRatingMode(i));
		}
		
	}
}
