package perceptron;

import java.util.Set;

import com.google.common.collect.Table;

public class OnLineKernelPerceptronRanker<T> extends AbstractPerceptronRanker<T> {

	public OnLineKernelPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r) {
		super(objects, K, r);
	}

	public void feed(ObjectRank<T> objectRank) {
		T o = objectRank.getObject();
		int r = objectRank.getRank();
		int y = rank(o);
	
		double _alpha = 0.0;
		if (y != r)
			_alpha = (alpha.containsKey(o) ? alpha.get(o) : 0.0);
		
		if (y < r) {
			alpha.put(o, _alpha + (double)(r - y));
			for (int z = (y - 1); z < (r - 1); z++)
				b[z] = b[z] - 1.0;
		} else if (y > r) {
			alpha.put(o, _alpha + (double)(r - y));
			for (int z = (r - 1); z < (y - 1); z++) {
				b[z] = b[z] + 1.0;
			}
		}
	}
	

}
