package kernels;

import java.util.Set;

import perceptron.AbstractPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.LargeMarginBatchPerceptronRankerSVRank;
import perceptron.OnLineKernelPerceptronRanker;

import com.google.common.collect.Table;

public class AbstractKernel<T> {

	public static enum KERNEL_MODE {
		ONLINE_NAIVE, BATCH_SVM, BATCH_SVRANK
	}

	public AbstractPerceptronRanker<T> buildRanker(KERNEL_MODE mode, Set<T> instances, Table<T, T, Double> K, int nrating, double param) {
		AbstractPerceptronRanker<T> ret = null;
		switch (mode) {
		case ONLINE_NAIVE:
			ret = new OnLineKernelPerceptronRanker<T>(instances, K, nrating);
			break;

		case BATCH_SVM:
			ret = new LargeMarginBatchPerceptronRanker<T>(instances, K, nrating, param);
			break;

		case BATCH_SVRANK:
			ret = new LargeMarginBatchPerceptronRankerSVRank<T>(instances, K, nrating, param);
			break;
		}
		return ret;
	}

	public double[] getParam(KERNEL_MODE mode) {
		double[] parametri = null;
		switch (mode) {
		case ONLINE_NAIVE:
			parametri = new double[] { 0.0 };
			break;

		case BATCH_SVM:
			parametri = new double[] { 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8 };
			break;

		case BATCH_SVRANK:
			parametri = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 };
			break;
		}
		return parametri;
	}
}
