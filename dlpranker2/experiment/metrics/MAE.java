package metrics;

import java.util.Iterator;
import java.util.List;

public class MAE extends AbstractMetric {

	@Override
	protected double _error(List<Integer> real, List<Integer> predicted) {
		final double n = real.size();
		Iterator<Integer> itr = real.iterator();
		Iterator<Integer> itp = predicted.iterator();
		double sum = 0.0;
		while (itr.hasNext() && itp.hasNext()) {
			double r = itr.next();
			double p = itp.next();
			sum += Math.abs(r - p);
		}
		double mae = sum / n;
		return mae;
	}

}