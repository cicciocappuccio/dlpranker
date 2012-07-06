package it.uniba.di.lacam.fanizzi.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import com.csvreader.CsvWriter;

public class CSVW {
	
	private CsvWriter csvWriter;
	
	public CSVW(PrintWriter printWriter) { 
		csvWriter = new CsvWriter(printWriter, ';');
	}
	
	public void write(Collection<String> tuple) throws IOException  {
		write(tuple.toArray(new String[tuple.size()]));
	}
	
	public void write(String[] tuple) throws IOException  {
		
		csvWriter.writeRecord(tuple);
		csvWriter.flush();
	}

	public void close()  {
		csvWriter.close();
	}
	
}
