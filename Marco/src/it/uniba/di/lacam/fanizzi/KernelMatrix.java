package it.uniba.di.lacam.fanizzi;


import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistanceD;
import it.uniba.di.lacam.fanizzi.utils.CSVWriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class KernelMatrix {

	private Table<Individual,Individual,Double> kernel;
	
	public KernelMatrix()
	{
		
	}
	
	public Table<Individual,Individual,Double> createKernelMatrix(FeaturesDrivenDistanceD features)
	{
		kernel = HashBasedTable.create();
		
		Set<Individual> individui = features.getIndividuals();
		Set<Individual> toCheck = new HashSet<Individual>(individui);
		
		System.out.println("Computing kernel matrix");
		for (Individual i : individui)
		{
			for (Individual j : toCheck)
			{
				Double kValue = 1 - features.sqrDistance(i, j);
				//System.out.println(kValue + " - " + i + " - " + j);
				kernel.put(i, j, kValue);
				kernel.put(j, i, kValue);
			}
			
			toCheck.remove(i);
		}

		System.out.println("Finished creating the Kernel Matrix.\n");
		return kernel;
	}
	
	public int rank(Individual e, Map<Individual, Double> wc, double[] thetac, int nRatings)
	{
		
		int ymin = nRatings-1;
		int y = nRatings-1;
		do {
			--y;
			double f = 0;
			for (Individual i : wc.keySet())
			{
				f += wc.get(i) * kernel.get(i, e);
			}
			if (f < thetac[y])
				ymin = y;
		} while (y == ymin && y>0);
		
		return ymin;
	}
	
	
	
	
	
	public int rank2(Individual e, Map<Individual, Double> wc, double[] thetac, int nRatings)
	{
		
		double sum = 0.0;
		for (Individual i : wc.keySet()) {
			sum += (wc.get(i) * kernel.get(i, e));
		}
		int ret = 0;
		while (thetac[ret] <= sum) {
			ret++;
		}
		ret++;
		//System.out.println("sum is: " + sum + " and b is: " + DebugUtils.toString(b) + ",  returning " + ret);
		return ret;
	}
	
	
	
	
	
	public void CSVPrint ()
	{
		CSVWriter.write("res/kernelMatrix.txt", kernel);
	}
}
