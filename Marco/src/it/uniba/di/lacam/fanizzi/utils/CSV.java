package it.uniba.di.lacam.fanizzi.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSV {
	
	private CSV() { }
	
	public static Collection<String[]> read(InputStreamReader isr) throws IOException { 
		CsvReader csvReader = new CsvReader(isr, ';');
		Collection<String[]> ret = new LinkedList<String[]>();
		while (csvReader.readRecord()) {
			ret.add(csvReader.getValues());
		}
		csvReader.close();
		return ret;
	}
	
	public static void write(PrintWriter printWriter, Collection<String> tuple) throws IOException {
		write(printWriter, tuple.toArray(new String[tuple.size()]));
	}
	
	public static void write(PrintWriter printWriter, String[] tuple) throws IOException {
		CsvWriter csvWriter = new CsvWriter(printWriter, ';');
		csvWriter.writeRecord(tuple);
		csvWriter.close();
	}
	
}
