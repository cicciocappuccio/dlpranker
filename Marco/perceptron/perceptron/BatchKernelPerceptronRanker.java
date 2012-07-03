package perceptron;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class BatchKernelPerceptronRanker<T> extends AbstractPerceptronRanker<T> {
	
	public static final double THRESHOLD = .1;
	
	public BatchKernelPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r) {
		super(objects, K, r);
	}
	
	public void train(List<ObjectRank<T>> stream) {

		double avgLoss = 0;
		do {
			avgLoss = 0;

			for (int i = 0; i < stream.size(); i++) {
				int ySegnato = rank(stream.get(i).getObject());
				if (ySegnato != stream.get(i).getRank()) {
					avgLoss += Math.abs(stream.get(i).getRank() - ySegnato);
					alpha.put(stream.get(i).getObject(), alpha.get(stream.get(i).getObject()) + (stream.get(i).getRank() - ySegnato));

					for (int j = (int) Math.min(ySegnato, stream.get(i).getRank()); j < Math.max(ySegnato, stream.get(i).getRank()) - 1; j++)
						b[j] -= 1;
				}
			}

			avgLoss /= (double) stream.size();
			System.out.println(avgLoss);
		} while (avgLoss > THRESHOLD);
	}

}
