package kernels;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import metrics.AbstractErrorMetric;
import perceptron.AbstractPerceptronRanker;
import perceptron.ObjectRank;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.KFolder;

public class GaussianKernel<T> extends AbstractKernel<T> {

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
				double val = Math.exp(-sqdist / (2.0 * Math.pow(sigma, 2.0)));
				K.put(xi, xj, val);
			}
		}
		return K;
	}

	public SortedSet<ParamsScore> getParameters(KERNEL_MODE mode, List<ObjectRank<T>> training, AbstractErrorMetric metric, int nrating) throws Exception {
		int nfolds = Math.min(_NFOLDS, training.size());

		SortedSet<ParamsScore> ret = Sets.newTreeSet();

		double[] sigmas = new double[] { 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 1e1, 1e2, 1e3, 1e4 };
		// double[] sigmas = new double[] { 1e-2, 1e-1, 1e0, 1e1, 1e2 };

		double[] parametri = getParam(mode);

		for (double sigma : sigmas) {

			Table<T, T, Double> K = calculate(sigma);
			KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, nfolds, new Random(0));

			for (double param : parametri) {
				
				double error = 0.0;

				for (int j = 0; j < nfolds; j++) {
					AbstractPerceptronRanker<T> mo = buildRanker(mode, instances, K, nrating, param);

					mo.train(folder.getOtherFolds(j));

					List<Integer> real = Lists.newLinkedList();
					List<Integer> predicted = Lists.newLinkedList();

					for (ObjectRank<T> or : folder.getFold(j)) {
						real.add(or.rank);
						predicted.add(mo.rank(or.object));
					}

					error += metric.error(real, predicted);
				}
				Map<String, Double> params = Maps.newHashMap();
				params.put("Sigma", sigma);
				params.put("Param", param);

				double dnfolds = nfolds;
				ParamsScore psJ = new ParamsScore(params, -(error / dnfolds), sigma + param);
				ret.add(psJ);
			}
		}

		return ret;
	}

}
