package perceptron;

import it.uniba.di.lacam.fanizzi.utils.DebugUtils;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class OnLineKernelPerceptronRanker<T> {

	private Map<T, Double> alpha;
	private double[] b;
	
	private Set<T> objects;
	private Table<T, T, Double> K;
	
	public OnLineKernelPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r) {
		this.b = new double[r];
		this.b[r - 1] = Double.POSITIVE_INFINITY;
		this.alpha = Maps.newHashMap();
		for (T object : objects)
			this.alpha.put(object, 0.0);
		this.objects = objects;
		this.K = K;
	}
	
	public void feed(ObjectRank<T> objectRank) {
		T o = objectRank.getObject();
		int r = objectRank.getRank();
		int yi = rank(o);
		if (yi < r) {
			alpha.put(o, alpha.get(o) + r - yi);
			for (int z = r; z < (yi - 1); z++)
				b[z]--;
		} else if (yi > r) {
			alpha.put(o, alpha.get(o) + r - yi);
			for (int z = yi; z < (r - 1); z++) {
				b[z]++;
			}
		}
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
}
