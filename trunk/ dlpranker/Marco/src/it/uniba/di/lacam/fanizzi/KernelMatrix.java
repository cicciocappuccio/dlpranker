package it.uniba.di.lacam.fanizzi;


import java.util.Map;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance2;
import it.uniba.di.lacam.fanizzi.utils.CSVWriter;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class KernelMatrix {

	private Table<OWLNamedIndividual,OWLNamedIndividual,Double> kernel;
	

	public KernelMatrix()
	{
		
	}
	
	
	public Table<OWLNamedIndividual,OWLNamedIndividual,Double> createKernelMatrix(FeaturesDrivenDistance2 features)
	{
		int numExamples = features.getIndividualsLength();
		kernel = HashBasedTable.create();
		
		OWLNamedIndividual[] individui = features.getIndividuals();
		
		System.out.println("Computing kernel matrix");

		for (int i = 0; i < numExamples; ++i)
		{
//			System.out.println("done.\n" + i);
			kernel.put(individui[i], individui[i], 1.0);
//			kernel[i][i] = 1.0;
			for (int j = i + 1; j < numExamples; ++j)
			{
				Double kValue = 1 - features.sqrDistance(i, j);
				
				kernel.put(individui[i], individui[j], kValue);
				kernel.put(individui[j], individui[i], kValue);
				
//				kernel[i][j] = kValue;
//				kernel[j][i] = kValue;
			}
		}

		System.out.println("Finished creating the Kernel Matrix.\n");
		return kernel;
	}
	
	public int rank(OWLNamedIndividual e, Map<OWLNamedIndividual, Double> wc, double[] thetac, int nRatings)
	{
		
//		System.out.println("ran(" + e + ", " + wc.toString() + ", " + thetac.toString() + ", " + nRatings + ") ..");
//		System.out.println("ran(" + e );
		
		int ymin = nRatings-1;
		int y = nRatings-1;
		do {
			--y;
			double f = 0;
			for (OWLNamedIndividual i : wc.keySet())
			{
//				System.out.println("wc[i]: " + wc.get(i) + "    kernel[i][e]: " + kernel.get(i, e));
				f += wc.get(i) * kernel.get(i, e);
			}
			if (f < thetac[y])
				ymin = y;
		} while (y == ymin && y>0);
		
//		System.out.println("ymin: " + ymin);
		
		return ymin;
	}
	
	public void CSVPrint ()
	{
		CSVWriter.write("res/kernelMatrix.csv", kernel);
	}
	
}
