package utils;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.Table;

public class PrintUtils {

	public static void printKernel(Table<Individual, Individual, Double> k)
	{
		for (Individual i : k.columnKeySet())
		{
			for (Individual j : k.rowKeySet())
			{
				System.out.println(i + " - " + j + " - " + k.get(i, j).toString());
			}
		}
	}
}
