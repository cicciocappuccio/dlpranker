package metrics;

import java.util.List;

public abstract class ErrorMetric {

	public ErrorMetric() { }
	
	public double error(List<Integer> real, List<Integer> predicted) {
		if (real.size() != predicted.size())
			throw new IllegalArgumentException("#Real rankings != #Predicted rankings");
		return _error(real, predicted);
	}
	
	protected abstract double _error(List<Integer> real, List<Integer> predicted);
	
}
