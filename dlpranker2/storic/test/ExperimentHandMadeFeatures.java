package test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import metrics.AbstractErrorMetric;
import metrics.MAE;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import scripts.AbstractRankExperiment;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;

public class ExperimentHandMadeFeatures {

	public static void main(String[] args) throws Exception {

		double lambda = 1.0;
		int nfeatures = 0;
		int j = 0; // j fold

		Inference inference = AbstractRankExperiment.getInference();	
		List<Tupla> lista = XMLFilmRatingStream.leggi();
		
		Set<Description> features = Sets.newHashSet();
		//features.add(KBParser.parseConcept("EXISTS \"http://dbpedia.org/property/country\".TOP"));
		for (Description f : inference.getReasoner().getAtomicConceptsList()) {
			features.add(f);
		}
		
		int nrating = 5;



		List<Tupla> utenti = ExperimentDataset.getUsers(lista);
		Iterator<Tupla> utenteI = utenti.iterator();

		Tupla utente = null;
		List<Tupla> ratingsUser = null;
		do {
			utente = utenteI.next();
			ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());
		} while (utenteI.hasNext() && ratingsUser.size() < 80);

		AbstractErrorMetric mae = new MAE();

		Set<Individual> filmsUser = Sets.newHashSet();

		for (Tupla i : ratingsUser) {
			filmsUser.add(i.getFilm());
		}
		
		KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, AbstractRankExperiment.NFOLDS);

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
		System.out.println(features);

		Table<Individual, Individual, Double> K = AbstractRankExperiment.buildKernel(inference, features, filmsUser);

		for (Cell<Individual, Individual, Double> cellK : K.cellSet()) {
			System.out.println("K(" + cellK.getRowKey() + ", " + cellK.getColumnKey() +" ) = " + cellK.getValue());
		}
		
		Table<Individual, Individual, Double> GK = AbstractRankExperiment.makeGaussian(filmsUser, K, objectranks, nrating);
		Table<Individual, Individual, Double> PK = AbstractRankExperiment.makePolynomial(filmsUser, K, objectranks, nrating);

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

		System.out.println(utente.getUser().getName());
		System.out.println("ratingsUser.size()" + ratingsUser.size());
		System.out.println("lambda: " + lambda);
		System.out.println("nfeatures: " + nfeatures + " |" + features);
		System.out.println("j: " + j);
		System.out.println("lmae: " + lmae);
		System.out.println("gmae: " + gmae);
		System.out.println("pmae: " + pmae);

	}
}
