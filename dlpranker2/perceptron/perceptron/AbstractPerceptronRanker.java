package perceptron;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public abstract class AbstractPerceptronRanker<T> {

	//protected Map<T, Double> alpha;
	protected Map<T, Double> alpha;
	protected double[] b;
	protected int ranks;
	
	protected Set<T> objects;
	protected Table<T, T, Double> K;
	
	public AbstractPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r) {
		this.b = new double[r];
		this.b[r - 1] = Double.POSITIVE_INFINITY;
		this.ranks = r;
		this.alpha = Maps.newHashMap();
		//for (T object : objects)
		//	this.alpha.put(object, 0.0);
		this.objects = objects;
		this.K = K;
	}
	
	public int rank(T object) {
		double sum = 0.0;
		//for (T o : this.objects) {
		for (Entry<T, Double> ae : this.alpha.entrySet()) {
			T key = ae.getKey();
			Double val = ae.getValue();
			sum += (val * this.K.get(object, key));
		}
		int ret = 0;
		while (ret < b.length && b[ret] <= sum) {
			ret++;
		}
		ret++;
		//System.out.println("sum is: " + sum + " and b is: " + DebugUtils.toString(b) + ",  returning " + ret);
		return ret;
	}
	
	public abstract void train(List<ObjectRank<T>> stream) throws Exception;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("AbstractPerceptronRanker [alpha=\n");
		for (Entry<T, Double> entry : alpha.entrySet())
			sb.append(entry + "\n");
		sb.append(", b=" + Arrays.toString(b) + ", objects=" + objects + "]");
		return sb.toString();
	}
	
}
