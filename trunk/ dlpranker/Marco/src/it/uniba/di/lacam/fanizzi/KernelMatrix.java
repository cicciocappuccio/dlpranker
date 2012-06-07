package it.uniba.di.lacam.fanizzi;


import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance;
import it.uniba.di.lacam.fanizzi.utils.CSVWriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class KernelMatrix {

	private Table<OWLNamedIndividual,OWLNamedIndividual,Double> kernel;
	
	public KernelMatrix()
	{
		
	}
	
	public Table<OWLNamedIndividual,OWLNamedIndividual,Double> createKernelMatrix(FeaturesDrivenDistance features)
	{
		kernel = HashBasedTable.create();
		
		Set<OWLNamedIndividual> individui = features.getIndividuals();
		Set<OWLNamedIndividual> toCheck = new HashSet<OWLNamedIndividual>(individui);
		
		System.out.println("Computing kernel matrix");
		for (OWLNamedIndividual i : individui)
		{
			for (OWLNamedIndividual j : toCheck)
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
/*
  				System.out.println(kernel.get(i, e));
 				System.out.println("f: " + f);
				System.out.println("wc(i): " + wc.get(i));
				System.out.println("i: " + i);
				System.out.println("e: " + e);
*/
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
		CSVWriter.write("res/kernelMatrix.txt", kernel);
	}
}
