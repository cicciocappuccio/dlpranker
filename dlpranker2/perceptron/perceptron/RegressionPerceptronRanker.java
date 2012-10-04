package perceptron;

import java.util.List;
import java.util.Map;
import java.util.Set;

import kernelMethods.regression.RidgeRegression;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class RegressionPerceptronRanker<T> extends AbstractPerceptronRanker<T> {

	private double lambda;
	private RidgeRegression<T> rr;

	public RegressionPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int ranks, double lambda) {
		super(objects, K, ranks);
		this.lambda = lambda;
	}

	public void train(List<ObjectRank<T>> stream) throws Exception {
		Map<T, Double> map = Maps.newHashMap();

		for (ObjectRank<T> o : stream) {
			map.put(o.getObject(), Double.valueOf(o.getRank()));
		}

		this.rr = null;

		try {
			this.rr = new RidgeRegression<T>(objects, map, K, lambda);
		} catch (gurobi.GRBException e) {
			this.rr = null;
		}

	}

	@Override
	public int rank(T o) {
		if (this.rr == null) {
			return Integer.MAX_VALUE;
		}
		double eval = rr.evaluate(o);
		int ret = 1;
		for (int rank = 1; rank <= this.ranks; ++rank) {
			if (eval > (rank - 0.5)) {
				ret = rank;
			}
		}
		return ret;
	}

}
