package test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.RhoDRDown;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;

import metrics.AbstractErrorMetric;
import metrics.MAE;
import metrics.RMSE;
import metrics.SpearmanCorrelationCoefficient;

import scoring.MRMRScore;
import scripts.AbstractRankExperiment;
import utils.CSVW;
import utils.EIUtils;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

import dataset.ExperimentDataset;
import dataset.KFolder;
import dataset.Tupla;
import features.FakeRefinementOperator;
import features.FeaturesGenerator;

public class ExperimentTestMRMR {

	public static void main(String[] args) throws Exception {

		double lambda = 1.0;
		int nfeatures = 2;
		int j = 0; // j fold

		// ----------------------------------------------------------------------------------------------------
		String fileName = "res/risultati/TEST_MRMR.csv";

		// CSVW csv = AbstractRankExperiment.getCSV(fileName, "lambda",
		// "nfeatures");

		int nrating = 5;

		// ------- inference ---------
		String owl = "res/fragmentOntology10.owl";
		KnowledgeSource ks = new OWLFile(owl);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(owl);
		Inference inference = new Inference(cache, reasoner);

		// ----------------------------

		RhoDRDown op = new RhoDRDown();
		op.setReasoner(reasoner);
		op.setSubHierarchy(reasoner.getClassHierarchy());
		op.setObjectPropertyHierarchy(reasoner.getObjectPropertyHierarchy());
		op.setDataPropertyHierarchy(reasoner.getDatatypePropertyHierarchy());
		
		op.setCardinalityLimit(5);
		op.setUseBooleanDatatypes(true);
		op.setUseNegation(false);
		op.setUseStringDatatypes(true);
		op.setUseDataHasValueConstructor(true);
		
		op.init();

		FeaturesGenerator fg = new FeaturesGenerator(inference, op);

		// Psi2DownWrapper psi = new Psi2DownWrapper(reasoner);

		// psi.init();

		// FeaturesGenerator fg = new FeaturesGenerator(inference, psi);

		// ----------------------------
		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> utenti = ExperimentDataset.getUsers(lista);
		Iterator<Tupla> utenteI = utenti.iterator();


		Tupla utente = null;
		List<Tupla> ratingsUser = null;
		do {
			utente = utenteI.next();
			ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());
		} while (utenteI.hasNext() && ratingsUser.size() < 80);
/**/		
		
		//List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());
		//System.out.println("ratingsUser.size(): " + ratingsUser.size());

		AbstractErrorMetric mae = new MAE();
		AbstractErrorMetric rmse = new RMSE();
		AbstractErrorMetric scc = new SpearmanCorrelationCoefficient();

		Set<Individual> filmsUser = Sets.newHashSet();

		for (Tupla i : ratingsUser)
			filmsUser.add(i.getFilm());
		KFolder<Tupla> folder = new KFolder<Tupla>(ratingsUser, AbstractRankExperiment.NFOLDS);

		// for (int j = 0; j < AbstractRankExperiment.NFOLDS; j++) {
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

		EIUtils calc = new EIUtils(inference);
		MRMRScore tScore = new MRMRScore(inference, multimap, 1.0, calc);

		Set<Description> features = fg.getMRMRFeatures(film, tScore, lambda, nfeatures);

		System.out.println("Lambda: " + lambda + " numero di features: " + features.size());
		System.out.println(features);

		Table<Individual, Individual, Double> K = AbstractRankExperiment.buildKernel(inference, features, filmsUser);

		System.out.println(K);

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

		double lrmse = rmse.error(reals, lpredicted);
		double grmse = rmse.error(reals, gpredicted);
		double prmse = rmse.error(reals, ppredicted);

		double lscc = scc.error(reals, lpredicted);
		double gscc = scc.error(reals, gpredicted);
		double pscc = scc.error(reals, ppredicted);

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
		System.out.println("pscc: " + pscc);

	}
}
