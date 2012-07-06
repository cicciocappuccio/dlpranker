package test;

import it.uniba.di.lacam.fanizzi.utils.CSV;
import it.uniba.di.lacam.fanizzi.utils.CSVW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;

public class CSVTest {
	public static void main(String[] args) throws IOException {
		File outFile = new File("res/risultati_test.csv");
		if (outFile.exists())
			outFile.delete();

		PrintWriter pw = new PrintWriter(outFile);
		CSVW csv = new CSVW(pw);
		
		List<String> methods = Lists.newArrayList("Linear", "Gaussian", "Polynomial");
		List<String> headRow = Lists.newArrayList();

		headRow.add("h");

		for (String method : methods)
			headRow.add(method + " MAE");

		for (String method : methods)
			headRow.add(method + " RMSE");

		for (String method : methods)
			headRow.add(method + " Spearman");

		csv.write(headRow);
		
		System.in.read();
		
		Double pValue = 0.1;
		
		Double lmae = 0.1;
		Double gmae = 0.1;
		Double pmae = 0.1;

		Double lrmse = 0.1;
		Double grmse = 0.1;
		Double prmse = 0.1;
		
		Double lscc = 0.1;
		Double gscc = 0.1;
		Double pscc = 0.1;
		
		
			
		List<String> row = Lists.newLinkedList();
		
		row.add(pValue.toString());
		
		row.add(lmae.toString());
		row.add(gmae.toString());
		row.add(pmae.toString());

		row.add(lrmse.toString());
		row.add(grmse.toString());
		row.add(prmse.toString());

		row.add(lscc.toString());
		row.add(gscc.toString());
		row.add(pscc.toString());
		
		csv.write(row);
		csv.close();
	
	}
}
