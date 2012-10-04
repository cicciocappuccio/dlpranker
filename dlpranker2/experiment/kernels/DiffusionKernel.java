package kernels;

import gurobi.GRBEnv;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import metrics.AbstractMetric;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import perceptron.AbstractPerceptronRanker;
import perceptron.ObjectRank;
import utils.MatrixUtils;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

import com.google.common.annotations.Beta;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import dataset.KFolder;

@Beta
public class DiffusionKernel<T> extends AbstractKernel<T> {

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

	public DiffusionKernel(Set<T> instances, Table<T, T, Double> euclideans) {
		this.instances = instances;
		this.euclideans = euclideans;
	}

	public Table<T, T, Double> calculate(double lambda) {
		BiMap<T, Integer> indices = HashBiMap.create();
		Set<T> X = Sets.union(euclideans.rowKeySet(), euclideans.columnKeySet());
		
		int n = 0;
		for (T x : X) {
			indices.put(x, Integer.valueOf(n++));
		}
		
		DoubleMatrix2D B = new DenseDoubleMatrix2D(n, n);
		for (T xi : X) {
			int i = indices.get(xi);
			for (T xj : X) {
				int j = indices.get(xj);
				B.set(i, j, euclideans.get(xi, xj));
			}
		}
		
		DoubleMatrix2D expB = MatrixUtils.expm(MatrixUtils.product(B, lambda));
		
		Table<T, T, Double> K = HashBasedTable.create();

		BiMap<Integer, T> inverse = indices.inverse();
		
		for (int i = 0; i < n; ++i) {
			T xi = inverse.get(i);
			for (int j = 0; j < n; ++j) {
				T xj = inverse.get(j);
				K.put(xi, xj, expB.get(i, j));
			}
		}
		
		return K;
	}

	public SortedSet<ParamsScore> getParameters(GRBEnv env, LearningMethod mode, List<ObjectRank<T>> training, AbstractMetric metric, int nrating) throws Exception {
		int nfolds = Math.min(_NFOLDS, training.size());

		SortedSet<ParamsScore> ret = Sets.newTreeSet();

		double[] lambdas = new double[] { .1, .2, .3, .4, .5, .6, .7, .8, .9 };

		double[] parametri = getParam(mode);

		for (double lambda : lambdas) {

			Table<T, T, Double> K = calculate(lambda);
			KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, nfolds, new Random(0));

			for (double param : parametri) {
				
				double error = 0.0;

				for (int j = 0; j < nfolds; j++) {
					AbstractPerceptronRanker<T> mo = buildRanker(mode, env, instances, K, nrating, param);

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
				params.put("Lambda", lambda);
				params.put("Param", param);

				double dnfolds = nfolds;
				ParamsScore psJ = new ParamsScore(params, -(error / dnfolds), lambda + param);
				ret.add(psJ);
			}
		}

		return ret;
	}


	public static void main(String[] args) {
		DoubleMatrix2D B = new DenseDoubleMatrix2D(2, 2);

		B.set(0, 0, 0.46076);
		B.set(0, 1, 0.78956);
		B.set(1, 0, 0.43202);
		B.set(1, 1, 0.76885);

		DoubleMatrix _B = new DoubleMatrix(B.toArray());
		DoubleMatrix _expB = MatrixFunctions.expm(_B);
		
		DoubleMatrix2D expB = new DenseDoubleMatrix2D(_expB.toArray2());
		System.out.println(expB);
	}

}
