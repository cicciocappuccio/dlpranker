

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

class PointRank {
	public double[] x;
	public double y;
	public PointRank(double[] x, double y) {
		this.x = x;
		this.y = y;
	}
}

public class OnLineKernelPerceptronRankingTest {

	private static final double[][] DATAPOINTS_1 = { {3, 2}, {4, 1}, {5, 2}, {6, 4}, {7, 3} };
	private static final double[][] DATAPOINTS_2 = { {1, 5}, {2, 5}, {2, 6}, {3, 6}, {5, 6} };
	
	private static final double[][] DATAPOINTS = ArrayUtils.addAll(DATAPOINTS_1, DATAPOINTS_2);
	
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
		b[1] = Double.POSITIVE_INFINITY;
		
		for (PointRank pointRank : stream) {
			int indexInAlpha = 0;
			
			for (int j = 0; j < alpha.length; ++j)
				if (pointRank.x == DATAPOINTS[j])
					indexInAlpha = j;
			
			int yi = rank(alpha, b, pointRank.x);
			
			if (yi < pointRank.y){
				alpha[indexInAlpha] += pointRank.y - yi;

				for (int z = (int) pointRank.y; z < (yi-1); z++)
					b[z]--;
			} else if (yi > pointRank.y){
				alpha[indexInAlpha] += pointRank.y - yi;
				
				for (int z = yi; z < (pointRank.y-1); z++)
					b[z]++;
			}
		}
	}
	
	private static int rank(double[] alpha, double[] b, double[] x)
	{
		int ret = 2;
		
		double sum = 0.0;
		for (int i = 0; i < DATAPOINTS.length; ++i) {
			double kxxi = kernel(x, DATAPOINTS[i]);
			sum += (alpha[i] * kxxi);
		}
		
		if (sum < b[0])
			ret = 1;
		
		return ret;
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
	}

	
}
