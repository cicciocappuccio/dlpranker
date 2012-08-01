package metrics;

import java.util.List;

public abstract class AbstractMetric {

	public enum MetricType { AccuracyError, MAE, RMSE, Spearman };
	
	public AbstractMetric() { }
	
	public double error(List<Integer> real, List<Integer> predicted) {
		if (real.size() != predicted.size())
			throw new IllegalArgumentException("#Real rankings != #Predicted rankings");
		return _error(real, predicted);
	}
	
	protected abstract double _error(List<Integer> real, List<Integer> predicted);
	
	public static AbstractMetric getErrorMetric(MetricType type) {
		AbstractMetric ret = null;
		
		switch (type) {
		case AccuracyError: {
			ret = new AccuracyError();
		} break;
		case MAE: {
			ret = new MAE();
		} break;
		case RMSE: {
			ret = new RMSE();
		} break;
		case Spearman: {
			ret = new SpearmanCorrelationCoefficient();
		} break;
		}
		
		return ret;
	}
}
