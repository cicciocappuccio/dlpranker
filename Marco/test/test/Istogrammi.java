package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.utils.CSVW;

public class Istogrammi {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String urlOwlFile = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);
		
		File outFile = new File("res/istogrammi.csv");
		if (outFile.exists())
			outFile.delete();
		
		PrintWriter pw = new PrintWriter(outFile);
		CSVW csv = new CSVW(pw);
		
		List<String> headRow = Lists.newArrayList();
		
		headRow.add("Film");
		headRow.add("1");
		headRow.add("2");
		headRow.add("3");
		headRow.add("4");
		headRow.add("5");
		
		csv.write(headRow);

		Table<String, Integer, Integer> a = dati.getDist();

		
		for (String i : a.rowKeySet())
		{
			List<String> row = Lists.newArrayList();
			
			row.add(i);
			for (int y = 1; y < 6; y++)
			{
				row.add(a.get(i, y).toString());
			}
			csv.write(row);
		}
		csv.close();
	}

}
