package scripts.user;

import java.util.List;
import java.util.Set;

import metrics.AbstractErrorMetric;
import metrics.MAE;
import metrics.RMSE;
import metrics.SpearmanCorrelationCoefficient;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import scoring.MHMRScore;
import scripts.AbstractRankExperiment;
import utils.CSVW;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;
import features.FeaturesGenerator;

public class RankExperimentMHMRUser extends AbstractRankExperiment {

	public static void main(String[] args) throws Exception {

		String fileName = "res/risultati/RankExperimentMHMRUser.csv";

		CSVW csv = getCSV(fileName, "lambda", "nfeatures");

		int nrating = 5;

		Inference inference = getInference();

		FeaturesGenerator fg = getFeaturesGenerator(inference);

		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> utenti = ExperimentDataset.getUsers(lista);

		List<Tupla> filmsList = ExperimentDataset.getFilms(lista);

		Set<Individual> filmsSet = Sets.newHashSet();
		for (Tupla i : filmsList)
			filmsSet.add(i.getFilm());
		
		MHMRScore tScore = new MHMRScore(inference, 1.0);

		List<Double> lambdas = Lists.newLinkedList();
		lambdas.add(1.0);

		List<Integer> nfeaturess = Lists.newArrayList();
		for (int i = 0; i < 50; i++)
			nfeaturess.add(i);

		for (Tupla utente : utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());
			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());
			
			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, NFOLDS);
			
			for (double lambda : lambdas) {
				for (int nfeatures : nfeaturess) {
					
					Set<Description> features = fg.getMHMRFeatures(filmsSet, tScore, 1.0, nfeatures);
					
					AbstractErrorMetric mae = new MAE();
					AbstractErrorMetric rmse = new RMSE();
					AbstractErrorMetric scc = new SpearmanCorrelationCoefficient();

					for (int j = 0; j < NFOLDS; j++) {
						
						List<Tupla> trainingRanks = folder.getOtherFolds(j);
						List<Tupla> testRanks = folder.getFold(j);
						
						Multimap<Integer, Individual> multimap = HashMultimap.create();
						List<ObjectRank<Individual>> objectranks = Lists.newLinkedList();

						for (Tupla film : trainingRanks) {
							multimap.put(film.getValue(), film.getFilm());
							ObjectRank<Individual> ii = new ObjectRank<Individual>(film.getFilm(), film.getValue());
							objectranks.add(ii);
						}
						
						

						System.out.println("Lambda: " + 1.0 + " numero di features: " + features.size());

						Table<Individual, Individual, Double> K = buildKernel(inference, features, filmsUser);

						Table<Individual, Individual, Double> GK = makeGaussian(filmsUser, K, objectranks, nrating);
						Table<Individual, Individual, Double> PK = makePolynomial(filmsUser, K, objectranks, nrating);

						OnLineKernelPerceptronRanker<Individual> lmo = new OnLineKernelPerceptronRanker<Individual>(filmsUser, K, nrating);
						OnLineKernelPerceptronRanker<Individual> gmo = new OnLineKernelPerceptronRanker<Individual>(filmsUser, GK, nrating);
						OnLineKernelPerceptronRanker<Individual> pmo = new OnLineKernelPerceptronRanker<Individual>(filmsUser, PK, nrating);

						for (ObjectRank<Individual> i : objectranks) {
							lmo.feed(i);
							gmo.feed(i);
							pmo.feed(i);
						}

						// Fase di TEST

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

						double lmae = mae.error(reals, lpredicted);
						double gmae = mae.error(reals, gpredicted);
						double pmae = mae.error(reals, ppredicted);

						double lrmse = rmse.error(reals, lpredicted);
						double grmse = rmse.error(reals, gpredicted);
						double prmse = rmse.error(reals, ppredicted);

						double lscc = scc.error(reals, lpredicted);
						double gscc = scc.error(reals, gpredicted);
						double pscc = scc.error(reals, ppredicted);

						write(csv, utente.getUser().getName(), lambda, nfeatures, j, lmae, gmae, pmae, lrmse, grmse, prmse, lscc, gscc, pscc);
					}
				}
			}
		}
		csv.close();
	}
}
