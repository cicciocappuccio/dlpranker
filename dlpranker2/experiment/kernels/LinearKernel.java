package kernels;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import metrics.AbstractMetric;
import perceptron.AbstractPerceptronRanker;
import perceptron.ObjectRank;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.KFolder;

public class LinearKernel<T> extends AbstractKernel<T> {

	private static final int _NFOLDS = 10;

	private Set<T> instances;
	private Table<T, T, Double> kernel;

	public static <T> LinearKernel<T> create(Set<T> instances, Table<T, T, Double> kernel) {
		return new LinearKernel<T>(instances, kernel);
	}

	public LinearKernel(Set<T> instances, Table<T, T, Double> kernel) {
		this.instances = instances;
		this.kernel = kernel;
	}
	
	public Table<T, T, Double> calculate() {
		return kernel;
	}

	public SortedSet<ParamsScore> getParameters(LearningMethod mode, List<ObjectRank<T>> training, AbstractMetric metric, int nrating) throws Exception {
		int nfolds = Math.min(_NFOLDS, training.size());

		SortedSet<ParamsScore> ret = Sets.newTreeSet();

		// double[] D = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		double[] parametri = getParam(mode);

		KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, nfolds, new Random(0));

		for (double param : parametri) {

			double error = 0.0;

			for (int j = 0; j < nfolds; j++) {
				AbstractPerceptronRanker<T> mo = buildRanker(mode, instances, kernel, nrating, param);

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
			params.put("Param", param);

			double dnfolds = nfolds;
			ParamsScore ps = new ParamsScore(params, -(error / dnfolds), param);

			ret.add(ps);
		}

		return ret;
	}

}
