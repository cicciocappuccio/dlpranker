package metrics;

import java.util.Iterator;
import java.util.List;

public class SpearmanCorrelationCoefficient extends ErrorMetric {

	@Override
	protected double _error(List<Integer> real, List<Integer> predicted) {

		final double n = real.size();
		double sumDi = 0.0;

		Iterator<Integer> itr = real.iterator();
		Iterator<Integer> itp = predicted.iterator();

		while (itr.hasNext() && itp.hasNext()) {
			double r = itr.next();
			double p = itp.next();
			sumDi = Math.pow((r - p), 2);
		}
		
		double scc = 1 - ((6 * sumDi) / (n * (n * n - 1)));
		
		return scc;
	}
}