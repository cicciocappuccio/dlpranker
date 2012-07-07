package metrics;

import java.util.List;

public class AccuracyError extends Accuracy{
	@Override
	protected double _error(List<Integer> real, List<Integer> predicted) {
		
		return (1 - super._error(real, predicted));
	}
}
