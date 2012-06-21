package test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.semanticweb.owlapi.model.OWLNamedIndividual;



public class KernelPerceptronRank {

	private static final double[][] DATAPOINTS_1 = { {3, 2}, {4, 1}, {5, 2}, {6, 4}, {7, 3} };
	private static final double[][] DATAPOINTS_2 = { {1, 5}, {2, 5}, {2, 6}, {3, 6}, {5, 6} };
	
	private static final double[][] DATAPOINTS = ArrayUtils.addAll(DATAPOINTS_1, DATAPOINTS_2);
	
	static final double THRESHOLD = .1;
	
	private static double kernel(double[] x1, double[] x2) {
		double ret = 0.0;
		for (int i = 0; i < Math.max(x1.length, x2.length); ++i) {
			ret += (x1[i] * x2[i]);
		}
		return ret;
	}
	
	private static double[] alpha = null;
	private static double[] b = null;
	
	private static void kernelPerceptronRank(List<PointRank> stream) {
		alpha = new double[DATAPOINTS.length];
		b = new double[2];
		
		for (int i = 0; i < alpha.length; i++)
			alpha[i] = 0;
		
		for (int i = 0; i < b.length; i++)
			b[i] = 0;
		
		b[b.length-1] = Double.POSITIVE_INFINITY;
		double avgLoss = 0;
		do
		{
			avgLoss = 0;
			
			for(int i = 0; i < stream.size(); i++)
			{
				int ySegnato = rank(alpha, b, stream.get(i).x);
				if (ySegnato != stream.get(i).y)
				{
					avgLoss += Math.abs(stream.get(i).y - ySegnato);
					alpha[i] += (stream.get(i).y - ySegnato);
					
					for (int j = (int) Math.min(ySegnato, stream.get(i).y); j < Math.max(ySegnato, stream.get(i).y) - 1; i++)
						b[j] -= 1;
				}
			}
			
			avgLoss /= (double)stream.size();
		}while(avgLoss>THRESHOLD);
	}
	
	private static int rank(double[] alpha, double[] thetac, double[] x)
	{
		int ymin = nRatings-1;
		int y = nRatings-1;
		do {
			--y;
			double f = 0;
			for (int i = 0; i < alpha.length; i++)
			{
				f += alpha[i] * kernel(x, DATAPOINTS[i]);
			}
			if (f < thetac[y])
				ymin = y;
		} while (y == ymin && y>0);
		return ymin;
	}
	
	
	public static void main(String[] args) {
		List<PointRank> stream = new LinkedList<PointRank>();
		for (double[] point : DATAPOINTS_1) {
			stream.add(new PointRank(point, 1));
		}
		for (double[] point : DATAPOINTS_2) {
			stream.add(new PointRank(point, 2));
		}
		
		Random prng = new Random(0);
		Collections.shuffle(stream, prng);
		kernelPerceptronRank(stream);
		
		System.out.println(rank(alpha, b, DATAPOINTS_2[0]));
		System.out.println(rank(alpha, b, DATAPOINTS_1[3]));
		}

	
}
