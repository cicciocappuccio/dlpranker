package test.Batch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import kernels.AbstractKernel.KERNEL_MODE;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import perceptron.ObjectRank;
import utils.CSVW;
import utils.EIUtils;
import utils.Inference;
import utils.Inference.LogicValue;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DiagonalDoubleMatrix2D;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;

import features.FakeRefinementOperator;
import features.FeaturesGenerator;

public class AbstractRankExperimentLMBP {

	public static final int NFOLDS = 10;
	private static final Logger log = LoggerFactory.getLogger(AbstractRankExperimentLMBP.class);

	public static FeaturesGenerator getFeaturesGenerator(Inference inference) {
		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		Set<Description> _features = _fg.getFilmSubClasses();
		FakeRefinementOperator fro = new FakeRefinementOperator(inference.getReasoner(), _features);

		FeaturesGenerator fg = new FeaturesGenerator(inference, fro);
		return fg;
	}

	public static <I> Table<I, I, Double> makeGaussian(KERNEL_MODE mode, Set<I> filmsUser, Table<I, I, Double> K, List<ObjectRank<I>> objectranks, int nrating) throws Exception {
		AbstractErrorMetric metric = new AccuracyError();
		GaussianKernel<I> gk = GaussianKernel.createGivenKernel(filmsUser, K);
		SortedSet<ParamsScore> gps = gk.getParameters(mode, objectranks, metric, nrating);
		Double sigma = gps.first().getParams().get("Sigma");
		System.out.println("Best param for Gaussian kernel: " + gps.first());
		Table<I, I, Double> GK = gk.calculate(sigma);
		return GK;
	}

	public static <I> Table<I, I, Double> makePolynomial(KERNEL_MODE mode,Set<I> filmsUser, Table<I, I, Double> K, List<ObjectRank<I>> objectranks, int nrating) throws Exception {
		AbstractErrorMetric metric = new AccuracyError();
		PolynomialKernel<I> pk = new PolynomialKernel<I>(filmsUser, K);
		SortedSet<ParamsScore> pps = pk.getParameters(mode, objectranks, metric, nrating);
		Double d = pps.first().getParams().get("D");
		System.out.println("Best param for Polynomial kernel: " + pps.first());
		Table<I, I, Double> PK = pk.calculate(d);
		return PK;
	}

	public static void write(CSVW csv, String user, double lambda, int nfeatures, int nfold, double lmae, double gmae, double pmae, double lrmse, double grmse, double prmse, double lscc, double gscc, double pscc) throws IOException {
		List<String> row = Lists.newLinkedList();

		row.add(user);
		row.add(Double.toString(lambda));
		row.add(Integer.toString(nfeatures));
		row.add(Integer.toString(nfold));

		row.add(Double.toString(lmae));
		row.add(Double.toString(gmae));
		row.add(Double.toString(pmae));

		row.add(Double.toString(lrmse));
		row.add(Double.toString(grmse));
		row.add(Double.toString(prmse));

		row.add(Double.toString(lscc));
		row.add(Double.toString(gscc));
		row.add(Double.toString(pscc));

		csv.write(row);
	}

	public static CSVW getCSV(String fileName, String param1, String param2) throws Exception {
		File outFile = new File(fileName);
		if (outFile.exists())
			outFile.delete();
		PrintWriter pw = new PrintWriter(outFile);
		CSVW csv = new CSVW(pw);
		List<String> methods = Lists.newArrayList("Linear", "Gaussian", "Polynomial");
		List<String> headRow = Lists.newArrayList();
		headRow.add("utente");
		headRow.add(param1);
		headRow.add(param2);
		headRow.add("fold");
		for (String method : methods)
			headRow.add(method + " MAE");
		for (String method : methods)
			headRow.add(method + " RMSE");
		for (String method : methods)
			headRow.add(method + " Spearman");
		csv.write(headRow);
		return csv;
	}

	public static Inference getInference() throws Exception {
		String owl = "res/fragmentOntology10.owl";
		KnowledgeSource ks = new OWLFile(owl);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(owl);
		Inference inference = new Inference(cache, reasoner);
		return inference;
	}

	private static double k(LogicValue a, LogicValue b) {
		double ret = 0.5;
		if (a == LogicValue.TRUE && b == LogicValue.TRUE) {
			ret = 1.0;
		} else if (a == LogicValue.FALSE && b == LogicValue.FALSE) {
			ret = 1.0;
		} else if (a == LogicValue.TRUE && b == LogicValue.FALSE) {
			ret = 0.0;
		} else if (a == LogicValue.FALSE && b == LogicValue.TRUE) {
			ret = 0.0;
		}
		return ret;
	}

    public static Table<Individual, Individual, Double> normalizeKernel(Table<Individual, Individual, Double> K) {
    	Table<Individual, Individual, Double> normalized = HashBasedTable.create();
    	for (Individual i : K.rowKeySet()) {
    		double Kii = K.get(i, i);
    		for (Individual j : K.columnKeySet()) {
    			double Kjj = K.get(j, j);
    			double val = K.get(i, j);
    			normalized.put(i, j, val / Math.sqrt(Kii * Kjj));
   
    		}
    	}
    	return normalized;
    }
	
	public static Table<Individual, Individual, Double> buildKernel(Inference inference, Set<Description> features, Set<Individual> films) {

		log.info("Creating Kernel..");

		EIUtils ei = new EIUtils(inference);

		Map<Description, Double> entropies = Maps.newHashMap();
		double normHs = 0.0;
		for (Description f : features) {
			double ent = ei.H(f, films);
			entropies.put(f, ent);
			normHs += Math.pow(ent, 2);
		}
		normHs = Math.sqrt(normHs);

		Map<Description, Double> weights = Maps.newHashMap();
		for (Description f : features) {
			weights.put(f, entropies.get(f) / normHs);
		}

		Table<Description, Individual, LogicValue> vi = HashBasedTable.create();

		for (Description feature : features) {
			for (Individual individual : films) {
				LogicValue b = inference.cover(feature, individual);
				vi.put(feature, individual, b);
			}
		}

		if (inference.getCache() != null)
			inference.getCache().save();

		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = Sets.newHashSet(films);

		for (Individual i : films) {
			for (Individual j : toCheck) {
				double sum = 0;
				for (Description feature : features) {
					LogicValue a = vi.get(feature, i);
					LogicValue b = vi.get(feature, j);
					double ki = k(a, b);
					double weight = weights.get(feature);
					sum += Math.pow(Math.abs(weight * ki), 2.0);
				}
				sum = (Math.sqrt(sum));
				K.put(i, j, sum);
				K.put(j, i, sum);
			}
			toCheck.remove(i);
		}

		return normalizeKernel(K);
	}

	public static <P extends Number & Comparable<P>> List<Number> getParam(P start, P end, int step) throws Exception {
		if (start.compareTo(end) > 0)
			throw new Exception();

		step--;
		List<Number> paramList = Lists.newLinkedList();

		System.out.println((end.doubleValue() - start.doubleValue()) % step);
		if ((end.doubleValue() - start.doubleValue()) % step == 0) {
			
			for (int i = 0; i <= step; i++) {
				paramList.add(new Integer(((end.intValue() - start.intValue()) / step) * i) + start.intValue());
			}

		} else {
			for (int i = 0; i <= step; i++) {
				paramList.add(new Double(((end.doubleValue() - start.doubleValue()) / step) * i) + start.doubleValue());
			}
		}
		return paramList;
	}

}
