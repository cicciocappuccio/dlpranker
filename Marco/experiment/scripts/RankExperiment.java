package scripts;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;
import it.uniba.di.lacam.fanizzi.utils.CSV;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import kernels.GaussianKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.Accuracy;
import metrics.ErrorMetric;
import metrics.MAE;
import metrics.RMSE;
import metrics.SpearmanCorrelationCoefficient;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

public class RankExperiment {

	public static final int NFOLDS = 10;

	public static void main(String[] args) throws Exception {

		Double lmae;
		Double gmae;
		Double pmae;

		Double lrmse;
		Double grmse;
		Double prmse;
		
		Double lscc;
		Double gscc;
		Double pscc;
		
		Double laccuracy;
		Double gaccuracy;
		Double paccuracy;

		Double hValue;
		
		//
		
		PrintWriter pw = new PrintWriter("res/risultati.csv");
		
		List<String> methods = Lists.newArrayList("Linear", "Gaussian", "Polynomial");
		List<String> headRow = Lists.newArrayList();
		
		headRow.add("p");
		
		for (String method : methods)
		 headRow.add(method + " MAE");

		for (String method : methods)
			 headRow.add(method + " RMSE");
		
		for (String method : methods)
			 headRow.add(method + " Spearman");
	
		CSV.write(pw, headRow);

		//
		
		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(urlOwlFile); 
		//AbstractConceptCache cache = new AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);
		
		Set<Individual> films = dati.getIndividuals();

		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		
		Set<Description> prevFeatures = null, features = null;
		
		for (double _h = 0.7; _h >= 0.0; _h -= 0.05 ) {
			
			hValue = _h;
			
			prevFeatures = features;
			features = _fg.getFilteredEntropyFilmSubClasses(films, _h);
			
			System.out.println("P = " + _h);
			
			if (prevFeatures != null && Sets.intersection(prevFeatures, features).size() == 0)
				continue;
			
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

			Table<Individual, Individual, Double> E = HashBasedTable.create();
			for (Individual xi : K.rowKeySet()) {
				double Kii = K.get(xi, xi);
				for (Individual xj : K.columnKeySet()) {
					double Kjj = K.get(xj, xj);
					E.put(xi, xj, Math.sqrt(-K.get(xi, xj) + 0.5 * (Kii + Kjj)));
				}
			}

			ErrorMetric accuracy = new Accuracy(); // da utilizzare per best sigma

			ErrorMetric mae = new MAE();
			ErrorMetric rmse = new RMSE();
			ErrorMetric scc = new SpearmanCorrelationCoefficient();

			GaussianKernel<Individual> gk = new GaussianKernel<Individual>(films, E);
			PolynomialKernel<Individual> pk = new PolynomialKernel<Individual>(films, K);
			
			final int nfolds = Math.min(NFOLDS, films.size());

			List<Individual> filmList = new ArrayList<Individual>(films);
			KFolder<Individual> folder = new KFolder<Individual>(filmList, nfolds);

			double maeLErr = 0.0;
			double maeGErr = 0.0;
			double maePErr = 0.0;
			
			double rmseLErr = 0.0;
			double rmseGErr = 0.0;
			double rmsePErr = 0.0;
			
			double sccLErr = 0.0;
			double sccGErr = 0.0;
			double sccPErr = 0.0;
			
			double accuracyLErr = 0.0;
			double accuracyGErr = 0.0;
			double accuracyPErr = 0.0;

			for (int j = 0; j < nfolds; j++) {
				List<ObjectRank<Individual>> training = Lists.newArrayList();

				for (Individual i : folder.getOtherFolds(j)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingMode(i));
					training.add(ii);
				}

				SortedSet<ParamsScore> gps = gk.getParameters(training, accuracy);
				SortedSet<ParamsScore> pps = pk.getParameters(training, accuracy);

				Double sigma = gps.first().getParams().get("Sigma");
				System.out.println("Best param for Gaussian kernel: " + gps.first());
				
				Double d = pps.first().getParams().get("D");
				System.out.println("Best param for Polynomial kernel: " + pps.first());

				Table<Individual, Individual, Double> GK = gk.calculate(sigma);
				Table<Individual, Individual, Double> PK = pk.calculate(d);

				OnLineKernelPerceptronRanker<Individual> lmo = new OnLineKernelPerceptronRanker<Individual>(films, K, 5);
				OnLineKernelPerceptronRanker<Individual> gmo = new OnLineKernelPerceptronRanker<Individual>(films, GK, 5);
				OnLineKernelPerceptronRanker<Individual> pmo = new OnLineKernelPerceptronRanker<Individual>(films, PK, 5);

				for (ObjectRank<Individual> i : training) {
					lmo.feed(i);
					gmo.feed(i);
					pmo.feed(i);
				}

				List<Integer> reals = Lists.newLinkedList();

				List<Integer> lpredicted = Lists.newLinkedList();
				List<Integer> gpredicted = Lists.newLinkedList();
				List<Integer> ppredicted = Lists.newLinkedList();

				for (Individual t : folder.getFold(j)) {
					reals.add(dati.getRatingMode(t));
					lpredicted.add(lmo.rank(t));
					gpredicted.add(gmo.rank(t));
					ppredicted.add(pmo.rank(t));
				}

				maeLErr += mae.error(reals, lpredicted);
				maeGErr += mae.error(reals, gpredicted);
				maePErr += mae.error(reals, ppredicted);

				rmseLErr += rmse.error(reals, lpredicted);
				rmseGErr += rmse.error(reals, gpredicted);
				rmsePErr += rmse.error(reals, ppredicted);
				
				sccLErr += scc.error(reals, lpredicted);
				sccGErr += scc.error(reals, gpredicted);
				sccPErr += scc.error(reals, ppredicted);
				
				accuracyLErr += accuracy.error(reals, lpredicted);
				accuracyGErr += accuracy.error(reals, gpredicted);
				accuracyPErr += accuracy.error(reals, ppredicted);
			}

			double dnfolds = nfolds;

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + maeLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + maeGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + maePErr / dnfolds);
			lmae = (maeLErr / dnfolds);
			gmae = (maeGErr / dnfolds);
			pmae = (maePErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + rmseLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + rmseGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + rmsePErr / dnfolds);
			lrmse = (rmseLErr / dnfolds);
			grmse = (rmseGErr / dnfolds);
			prmse = (rmsePErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + sccLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + sccGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + sccPErr / dnfolds);
			lscc = (sccLErr / dnfolds);
			gscc = (sccGErr / dnfolds);
			pscc = (sccPErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + accuracyLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + accuracyGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + accuracyPErr / dnfolds);


			List<String> row = Lists.newLinkedList();
			
			row.add(hValue.toString());
			
			row.add(lmae.toString());
			row.add(gmae.toString());
			row.add(pmae.toString());

			row.add(lrmse.toString());
			row.add(grmse.toString());
			row.add(prmse.toString());

			row.add(lscc.toString());
			row.add(gscc.toString());
			row.add(pscc.toString());
			
			CSV.write(pw, row);
		}
	}
}
