package scripts.user;

import features.FakeRefinementOperator;
import features.FeaturesGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import kernels.GaussianKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.AbstractErrorMetric;
import metrics.AccuracyError;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import scoring.MRMRScore;
import scripts.AbstractRankExperiment;
import utils.EIUtils;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;

public class RankExperimentMRMRUser extends AbstractRankExperiment {

	public static final int NFOLDS = 10;

	public static void main(String[] args) throws Exception {

		List<Tupla> lista = XMLFilmRatingStream.leggi();

		// XXX XXX XXX
		for (Tupla t : lista) {
			t.setValue((t.getValue() < 3 ? 1 : 2));
		}
		int nrating = 2;
		int nfeatures = 3;
		
		String owl = "res/fragmentOntology10.owl";

		KnowledgeSource ks = new OWLFile(owl);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(owl);

		Inference inference = new Inference(cache, reasoner);

		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		FakeRefinementOperator fro = new FakeRefinementOperator(reasoner, _fg.getFilmSubClasses());

		FeaturesGenerator fg = new FeaturesGenerator(inference, fro);

		List<Tupla> utenti = ExperimentDataset.getUsers(lista);

		List<Double> lambdas = Lists.newLinkedList();
		lambdas.add(1.0);

		for (Tupla utente : utenti) {

			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());

			if (ratingsUser.size() < 100)
				continue;

			System.out.println("Number of ratings: " + ratingsUser.size());
			
			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser)
				filmsUser.add(i.getFilm());

			KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, NFOLDS);

			for (int j = 0; j < NFOLDS; j++) {
				List<Tupla> trainingRanks = folder.getOtherFolds(j);

				Multimap<Integer, Individual> multimap = HashMultimap.create();
				List<ObjectRank<Individual>> objectranks = Lists.newLinkedList();

				for (Tupla film : trainingRanks) {
					multimap.put(film.getValue(), film.getFilm());
					ObjectRank<Individual> ii = new ObjectRank<Individual>(film.getFilm(), film.getValue());
					objectranks.add(ii);
				}

				Set<Individual> film = Sets.newHashSet(multimap.values());

				List<Tupla> testRanks = folder.getFold(j);

				Set<Description> prevFeatures = null, features = null;

				for (double lambda : lambdas) {
					EIUtils calc = new EIUtils(inference);
					MRMRScore tScore = new MRMRScore(inference, multimap, 0, calc);

					prevFeatures = features;
					features = fg.getMRMRFeatures(film, tScore, lambda, nfeatures);

					if (prevFeatures != null && Sets.symmetricDifference(prevFeatures, features).size() == 0)
						continue;

					System.out.println("Lambda: " + lambda + " numero di features: " + features.size());

					Table<Individual, Individual, Double> K = buildKernel(inference, features, filmsUser);

					//for (Cell<Individual, Individual, Double> kCell : K.cellSet()) {
					//	System.out.println(features);
					//	System.out.println(kCell);
					//}
					
					AbstractErrorMetric accuracy = new AccuracyError();
					GaussianKernel<Individual> gk = GaussianKernel.createGivenKernel(filmsUser, K);
					PolynomialKernel<Individual> pk = new PolynomialKernel<Individual>(filmsUser, K);

					SortedSet<ParamsScore> gps = gk.getParameters(objectranks, accuracy, nrating);
					SortedSet<ParamsScore> pps = pk.getParameters(objectranks, accuracy, nrating);

					Double sigma = gps.first().getParams().get("Sigma");
					System.out.println("Best param for Gaussian kernel: " + gps.first());

					Double d = pps.first().getParams().get("D");
					System.out.println("Best param for Polynomial kernel: " + pps.first());

					Table<Individual, Individual, Double> GK = gk.calculate(sigma);
					Table<Individual, Individual, Double> PK = pk.calculate(d);

					// XXX XXX XXX
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
						System.out.printf("reale: " + t.getValue() + "; ");

						lpredicted.add(lmo.rank(t.getFilm()));
						System.out.printf(lpredicted.get(lpredicted.size() - 1) + "; ");

						gpredicted.add(gmo.rank(t.getFilm()));
						System.out.printf(gpredicted.get(lpredicted.size() - 1) + "; ");

						ppredicted.add(pmo.rank(t.getFilm()));
						System.out.printf(ppredicted.get(lpredicted.size() - 1) + "; ");

					}
					System.out.println();
				}
			}
		}
	}
}
