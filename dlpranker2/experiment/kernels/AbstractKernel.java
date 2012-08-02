package kernels;

import gurobi.GRBEnv;

import java.util.Set;

import perceptron.AbstractPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRankerSVRank;
import perceptron.OnLineKernelPerceptronRanker;

import com.google.common.collect.Table;
import com.neuralnoise.svm.SVMUtils;

public class AbstractKernel<T> {

	public static enum LearningMethod {
		SIMPLE_ONLINE, ONEVSALL_BATCH, SOFTMARGIN_BATCH
	}

	public AbstractPerceptronRanker<T> buildRanker(LearningMethod mode, GRBEnv env, Set<T> instances, Table<T, T, Double> K, int nrating, double param) {
		AbstractPerceptronRanker<T> ret = null;
		switch (mode) {
		case SIMPLE_ONLINE:
			ret = new OnLineKernelPerceptronRanker<T>(instances, K, nrating);
			break;

		case ONEVSALL_BATCH:
			ret = new LargeMarginBatchPerceptronRanker<T>(env, instances, K, nrating, param);
			break;

		case SOFTMARGIN_BATCH:
			ret = new LargeMarginBatchPerceptronRankerSVRank<T>(env, instances, K, nrating, param);
			break;
		}
		return ret;
	}

	public double[] getParam(LearningMethod mode) {
		double[] parametri = null;
		switch (mode) {
		case SIMPLE_ONLINE:
			parametri = new double[] { 0.0 };
			break;

		case ONEVSALL_BATCH:
			parametri = new double[] { 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6 };
			break;

		case SOFTMARGIN_BATCH:
			parametri = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1e-1, 1e-2, 1e-3, 1e-4, 1 - 1e-1, 1 - 1e-2, 1 - 1e-3, 1 - 1e-4 };
			break;
		}
		return parametri;
	}
}
