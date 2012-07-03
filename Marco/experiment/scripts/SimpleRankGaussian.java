package scripts;

import features.FakeRefinementOperator;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import perceptron.BatchKernelPerceptronRanker;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import test.KFolder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

public class SimpleRankGaussian {

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
		AbstractConceptCache cache = new VolatileConceptCache(urlOwlFile); //new AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);
		Set<Individual> films = dati.getIndividuals();

		//FeaturesGenerator fg = new FeaturesGenerator(inference, new Psi2Wrapper(reasoner));
		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		Set<Description> filmSubClasses = _fg.getFilteredFilmSubClasses(films, 0.02);
		FeaturesGenerator fg = new FeaturesGenerator(inference, new FakeRefinementOperator(reasoner, filmSubClasses));
		
		Set<Description> features = filmSubClasses; //fg.getMHMRFeatures(films, .9);
		//Set<Description> features = XMLConceptStream.leggi(1);
		//Set<Description> features = XMLConceptStream.leggi(2);

		System.out.println("Features:");
		for (Description f : features) {
			System.out.println("\t" + f);
		}
		
		System.out.println("Creating Pi..");
		
		Table<Description, Individual, Double> Pi = HashBasedTable.create();

		Inference a = new Inference(cache, reasoner);

		for (Description feature : features) {
			for (Individual individual : films) {
				LogicValue b = a.cover(feature, individual);
				Pi.put(feature, individual, (b == LogicValue.TRUE ? 0 : (b == LogicValue.FALSE ? 1 : 0.5)));
			}
		}

		if (cache != null)
			cache.save();
		
		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = new HashSet<Individual>(films);

		System.out.println("Creating Kernel..");

		final boolean paperKernel = false;

		if (paperKernel) {
			for (Individual i : films) {
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
			double featuresWeight = ((double) 1) / ((double) features.size());
			for (Individual i : films) {
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
		
/*
		Individual candidateI = null, candidateJ = null;
		for (Individual xi : K.rowKeySet()) {
			double min = Double.MAX_VALUE;
			for (Individual xj : K.columnKeySet()) {
				if (xi != xj && min > E.get(xi, xj)) {
					min = E.get(xi, xj); candidateI = xi; candidateJ = xj;
				}
			}
			System.out.println("min(" + candidateI + " -> " + candidateJ + "): " + min);
		}
*/

		final int NFOLDS = films.size();
		Map<Double, Double> accuracySigma = Maps.newTreeMap();
		
		for (double sigma = 1e-4; sigma <= 1e4; sigma *= 5.0) {

			System.out.println("Sigma: " + sigma);
			
			K = HashBasedTable.create();
			for (Individual xi : E.rowKeySet()) {
				for (Individual xj : E.columnKeySet()) {
					double sqdist = Math.pow(E.get(xi, xj), 2.0);
					double val = Math.exp(-sqdist / (2.0 * Math.pow(sigma, 2.0)));
					K.put(xi, xj, val);
				}
			}

			List<Individual> filmList = new ArrayList<Individual>(films);
			KFolder<Individual> folder = new KFolder<Individual>(filmList, NFOLDS);

			double right = 0.0;
			double total = 0.0;
			
			for (int j = 0; j < NFOLDS; j++) {
				OnLineKernelPerceptronRanker<Individual> mo = new OnLineKernelPerceptronRanker<Individual>(films, K, 5);

				List<ObjectRank<Individual>> training = new ArrayList<ObjectRank<Individual>>();
				for (Individual i : (List<Individual>) folder.getOtherFolds(j)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingMode(i));
					training.add(ii);
				}

				//System.out.println("Training the perceptron (sigma = " + sigma + ")..");
				for (ObjectRank<Individual> i : training) {
					//System.out.println("Feeding " + i + " to the online algorithm..");
					mo.feed(i);
				}

				for (Individual i : (List<Individual>) folder.getFold(j)) {
					System.out.println("\t(sigma = " + sigma + ") Real: " + dati.getRatingMode(i) + ", Online predicted: " + mo.rank(i));
					if (dati.getRatingMode(i) == mo.rank(i)) {
						right += 1.0;
					}
					total += 1.0;
				}

			}

			
			accuracySigma.put(sigma, right / total);
		}
		
		for (Entry<Double, Double> punteggio : accuracySigma.entrySet()) {
			System.out.println("Sigma: " + punteggio.getKey() + ", Accuracy: " + punteggio.getValue());
		}
	}
}
