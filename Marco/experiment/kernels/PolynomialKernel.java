package kernels;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import metrics.ErrorMetric;
import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import test.KFolder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class PolynomialKernel<T> {

	private static final int _NFOLDS = 10;

	private Set<T> instances;
	private Table<T, T, Double> kernel;

	public PolynomialKernel(Set<T> instances, Table<T, T, Double> kernel) {
		this.instances = instances;
		this.kernel = kernel;
	}

	public Table<T, T, Double> calculate(double D) {
		Table<T, T, Double> K = HashBasedTable.create();
		for (T xi : kernel.rowKeySet()) {
			for (T xj : kernel.columnKeySet()) {
				double val = Math.pow((kernel.get(xi, xj) + 1.0) , D);
				K.put(xi, xj, val);
			}
		}
		return K;
	}

	public SortedSet<ParamsScore> getParameters(List<ObjectRank<T>> training, ErrorMetric metric) {
		int nfolds = Math.min(_NFOLDS, training.size());

		SortedSet<ParamsScore> ret = Sets.newTreeSet();

		for (double D = 1; D <= 9; D += 1.0) {

			Table<T, T, Double> K = calculate(D);
			KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, nfolds, new Random(0));

			double error = 0.0;
			
			for (int j = 0; j < nfolds; j++) {
				OnLineKernelPerceptronRanker<T> mo = new OnLineKernelPerceptronRanker<T>(instances, K, 5);

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
			params.put("D", D);

			double dnfolds = nfolds;
			ParamsScore psJ = new ParamsScore(params, - (error / dnfolds), D);
			ret.add(psJ);
		}

		return ret;
	}

}
