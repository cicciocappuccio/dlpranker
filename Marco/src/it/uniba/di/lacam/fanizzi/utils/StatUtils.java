package it.uniba.di.lacam.fanizzi.utils;

import java.util.Set;

public class StatUtils {

	private StatUtils() {}

	 /**
	  * calcola la media
	  * @param v: array di cui calcolare la media
	  * @return restituisce la media
	  * */
	 public static double avg(double[] v) {
		 double acc = 0;
		 for (int f=0; f < v.length; ++f)
			 acc += + v[f];
		 return acc / v.length;
	 }
	 
	 
	 
	public static double stdDeviation(double[] population) {
		return Math.sqrt(variance(population));
	}
	
	
	public static int max(int[] t) {
	    int maximum = t[0];   // start with the first value
	    for (int i=1; i<t.length; i++) {
	        if (t[i] > maximum) {
	            maximum = t[i];   // new maximum
	        }
	    }
	    return maximum;
	}//end method max

	public static double max(Set<Double> t)
	{
	    
		double maximum = Double.NEGATIVE_INFINITY;
		for (Double i : t)
		{
			if (i > maximum)
				maximum = i;
		}
		return maximum;
	}//end method max

	public static double variance(double[] population) {
		long n = 0;
		double mean = 0;
		double s = 0.0;

		for (double x : population) {
			n++;
			double delta = x - mean;
			mean += delta / n;
			s += delta * (x - mean);
		}

		return (s / (n-1));
	}
	

}
