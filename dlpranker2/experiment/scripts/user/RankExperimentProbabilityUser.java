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
import scripts.AbstractRankExperiment;
import utils.CSVW;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;
import features.FeaturesGenerator;

public class RankExperimentProbabilityUser extends AbstractRankExperiment {

	public static void main(String[] args) throws Exception {

		String fileName = "res/risultati/RankExperimentProbabilityUser.csv";

		CSVW csv = getCSV(fileName, "probability", "nfeatures");

		int nrating = 5;

		Inference inference = getInference();
		
		FeaturesGenerator fg = getFeaturesGenerator(inference);

		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> utenti = ExperimentDataset.getUsers(lista);
		
		List<Tupla> filmList = ExperimentDataset.getFilms(lista);
		
		Set<Individual> filmsSet = Sets.newHashSet();
		for(Tupla i : filmList)
			filmsSet.add(i.getFilm());
			
		List<Double> probabilities = Lists.newLinkedList();
		for(double p = 1.0; p > 0; p -= 0.1)
			probabilities.add(p);
		
		List<Integer> nfeaturess = Lists.newArrayList();
		for (int i = 0; i < 1; i++)
			nfeaturess.add(i);
		
		
		for (Tupla utente : utenti) {

			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());

			if (ratingsUser.size() < 80)
				continue;

			System.out.println("Number of ratings: " + ratingsUser.size());

			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());
			
			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, NFOLDS);

			for (double p : probabilities) {
			
				for (int nfeatures : nfeaturess) {
					
					Set<Description> features = fg.getFilteredProbabilityFilmSubClasses(filmsSet, p);
					
					AbstractErrorMetric mae = new MAE();
					AbstractErrorMetric rmse = new RMSE();
					AbstractErrorMetric scc = new SpearmanCorrelationCoefficient();
					
					for (int j = 0; j < NFOLDS; j++) {
						List<Tupla> trainingRanks = folder.getOtherFolds(j);
						
						List<ObjectRank<Individual>> objectranks = Lists.newLinkedList();

						for (Tupla film : trainingRanks) {
							ObjectRank<Individual> ii = new ObjectRank<Individual>(film.getFilm(), film.getValue());
							objectranks.add(ii);
						}

						List<Tupla> testRanks = folder.getFold(j);
						
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

						write(csv, utente.getUser().getName(), p, features.size(), j, lmae, gmae, pmae, lrmse, grmse, prmse, lscc, gscc, pscc);
					}
				}
			}
		}
		csv.close();
	}
}
