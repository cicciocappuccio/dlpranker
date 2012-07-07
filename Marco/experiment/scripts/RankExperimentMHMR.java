package scripts;

import features.FakeRefinementOperator;
import features.FeaturesGenerator;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.utils.CSVW;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import kernels.GaussianKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.AbstractErrorMetric;
import metrics.AccuracyError;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

public class RankExperimentMHMR extends AbstractRankExperiment {

	public static final int NFOLDS = 10;

	public static void main(String[] args) throws Exception {

		File outFile = new File("res/risultati_MHMR_fsub.csv");
		if (outFile.exists())
			outFile.delete();

		PrintWriter pw = new PrintWriter(outFile);

		CSVW csv = new CSVW(pw);

		List<String> methods = Lists.newArrayList("Linear", "Gaussian", "Polynomial");
		List<String> headRow = Lists.newArrayList();

		headRow.add("Alpha");

		for (String method : methods)
			headRow.add(method + " MAE");

		for (String method : methods)
			headRow.add(method + " RMSE");

		for (String method : methods)
			headRow.add(method + " Spearman");
		
		headRow.add("features number");
		
		csv.write(headRow);

		//

		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));

		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(urlOwlFile);
		// AbstractConceptCache cache = new
		// AsynchronousHibernateConceptCache(urlOwlFile);

		Inference inference = new Inference(cache, reasoner);

		Set<Individual> films = dati.getIndividuals();

		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		FakeRefinementOperator fro = new FakeRefinementOperator(reasoner, _fg.getFilmSubClasses());
		FeaturesGenerator fg = new FeaturesGenerator(inference, fro);

		Set<Description> prevFeatures = null, features = null;

		MHMRScore tScore = new MHMRScore(inference, 1.0);

		for (double _alpha = 0.99; _alpha > 0.0; _alpha -= 0.1) {

			double alphaValue = _alpha;

			prevFeatures = features;

			features = fg.getMHMRFeatures(films, tScore, _alpha);

			System.out.println("Features: " + features.size() + " con Alpha = " + _alpha);

			if (prevFeatures != null && Sets.symmetricDifference(prevFeatures, features).size() == 0)
				continue;

			System.out.println("Features:");
			for (Description f : features) {
				System.out.println("\t" + f);
			}

			Table<Individual, Individual, Double> K = buildKernel(inference, features, films);

			AbstractErrorMetric accuracy = new AccuracyError(); // da utilizzare per best sigma

			AbstractErrorMetric mae = new MAE();
			AbstractErrorMetric rmse = new RMSE();
			AbstractErrorMetric scc = new SpearmanCorrelationCoefficient();

			GaussianKernel<Individual> gk = GaussianKernel.createGivenKernel(films, K);
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
			double lmae = (maeLErr / dnfolds);
			double gmae = (maeGErr / dnfolds);
			double pmae = (maePErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + rmseLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + rmseGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + rmsePErr / dnfolds);
			double lrmse = (rmseLErr / dnfolds);
			double grmse = (rmseGErr / dnfolds);
			double prmse = (rmsePErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + sccLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + sccGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + sccPErr / dnfolds);
			double lscc = (sccLErr / dnfolds);
			double gscc = (sccGErr / dnfolds);
			double pscc = (sccPErr / dnfolds);

			System.out.println("Linear kernel on test set with " + features.size() + " features: " + accuracyLErr / dnfolds);
			System.out.println("Gaussian kernel on test set with " + features.size() + " features: " + accuracyGErr / dnfolds);
			System.out.println("Polynomial kernel on test set with " + features.size() + " features: " + accuracyPErr / dnfolds);


			List<String> row = Lists.newLinkedList();
			
			row.add(Double.toString(alphaValue));
			
			row.add(Double.toString(lmae));
			row.add(Double.toString(gmae));
			row.add(Double.toString(pmae));

			row.add(Double.toString(lrmse));
			row.add(Double.toString(grmse));
			row.add(Double.toString(prmse));

			row.add(Double.toString(lscc));
			row.add(Double.toString(gscc));
			row.add(Double.toString(pscc));
			
			row.add(((Integer)features.size()).toString());

			csv.write(row);
		}
		csv.close();
	}
}
