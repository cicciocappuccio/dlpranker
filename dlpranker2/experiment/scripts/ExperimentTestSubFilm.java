package scripts;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import kernels.AbstractKernel.KERNEL_MODE;
import kernels.GaussianKernel;
import kernels.LinearKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.AbstractErrorMetric;
import metrics.MAE;
import metrics.RMSE;
import metrics.SpearmanCorrelationCoefficient;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.ObjectRank;
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

public class ExperimentTestSubFilm {

	public static void main(String[] args) throws Exception {

		double lambda = 1.0;
		int nfeatures = -1;
		// int j = 3; // j fold

		//KERNEL_MODE mode = KERNEL_MODE.ONEVSALL_BATCH;

		// ----------------------------------------------------------------------------------------------------
		String fileName = "res/risultati/ExperimentTestSubFilm.csv";

		CSVW csv = AbstractRankExperiment.getCSV(fileName, "lambda", "nfeatures");

		int nrating = 5;

		Inference inference = AbstractRankExperiment.getInference();
		FeaturesGenerator fg = AbstractRankExperiment.getFeaturesGenerator(inference);

		// ----------------------------
		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> utenti = ExperimentDataset.getUsers(lista);
		Iterator<Tupla> utenteI = utenti.iterator();

		AbstractErrorMetric mae = new MAE();
		AbstractErrorMetric rmse = new RMSE();
		AbstractErrorMetric scc = new SpearmanCorrelationCoefficient();

		Set<Description> features = fg.getFilmSubClasses();
		
		do {

			Tupla utente = null;
			List<Tupla> ratingsUser = null;
			do {
				utente = utenteI.next();
				ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());
			} while (utenteI.hasNext() && ratingsUser.size() < 30);

			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());
			
			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, AbstractRankExperiment.NFOLDS);
			
			for (KERNEL_MODE mode : KERNEL_MODE.values()) {
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

					LinearKernel<Individual> lk = new LinearKernel<Individual>(filmsUser, K);
					SortedSet<ParamsScore> lps = AbstractRankExperiment.findLinear(mode, filmsUser, K, objectranks, nrating, lk);
					Double paramL = lps.first().getParams().get("Param");
					System.out.println("Best param for Linear kernel: " + lps.first());
					Table<Individual, Individual, Double> LK = lk.calculate();

					GaussianKernel<Individual> gk = GaussianKernel.createGivenKernel(filmsUser, K);
					SortedSet<ParamsScore> gps = AbstractRankExperiment.findGaussian(mode, filmsUser, K, objectranks, nrating, gk);
					Double sigma = gps.first().getParams().get("Sigma");
					Double paramG = gps.first().getParams().get("Param");
					System.out.println("Best param for Gaussian kernel: " + gps.first());
					Table<Individual, Individual, Double> GK = gk.calculate(sigma);

					PolynomialKernel<Individual> pk = new PolynomialKernel<Individual>(filmsUser, K);
					SortedSet<ParamsScore> pps = AbstractRankExperiment.findPolynomial(mode, filmsUser, K, objectranks, nrating, pk);
					Double d = pps.first().getParams().get("D");
					Double paramP = pps.first().getParams().get("Param");
					System.out.println("Best param for Polynomial kernel: " + pps.first());
					Table<Individual, Individual, Double> PK = pk.calculate(d);

					LargeMarginBatchPerceptronRanker<Individual> lmo = new LargeMarginBatchPerceptronRanker<Individual>(filmsUser, LK, nrating, paramL);
					LargeMarginBatchPerceptronRanker<Individual> gmo = new LargeMarginBatchPerceptronRanker<Individual>(filmsUser, GK, nrating, paramG);
					LargeMarginBatchPerceptronRanker<Individual> pmo = new LargeMarginBatchPerceptronRanker<Individual>(filmsUser, PK, nrating, paramP);

					lmo.train(objectranks);
					gmo.train(objectranks);
					pmo.train(objectranks);

					// Fase di TEST

					List<Integer> reals = Lists.newLinkedList();

					List<Integer> lpredicted = Lists.newLinkedList();
					List<Integer> gpredicted = Lists.newLinkedList();
					List<Integer> ppredicted = Lists.newLinkedList();

					for (Tupla t : testRanks) {
						reals.add(t.getValue());
//						System.out.println(t.getFilm() + " reale: " + t.getValue());
						lpredicted.add(lmo.rank(t.getFilm()));
//						System.out.println(lmo.rank(t.getFilm()));
						gpredicted.add(gmo.rank(t.getFilm()));
//						System.out.println(gmo.rank(t.getFilm()));
						ppredicted.add(pmo.rank(t.getFilm()));
//						System.out.println(pmo.rank(t.getFilm()));

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

/*					System.out.println("\n------------------------------------------------------------------------------\n\nmode: " + mode);
					System.out.println(utente.getUser().getName());
					System.out.println("ratingsUser.size()" + ratingsUser.size());
					System.out.println("lambda: " + lambda);
					System.out.println("nfeatures: " + nfeatures + " |" + features);
					System.out.println("j: " + j);
					System.out.println("lmae: " + lmae);
					System.out.println("gmae: " + gmae);
					System.out.println("pmae: " + pmae);
					System.out.println("lrmse: " + lrmse);
					System.out.println("grmse: " + grmse);
					System.out.println("prmse: " + prmse);
					System.out.println("lscc: " + lscc);
					System.out.println("gscc: " + gscc);
					System.out.println("pscc: " + pscc);*/

					AbstractRankExperiment.write(csv, utente.getUser().getName(), mode, lambda, nfeatures, j, lmae, gmae, pmae, lrmse, grmse, prmse, lscc, gscc, pscc);
				}
			}
		} while (utenteI.hasNext());
		csv.close();
	}
}
