package scripts;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import kernels.AbstractKernel.LearningMethod;
import kernels.GaussianKernel;
import kernels.LinearKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.AbstractMetric;
import metrics.AbstractMetric.MetricType;
import metrics.MAE;
import metrics.RMSE;
import metrics.SpearmanCorrelationCoefficient;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import perceptron.AbstractPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRankerSVRank;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import scripts.AbstractRankExperiment.KernelType;
import utils.CSVW;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;
import features.FeaturesGenerator;

public class ExperimentTestSubFilm {

	public static void main(String[] args) throws Exception {

		double lambda = 1.0;
		int nfeatures = -1;

		int nrating = 5;

		LearningMethod[] modes = LearningMethod.values();
		// modes = new KERNEL_MODE[] { KERNEL_MODE.SOFTMARGIN_BATCH };

		String fileName = "res/risultati/ExperimentTestSubFilm.csv";

		CSVW csv = AbstractRankExperiment.getCSV(fileName, "lambda", "nfeatures");

		Inference inference = AbstractRankExperiment.getInference();
		FeaturesGenerator fg = AbstractRankExperiment.getFeaturesGenerator(inference);

		List<Tupla> lista = XMLFilmRatingStream.leggi();
		Set<Description> features = fg.getFilmSubClasses();

		List<Tupla> _utenti = ExperimentDataset.getUsers(lista);

		List<Tupla> utenti = Lists.newArrayList();
		for (Tupla u : _utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, u.getUser());
			if (ratingsUser.size() >= 30) {
				utenti.add(u);
			}
		}

		for (Tupla utente : utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());

			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());

			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, AbstractRankExperiment.NFOLDS);

			for (LearningMethod mode : modes) {
				for (int j = 0; j < AbstractRankExperiment.NFOLDS; j++) {
					List<Tupla> trainingRanks = folder.getOtherFolds(j);

					Multimap<Integer, Individual> multimap = HashMultimap.create();
					List<ObjectRank<Individual>> objectranks = Lists.newLinkedList();

					for (Tupla film : trainingRanks) {
						multimap.put(film.getValue(), film.getFilm());
						ObjectRank<Individual> ii = new ObjectRank<Individual>(film.getFilm(), film.getValue());
						objectranks.add(ii);
					}

					List<Tupla> testRanks = folder.getFold(j);

					System.out.println("Lambda: " + lambda + " numero di features: " + features.size());

					Table<Individual, Individual, Double> K = AbstractRankExperiment.buildKernel(inference, features, filmsUser);

					AbstractPerceptronRanker<Individual> lmo = AbstractRankExperiment.train(KernelType.Linear, mode, AbstractMetric.MetricType.AccuracyError, filmsUser, K, nrating, objectranks);
					AbstractPerceptronRanker<Individual> gmo = AbstractRankExperiment.train(KernelType.Gaussian, mode, AbstractMetric.MetricType.AccuracyError, filmsUser, K, nrating, objectranks);
					AbstractPerceptronRanker<Individual> pmo = AbstractRankExperiment.train(KernelType.Polynomial, mode, AbstractMetric.MetricType.AccuracyError, filmsUser, K, nrating, objectranks);
					;

					List<Integer> reals = Lists.newLinkedList();
					List<Integer> lpredicted = Lists.newLinkedList();
					List<Integer> gpredicted = Lists.newLinkedList();
					List<Integer> ppredicted = Lists.newLinkedList();

					for (Tupla t : testRanks) {
						reals.add(t.getValue());
						lpredicted.add(lmo.rank(t.getFilm()));
						gpredicted.add(gmo.rank(t.getFilm()));
						ppredicted.add(pmo.rank(t.getFilm()));
					}

					Map<KernelType, List<Integer>> apr = Maps.newHashMap();
					apr.put(KernelType.Linear, lpredicted);
					apr.put(KernelType.Gaussian, gpredicted);
					apr.put(KernelType.Polynomial, ppredicted);

					Table<KernelType, MetricType, Double> predicted = HashBasedTable.create();
					for (KernelType kType : KernelType.values()) {
						for (MetricType metricType : AbstractMetric.MetricType.values()) {
							AbstractMetric metric = AbstractMetric.getErrorMetric(metricType);
							predicted.put(kType, metricType, metric.error(reals, apr.get(kType)));
						}
					}
					
					AbstractRankExperiment.write(csv, utente.getUser().getName(), ratingsUser.size(), mode, lambda, nfeatures, j, predicted);
				}
			}
		}
		csv.close();
	}
}
