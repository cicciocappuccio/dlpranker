package perceptron;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public abstract class AbstractPerceptronRanker<T> {

	protected Map<T, Double> alpha;
	protected double[] b;
	
	protected Set<T> objects;
	protected Table<T, T, Double> K;
	
	public AbstractPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r) {
		this.b = new double[r];
		this.b[r - 1] = Double.POSITIVE_INFINITY;
		this.alpha = Maps.newHashMap();
		for (T object : objects)
			this.alpha.put(object, 0.0);
		this.objects = objects;
		this.K = K;
	}
	
	public int rank(T object) {
		double sum = 0.0;
		for (T o : this.objects) {
			sum += (this.alpha.get(o) * this.K.get(object, o));
		}
		int ret = 0;
		while (b[ret] <= sum) {
			ret++;
		}
		ret++;
		//System.out.println("sum is: " + sum + " and b is: " + DebugUtils.toString(b) + ",  returning " + ret);
		return ret;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("AbstractPerceptronRanker [alpha=\n");
		for (Entry<T, Double> entry : alpha.entrySet())
			sb.append(entry + "\n");
		sb.append(", b=" + Arrays.toString(b) + ", objects=" + objects + "]");
		return sb.toString();
	}
	
}
