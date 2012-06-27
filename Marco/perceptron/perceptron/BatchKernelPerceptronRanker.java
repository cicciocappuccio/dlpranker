package perceptron;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class BatchKernelPerceptronRanker<T> {
	
	static final double THRESHOLD = .5;
	
	private Map<T, Double> alpha;
	private double[] b;
	
	private Table<T, T, Double> K;
	
	public BatchKernelPerceptronRanker(Set<T> objects, Table<T, T, Double> K, int r)
	{
		this.alpha = Maps.newHashMap();
		for (T object : objects)
			this.alpha.put(object, 0.0);
		
		this.b = new double[r];
		for (int i = 0; i < b.length; i++)
			b[i] = 0;
		this.b[r - 1] = Double.POSITIVE_INFINITY;
		
		this.K = K;
	}
	
	
	public void kernelPerceptronRank(List<ObjectRank<T>> stream) {
		
		double avgLoss = 0;
		do
		{
			avgLoss = 0;
			
			for(int i = 0; i < stream.size(); i++)
			{
				int ySegnato = rank2(stream.get(i).getObject());
				if (ySegnato != stream.get(i).getRank())
				{
					avgLoss += Math.abs(stream.get(i).getRank() - ySegnato);
					alpha.put(stream.get(i).getObject(), alpha.get(stream.get(i).getObject()) + (stream.get(i).getRank() - ySegnato));
										
					for (int j = (int) Math.min(ySegnato, stream.get(i).getRank()); j < Math.max(ySegnato, stream.get(i).getRank()) - 1; i++)
						b[j] -= 1;
				}
			}
			
			avgLoss /= (double)stream.size();
		}while(avgLoss>THRESHOLD);
	}
	
	public int rank(T t)
	{
		int ymin = b.length - 1;
		int y = b.length - 1;
		do {
			--y;
			double f = 0;
			for (T i : alpha.keySet())
			{
				f += alpha.get(i) * K.get(t, i); // kernel(x, DATAPOINTS[i]);
			}
			if (f < b[y])
				ymin = y;
		} while (y == ymin && y>0);
		return ymin;
	}
	
	public int rank2(T object) {
		double sum = 0.0;
		for (T o : this.alpha.keySet()) {
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
