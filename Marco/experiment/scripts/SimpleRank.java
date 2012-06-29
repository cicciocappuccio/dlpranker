package scripts;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.psi.Psi2Wrapper;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
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

		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);
		FeaturesGenerator fg = new FeaturesGenerator(inference, new Psi2Wrapper(reasoner));

		Set<Description> features = fg.getExistentialFeatures();

		//Set<Description> features = XMLConceptStream.leggi(1);
		//Set<Description> features = XMLConceptStream.leggi(2);

		Set<Individual> film = dati.getIndividuals();

		System.out.println("Creating Pi..");
		
		Table<Description, Individual, Double> Pi = HashBasedTable.create();

		Inference a = new Inference(cache, reasoner);

		for (Description feature : features) {
			for (Individual individual : film) {
				LogicValue b = a.cover(feature, individual);
				Pi.put(feature, individual, (b == LogicValue.TRUE ? 0 : (b == LogicValue.FALSE ? 1 : 0.5)));
			}
		}

		if (cache != null)
			cache.save();
		
		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = new HashSet<Individual>(film);

		System.out.println("Creating Kernel..");

		final boolean paperKernel = true;

		if (paperKernel) {
			for (Individual i : film) {
				for (Individual j : toCheck) {
					double sum = 0;
					for (Description feature : features)
						sum += Math.pow(1 - Math.abs((Pi.get(feature, i) - Pi.get(feature, j))), 2);
					sum = (Math.sqrt(sum));
					K.put(i, j, sum);
					K.put(j, i, sum);
				}
				toCheck.remove(i);
			}
		} else {

			// FANIZZI
			double featuresWeight = ((double) 1) / ((double) features.size());
			for (Individual i : film) {
				for (Individual j : toCheck) {
					double sum = 0;
					for (Description feature : features)
						sum += Math.pow(Math.abs(featuresWeight * ((double) (Pi.get(feature, i) - Pi.get(feature, j)))), 2);
					sum = 1 - (Math.sqrt(sum) / (double) (features.size() * 2));
					K.put(i, j, sum);
					K.put(j, i, sum);
				}
				toCheck.remove(i);
			}

		//						FINE FANIZZI
		}
		
		//System.out.println(K);
		
		Table<Individual, Individual, Double> E = HashBasedTable.create();
		for (Individual xi : K.rowKeySet()) {
			double Kii = K.get(xi, xi);
			for (Individual xj : K.columnKeySet()) {
				double Kjj = K.get(xj, xj);
				E.put(xi, xj, Math.sqrt(- K.get(xi, xj) + 0.5 * (Kii + Kjj)));
			}
		}
		
		
		Individual candidateI = null, candidateJ = null;
		
		for (Individual xi : K.rowKeySet()) {
			double min = Double.MAX_VALUE;
			for (Individual xj : K.columnKeySet()) {
				if (xi != xj && min > E.get(xi, xj))
				{
					min = E.get(xi, xj);
					candidateI = xi;
					candidateJ = xj;
				}
			}
			System.out.println("min(" + candidateI + " -> " + candidateJ + "): " + min);
		}
		
		
		
		
		
		//BatchKernelPerceptronRanker<Individual> m = new BatchKernelPerceptronRanker<Individual>(film, K, 5);

		List<Individual> filmList = new ArrayList<Individual>(film);
		KFolder<Individual> folder = new KFolder<Individual>(filmList, 10);
		
		for (int j = 0; j < 10; j++) {
			OnLineKernelPerceptronRanker<Individual> m = new OnLineKernelPerceptronRanker<Individual>(film, K, 5);
			
			List<ObjectRank<Individual>> training = new ArrayList<ObjectRank<Individual>>();
			for (Individual i : (List<Individual>) folder.getOtherFolds(j)) {
/*
 				for (Individual y : dati.getRatings(i)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingValue(y));
					lista.add(ii);
				}
*/
				ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingMode(i));
				training.add(ii);
			}

			System.out.println("Training the perceptron..");
			
			for (ObjectRank<Individual> i : training) {
				System.out.println("Feeding " + i + " to the online algorithm..");
				m.feed(i);
			}
				
/**/			
//			m.kernelPerceptronRank(lista);

			for (Individual i : (List<Individual>) folder.getFold(j)) {
				System.out.println(i + " - Predicted: " + m.rank(i) + " - Real: " + dati.getRatingMode(i));
			}
			
			System.exit(0);
			
		}
	}
}
