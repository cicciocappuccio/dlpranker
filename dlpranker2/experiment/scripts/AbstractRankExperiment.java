package scripts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import kernels.AbstractKernel.LearningMethod;
import kernels.DiffusionKernel;
import kernels.GaussianKernel;
import kernels.LinearKernel;
import kernels.ParamsScore;
import kernels.PolynomialKernel;
import metrics.AbstractMetric;
import metrics.AbstractMetric.MetricType;
import metrics.AccuracyError;
import metrics.MAE;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import perceptron.AbstractPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRankerSVRank;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import utils.CSVW;
import utils.EIUtils;
import utils.Inference;
import utils.Inference.LogicValue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;
import com.neuralnoise.cache.VolatileConceptCache;
import com.neuralnoise.svm.SVMUtils;

import dataset.ITreeNode;
import dataset.OntologyAsGraph;
import dataset.RoleEdge;

import features.FakeRefinementOperator;
import features.FeaturesGenerator;
import gurobi.GRBEnv;

public class AbstractRankExperiment {


	private static final Logger log = LoggerFactory.getLogger(AbstractRankExperiment.class);
	
	public static final int NFOLDS = 10;
	
	public static enum KernelType {
		Linear, Gaussian, Polynomial, Diffusion
	}
  	
	public static FeaturesGenerator getFeaturesGenerator(Inference inference) {
		FeaturesGenerator _fg = new FeaturesGenerator(inference, null);
		Set<Description> _features = _fg.getFilmSubClasses();
		FakeRefinementOperator fro = new FakeRefinementOperator(inference.getReasoner(), _features);

		FeaturesGenerator fg = new FeaturesGenerator(inference, fro);
		return fg;
	}
	
	public static void write(CSVW csv, String user, int ratingsNumber, LearningMethod learningMethod, double param1, int param2, int nfold, Table<KernelType, MetricType, Double> predicted)
			throws IOException {
		List<String> row = Lists.newLinkedList();

		row.add(user);
		row.add(Integer.toString(ratingsNumber));
		row.add(learningMethod.toString());
		row.add(Double.toString(param1));
		row.add(Integer.toString(param2));
		row.add(Integer.toString(nfold));

		for (KernelType kernelType : KernelType.values()) {
			for (MetricType metric : AbstractMetric.MetricType.values()) {
				row.add(Double.toString(predicted.get(kernelType, metric)));
			}
		}
		
		csv.write(row);
	}

	public static CSVW getCSV(String fileName, String param1, String param2) throws Exception {
		File outFile = new File(fileName);
		if (outFile.exists())
			outFile.delete();
		
		PrintWriter pw = new PrintWriter(outFile);
		CSVW csv = new CSVW(pw);
		List<String> headRow = Lists.newArrayList();
		
		headRow.add("User");
		headRow.add("nRatings");
		headRow.add("LearningMethod");
		
		headRow.add(param1);
		headRow.add(param2);
		
		headRow.add("fold");
		
		for (KernelType kernelType : KernelType.values()) {
			for (MetricType metric : AbstractMetric.MetricType.values()) {
				headRow.add(kernelType + " " + metric);
			}
		}

		csv.write(headRow);
		return csv;
	}

	public static Inference getInference() throws Exception {
		String owl = "res/fragmentOntology10.owl";
		KnowledgeSource ks = new OWLFile(owl);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		AbstractConceptCache cache = new VolatileConceptCache(owl);
		//AbstractConceptCache cache = new AsynchronousHibernateConceptCache(owl);
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

	public static Table<Individual, Individual, Double> buildLoeschKernel(OntologyAsGraph onto, Set<Individual> films, int d, double lambda) {
		
		log.info("Creating Kernel..");
		
		Table<Individual, Individual, Double> K = HashBasedTable.create();

		for (Individual i : films) {
			for (Individual j : films) {
				DirectedMultigraph<ITreeNode, RoleEdge> itree = onto.intersectionTree(i.toString(), j.toString(), d);
				double k = onto.st(itree, lambda);
				K.put(i, j, k);
			}
		}
		
		return K;
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

		return K;// return normalizeKernel(K);
	}

	public static <I> AbstractPerceptronRanker<I> train(GRBEnv env, KernelType kType, LearningMethod mode, MetricType metric,
			Set<I> filmsUser, Table<I, I, Double> k, int ranks, List<ObjectRank<I>> objectranks) throws Exception {
		
		AbstractMetric _metric = AbstractMetric.getErrorMetric(metric);
		AbstractPerceptronRanker<I> ret = null;
		
		SortedSet<ParamsScore> _ps = null;
		Table<I, I, Double> _K = null;
		Double _param = null;
		
		switch (kType) {
		case Linear: {
			LinearKernel<I> lk = new LinearKernel<I>(filmsUser, k);
			_ps = lk.getParameters(env, mode, objectranks, _metric, ranks);
			_K = lk.calculate();
		} break;
			
		case Gaussian: {
			GaussianKernel<I> gk = GaussianKernel.createGivenKernel(filmsUser, k);
			_ps = gk.getParameters(env, mode, objectranks, _metric, ranks);
			Double sigma = _ps.first().getParams().get("Sigma");
			_K = gk.calculate(sigma);
		} break;

		case Polynomial: {
			PolynomialKernel<I> pk = new PolynomialKernel<I>(filmsUser, k);
			_ps = pk.getParameters(env, mode, objectranks, _metric, ranks);
			Double d = _ps.first().getParams().get("D");
			_K = pk.calculate(d);
		} break;
			
		case Diffusion: {
			DiffusionKernel<I> dk = new DiffusionKernel<I>(filmsUser, k);
			_ps = dk.getParameters(env, mode, objectranks, _metric, ranks);
			Double d = _ps.first().getParams().get("Lambda");
			_K = dk.calculate(d);
		} break;
		}
		
		_param = _ps.first().getParams().get("Param");
		
		log.info("Best params for " + kType + ": " + _ps.first());

		ret = buildLearner(mode, env, filmsUser, _K, ranks, _param);
		ret.train(objectranks);
		
		return ret;
	}
	
	public static <I> AbstractPerceptronRanker<I> buildLearner(LearningMethod mode, GRBEnv env, Set<I> ratings, Table<I, I, Double> K, int ranks, Double param) {
		AbstractPerceptronRanker<I> ret = null;

		log.info("Building learner " + mode + " with " + ratings.size() + " ratings ..");
		
		switch (mode) {
		case SIMPLE_ONLINE:
			ret = new OnLineKernelPerceptronRanker<I>(ratings, K, ranks);
			break;
		case ONEVSALL_BATCH:
			ret = new LargeMarginBatchPerceptronRanker<I>(env, ratings, K, ranks, param);
			break;
		case SOFTMARGIN_BATCH:
			ret = new LargeMarginBatchPerceptronRankerSVRank<I>(env, ratings, K, ranks, param);
		}
		return ret;
	}
	
	public static Multimap<Integer, Individual> balance(Multimap<Integer, Individual> multimap, List<ObjectRank<Individual>> objectranks) {
		
		int min = Integer.MAX_VALUE;
		
		for (Integer value : multimap.keySet())
		{
			if (multimap.get(value).size() < min)
				min = multimap.get(value).size();
		}
		
		
		return null;
	}
}
