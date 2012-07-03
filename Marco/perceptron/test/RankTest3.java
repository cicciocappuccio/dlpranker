package test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class RankTest3 {

	private static double[][] DATAPOINTS;
	private static int[] LABELS;

	public static void main(String[] args) {
		List<ObjectRank<double[]>> stream = new LinkedList<ObjectRank<double[]>>();
		
		final int n = 100;
		DATAPOINTS = new double[n][2];
		LABELS = new int[n];
		
		Random prng = new Random(0);
		
		for (int i = 0; i < n; ++i) {
			double x1 = prng.nextDouble();
			double x2 = prng.nextDouble();
			double eval = x1 * 10.0 - x2 * 8.0;
			boolean label = (eval >= 0);
			LABELS[i] = (label ? 1 : 2);
			DATAPOINTS[i][0] = x1;
			DATAPOINTS[i][1] = x2;
		}

		for (int i = 0; i < n; ++i) {
			stream.add(new ObjectRank<double[]>(DATAPOINTS[i], LABELS[i]));
		}
		
		Set<double[]> objects = Sets.newHashSet(DATAPOINTS);
		Table<double[], double[], Double> K = HashBasedTable.create();
		for (double[] xi : objects) {
			for (double[] xj : objects) {
				K.put(xi, xj, kernel(xi, xj));
			}
		}
		
		OnLineKernelPerceptronRanker<double[]> ranker = new OnLineKernelPerceptronRanker<double[]>(objects, K, 2);

		for (ObjectRank<double[]> or : stream) {
			ranker.feed(or);
		}
		
		for (int i = 0; i < n; ++i) {
			System.out.println("DATAPOINT: " + i);
			System.out.println("\tReal rank: " + LABELS[i]);
			System.out.println("\tPredicted rank: " + ranker.rank(DATAPOINTS[i]));
		}
		
		System.out.println(ranker);
	}

	private static double kernel(double[] x1, double[] x2) {
		double ret = 0.0;
		for (int i = 0; i < Math.max(x1.length, x2.length); ++i)
			ret += (x1[i] * x2[i]);
		return ret;
	}
	
}
