package kernels;

import java.util.Set;

import perceptron.AbstractPerceptronRanker;
import perceptron.OnLineKernelPerceptronRanker;

import com.google.common.collect.Table;

public class AbstractKernel<T> {

	public static enum KERNEL_MODE {
		ONLINE_NAIVE, BATCH_SVM
	}

	public AbstractPerceptronRanker<T> buildRanker(KERNEL_MODE mode, Set<T> instances, Table<T, T, Double> K, int nrating) {
		AbstractPerceptronRanker<T> ret = null;
		switch (mode) {
		case ONLINE_NAIVE:
			ret = new OnLineKernelPerceptronRanker<T>(instances, K, nrating);
			break;	
		case BATCH_SVM:
			ret = new OnLineKernelPerceptronRanker<T>(instances, K, nrating);
			break;
		}
		return ret;
	}

}
