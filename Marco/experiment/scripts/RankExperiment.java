package scripts;

import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;
import it.uniba.di.lacam.fanizzi.utils.CSVWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import kernels.GaussianKernel;
import kernels.ParamsScore;
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
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

public class RankExperiment {

	public static final int NFOLDS = 10;

	public static void main(String[] args) throws Exception {

		List<Double> lmae = Lists.newArrayList();
		List<Double> gmae = Lists.newArrayList();
		List<Double> lrmse = Lists.newArrayList();
		List<Double> grmse = Lists.newArrayList();
		List<Double> lscc = Lists.newArrayList();
		List<Double> gscc = Lists.newArrayList();
		List<Double> laccuracy = Lists.newArrayList();
		List<Double> gaccuracy = Lists.newArrayList();

		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(urlOwlFile); // new
																			// AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);
		Set<Individual> films = dati.getIndividuals();

		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);

		for (double _p = 0.00; _p <= 0.2; _p += 0.01) {

			Set<Description> features = _fg.getFilteredFilmSubClasses(films, _p);
			// FeaturesGenerator fg = new FeaturesGenerator(inference, new
			// FakeRefinementOperator(reasoner, filmSubClasses));

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

			ErrorMetric accuracy = new Accuracy(); // da utilizzare per best
													// sigma

			ErrorMetric mae = new MAE();
			ErrorMetric rmse = new RMSE();
			ErrorMetric scc = new SpearmanCorrelationCoefficient();

			GaussianKernel<Individual> gk = new GaussianKernel<Individual>(films, E);

			final int nfolds = Math.min(NFOLDS, films.size());

			List<Individual> filmList = new ArrayList<Individual>(films);
			KFolder<Individual> folder = new KFolder<Individual>(filmList, nfolds);

			double maeLErr = 0.0;
			double maeGErr = 0.0;
			double rmseLErr = 0.0;
			double rmseGErr = 0.0;
			double sccLErr = 0.0;
			double sccGErr = 0.0;
			double accuracyLErr = 0.0;
			double accuracyGErr = 0.0;

			for (int j = 0; j < nfolds; j++) {
				List<ObjectRank<Individual>> training = Lists.newArrayList();

				for (Individual i : folder.getOtherFolds(j)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingMode(i));
					training.add(ii);
				}

				SortedSet<ParamsScore> ps = gk.getParameters(training, accuracy);

				Double sigma = ps.first().getParams().get("Sigma");
				System.out.println("Best parameters found: " + ps.first());

				for (ParamsScore p : ps) {
					System.out.println("\t" + p);
				}

				Table<Individual, Individual, Double> GK = gk.calculate(sigma);

				OnLineKernelPerceptronRanker<Individual> lmo = new OnLineKernelPerceptronRanker<Individual>(films, K, 5);
				OnLineKernelPerceptronRanker<Individual> gmo = new OnLineKernelPerceptronRanker<Individual>(films, GK, 5);

				for (ObjectRank<Individual> i : training) {
					lmo.feed(i);
					gmo.feed(i);
				}

				List<Integer> reals = Lists.newLinkedList();

				List<Integer> lpredicted = Lists.newLinkedList();
				List<Integer> gpredicted = Lists.newLinkedList();

				for (Individual t : folder.getFold(j)) {
					reals.add(dati.getRatingMode(t));
					lpredicted.add(lmo.rank(t));
					gpredicted.add(gmo.rank(t));
				}

				maeLErr += mae.error(reals, lpredicted);
				maeGErr += mae.error(reals, gpredicted);
				rmseLErr += rmse.error(reals, lpredicted);
				rmseGErr += rmse.error(reals, gpredicted);
				sccLErr += scc.error(reals, lpredicted);
				sccGErr += scc.error(reals, gpredicted);
				accuracyLErr += accuracy.error(reals, lpredicted);
				accuracyGErr += accuracy.error(reals, gpredicted);

			}

			double dnfolds = nfolds;

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + maeLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + maeGErr / dnfolds);
			lmae.add(maeLErr / dnfolds);
			gmae.add(maeGErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + maeLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + maeGErr / dnfolds);
			lrmse.add(rmseLErr / dnfolds);
			grmse.add(rmseGErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + maeLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + maeGErr / dnfolds);
			lscc.add(sccLErr / dnfolds);
			gscc.add(sccGErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + maeLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + maeGErr / dnfolds);
			laccuracy.add(accuracyLErr / dnfolds);
			gaccuracy.add(accuracyGErr / dnfolds);

		}
		
		CSVWriter csv = new CSVWriter("res/risultati.csv");

		csv.append("Linear MAE");
		csv.append("Gaussian MAE");
		csv.append("Linear RMSE");
		csv.append("Gaussian RMSE");
		csv.append("Linear Spearman Correlation Coefficient");
		csv.append("Gaussian Spearman Correlation Coefficient");
		csv.append("Linear Accuracy");
		csv.append("Gaussian Accuracy");
		csv.newRow();

		for (int i = 0; i < Math.min(NFOLDS, films.size()); i++) {
			csv.append(lmae.get(i).toString());
			csv.append(gmae.get(i).toString());

			csv.append(lrmse.get(i).toString());
			csv.append(grmse.get(i).toString());

			csv.append(lscc.get(i).toString());
			csv.append(gscc.get(i).toString());

			csv.append(laccuracy.get(i).toString());
			csv.append(gaccuracy.get(i).toString());
			csv.newRow();
		}
	}
}
