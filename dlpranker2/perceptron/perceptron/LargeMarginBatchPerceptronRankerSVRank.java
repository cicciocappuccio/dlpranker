package perceptron;

import gurobi.GRBEnv;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.neuralnoise.svm.SVMUtils;
import com.neuralnoise.svrank.AbstractSVRank;
import com.neuralnoise.svrank.SoftRank;

public class LargeMarginBatchPerceptronRankerSVRank<T> extends AbstractPerceptronRanker<T> {

	// map: rank - classifier
	// private BiMap<Integer, AbstractSVRank<T>> classifiers;
	// private double[] V = new double[] { 0.05, 0.1, 0.15, 0.2, 0.25, 0.3,
	// 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95,
	// 1.0 };

	private double v;

	private AbstractSVRank<T> svm;

	public LargeMarginBatchPerceptronRankerSVRank(Set<T> objects, Table<T, T, Double> K, int ranks, double v) {
		super(objects, K, ranks);
		this.v = v;
	}

	public void train(List<ObjectRank<T>> stream) throws Exception {
		GRBEnv env = SVMUtils.buildEnvironment();

		Map<T, Integer> map = Maps.newHashMap();

		for (ObjectRank<T> o : stream) {
			map.put(o.getObject(), o.getRank());
		}

		svm = null;

		try {
			svm = new SoftRank<T>(env, objects, map, ranks, K, v);
		} catch (gurobi.GRBException e) {
			svm = null;
		}

	}

	// ################################ DA CONTROLLARE
	// ################################
	@Override
	public int rank(T o) {
		if (svm == null)
			return Integer.MAX_VALUE;

		return svm.rank(o);
	}
}
