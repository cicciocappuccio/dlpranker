package scripts.september24;

import gurobi.GRBEnv;

import java.util.List;
import java.util.Map;
import java.util.Set;

import kernels.AbstractKernel.LearningMethod;
import metrics.AbstractMetric;
import metrics.AbstractMetric.MetricType;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import perceptron.AbstractPerceptronRanker;
import perceptron.ObjectRank;
import scripts.AbstractRankExperiment;
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
import com.neuralnoise.svm.SVMUtils;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;

public class NoFeaturesAccuracy extends AbstractRankExperiment{

	public static final Logger log = LoggerFactory.getLogger(AbstractRankExperiment.class);
	
	public static void main(String[] args) throws Exception {

		int nfeatures = -1;
		int nrating = 5;

		//LearningMethod[] modes = LearningMethod.values();
		LearningMethod[] modes = {LearningMethod.SIMPLE_ONLINE};
		
		String fileName = "res/risultati/September24NoFeaturesAccuracy.csv";
		
		AbstractMetric.MetricType metricEval = AbstractMetric.MetricType.AccuracyError;
		
		CSVW csv = getCSV(fileName, "lambda", "nfeatures");

		GRBEnv env = SVMUtils.buildEnvironment();
		Inference inference = getInference();

		List<Tupla> lista = XMLFilmRatingStream.leggi();
		Set<Description> features = Sets.newHashSet();

		List<Tupla> _utenti = ExperimentDataset.getUsers(lista);

		List<Tupla> utenti = Lists.newArrayList();
		for (Tupla u : _utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, u.getUser());
			if (ratingsUser.size() >= 10) {
				utenti.add(u);
			}
		}

		for (Tupla utente : utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());

			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());

			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, NFOLDS);

			for (LearningMethod mode : modes) {
				for (int j = 0; j < NFOLDS; j++) {
					List<Tupla> trainingRanks = folder.getOtherFolds(j);

					Multimap<Integer, Individual> multimap = HashMultimap.create();
					List<ObjectRank<Individual>> objectranks = Lists.newLinkedList();

					for (Tupla film : trainingRanks) {
						multimap.put(film.getValue(), film.getFilm());
						ObjectRank<Individual> ii = new ObjectRank<Individual>(film.getFilm(), film.getValue());
						objectranks.add(ii);
					}

					List<Tupla> testRanks = folder.getFold(j);

					log.info("Numero di features: " + features.size());

					Table<Individual, Individual, Double> K = buildKernel(inference, features, filmsUser);

					AbstractPerceptronRanker<Individual> lmo = train(env, KernelType.Linear, mode, metricEval, filmsUser, K, nrating, objectranks);
					AbstractPerceptronRanker<Individual> gmo = train(env, KernelType.Gaussian, mode, metricEval, filmsUser, K, nrating, objectranks);
					AbstractPerceptronRanker<Individual> pmo = train(env, KernelType.Polynomial, mode, metricEval, filmsUser, K, nrating, objectranks);

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
					
					write(csv, utente.getUser().getName(), ratingsUser.size(), mode, 1.0, features.size(), j, predicted);
				}
			}
		}
		csv.close();
	}
}
