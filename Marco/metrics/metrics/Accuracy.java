package metrics;

import java.util.Iterator;
import java.util.List;

public class Accuracy extends AbstractErrorMetric {

	XXX NOT AN ERROR METRIC
	
	@Override
	protected double _error(List<Integer> real, List<Integer> predicted) {
		
		double right = 0.0;
		double total = 0.0;
		
		Iterator<Integer> itr = real.iterator();
		Iterator<Integer> itp = predicted.iterator();
		
		while (itr.hasNext() && itp.hasNext()) {
			int r = itr.next();
			int p = itp.next();
			if (r == p) {
				right += 1.0;
			}
			total += 1.0;
		}
		
		return right / total;
	}

}