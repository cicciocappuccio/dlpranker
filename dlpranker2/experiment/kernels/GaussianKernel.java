package kernels;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import metrics.AbstractErrorMetric;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.KFolder;

public class GaussianKernel<T> {

	private static final int _NFOLDS = 10;

	private Set<T> instances;
	private Table<T, T, Double> euclideans;

	public static <T> GaussianKernel<T> createGivenEuclideans(Set<T> instances, Table<T, T, Double> euclideans) {
		return new GaussianKernel<T>(instances, euclideans);
	}
	
	public static <T> GaussianKernel<T> createGivenKernel(Set<T> instances, Table<T, T, Double> kernel) {
		Table<T, T, Double> euclideans = HashBasedTable.create();
		for (T xi : kernel.rowKeySet()) {
			double Kii = kernel.get(xi, xi);
			for (T xj : kernel.columnKeySet()) {
				double Kjj = kernel.get(xj, xj);
				euclideans.put(xi, xj, Math.sqrt(-kernel.get(xi, xj) + 0.5 * (Kii + Kjj)));
			}
		}
		return new GaussianKernel<T>(instances, euclideans);
	}
	
	public GaussianKernel(Set<T> instances, Table<T, T, Double> euclideans) {
		this.instances = instances;
		this.euclideans = euclideans;
	}

	public Table<T, T, Double> calculate(double sigma) {
		Table<T, T, Double> K = HashBasedTable.create();
		for (T xi : euclideans.rowKeySet()) {
			for (T xj : euclideans.columnKeySet()) {
				double sqdist = Math.pow(euclideans.get(xi, xj), 2.0);
				double val = Math.exp(- sqdist / (2.0 * Math.pow(sigma, 2.0)));
				K.put(xi, xj, val);
			}
		}
		return K;
	}

	public SortedSet<ParamsScore> getParameters(List<ObjectRank<T>> training, AbstractErrorMetric metric, int nrating) {
		int nfolds = Math.min(_NFOLDS, training.size());

		SortedSet<ParamsScore> ret = Sets.newTreeSet();

		for (double sigma = 1e-4; sigma <= 1e4; sigma *= 10.0) {

			Table<T, T, Double> K = calculate(sigma);
			KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, nfolds, new Random(0));

			double error = 0.0;
			
			for (int j = 0; j < nfolds; j++) {
				OnLineKernelPerceptronRanker<T> mo = new OnLineKernelPerceptronRanker<T>(instances, K, nrating);

				for (ObjectRank<T> or : folder.getOtherFolds(j)) {
					mo.feed(or);
				}

				List<Integer> real = Lists.newLinkedList();
				List<Integer> predicted = Lists.newLinkedList();

				for (ObjectRank<T> or: folder.getFold(j)) {
					real.add(or.rank);
					predicted.add(mo.rank(or.object));
				}
				
				error += metric.error(real, predicted);
			}
			
			Map<String, Double> params = Maps.newHashMap();
			params.put("Sigma", sigma);

			double dnfolds = nfolds;
			ParamsScore psJ = new ParamsScore(params, - (error / dnfolds), sigma);
			ret.add(psJ);
		}

		return ret;
	}

}
