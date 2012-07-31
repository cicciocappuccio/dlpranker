package test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import perceptron.LargeMarginBatchPerceptronRanker;
import perceptron.ObjectRank;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class RankTest2 {

	private static final double[][] DATAPOINTS_1 = { {3, 2}, {4, 1}, {5, 2}, {6, 4}, {7, 3} };
	private static final double[][] DATAPOINTS_2 = { {1, 5}, {2, 5}, {2, 6}, {3, 6}, {5, 6} };
	
	private static final double[][] DATAPOINTS = ArrayUtils.addAll(DATAPOINTS_1, DATAPOINTS_2);
	
	public static void main(String[] args) throws Exception {
		List<ObjectRank<double[]>> stream = new LinkedList<ObjectRank<double[]>>();
		
		for (double[] point : DATAPOINTS_1) {
			stream.add(new ObjectRank<double[]>(point, 1));
		}
		for (double[] point : DATAPOINTS_2) {
			stream.add(new ObjectRank<double[]>(point, 2));
		}
		
		Set<double[]> objects = Sets.newHashSet(DATAPOINTS);
		Table<double[], double[], Double> K = HashBasedTable.create();
		for (double[] xi : objects) {
			for (double[] xj : objects) {
				K.put(xi, xj, kernel(xi, xj));
			}
		}
		
		LargeMarginBatchPerceptronRanker<double[]> ranker = new LargeMarginBatchPerceptronRanker<double[]>(objects, K, 2);

		ranker.train(stream);

		System.out.println(ranker.rank(DATAPOINTS_1[1]));
		System.out.println(ranker.rank(DATAPOINTS_1[2]));
		System.out.println(ranker.rank(DATAPOINTS_1[3]));
		System.out.println(ranker.rank(DATAPOINTS_1[4]));
		System.out.println(ranker.rank(DATAPOINTS_1[0]));
		System.out.println(ranker.rank(DATAPOINTS_2[1]));
		System.out.println(ranker.rank(DATAPOINTS_2[2]));
		System.out.println(ranker.rank(DATAPOINTS_2[3]));
		System.out.println(ranker.rank(DATAPOINTS_2[4]));
		System.out.println(ranker.rank(DATAPOINTS_2[0]));
	}

	private static double kernel(double[] x1, double[] x2) {
		double ret = 0.0;
		for (int i = 0; i < Math.max(x1.length, x2.length); ++i)
			ret += (x1[i] * x2[i]);
		return ret;
	}
	
}
