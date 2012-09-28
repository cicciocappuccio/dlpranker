package utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CSV {
	
	private PrintWriter pw;
	private CsvWriter writer;
	
	public CSV(PrintWriter pw) {
		this.pw = pw;
		this.writer = new CsvWriter(this.pw, ';');
	}
	
	public void write(Collection<String> tuple) throws IOException {
		write(tuple.toArray(new String[tuple.size()]));
	}
	
	public void write(String[] tuple) throws IOException {
		this.writer.writeRecord(tuple);
		this.writer.flush();
	}
	
	//public void writeArgs(String ...tuple) throws IOException {
	//	this.writer.writeRecord(tuple);
	//	this.writer.flush();
	//}
	
	public void writeArgs(Object ..._tuple) throws IOException {
		String[] tuple = new String[_tuple.length];
		for (int i = 0; i < _tuple.length; ++i) {
			tuple[i] = String.valueOf(_tuple[i]);
		}
		this.writer.writeRecord(tuple);
		this.writer.flush();
	}
	
	public void close() {
		this.writer.close();
	}

	public static Collection<String[]> read(InputStreamReader isr) throws IOException { 
		CsvReader csvReader = new CsvReader(isr, ';');
		Collection<String[]> ret = new LinkedList<String[]>();
		while (csvReader.readRecord()) {
			ret.add(csvReader.getValues());
		}
		csvReader.close();
		return ret;
	}
	
	public static Map<String, List<String>> readMap(InputStreamReader isr) throws IOException { 
		Collection<String[]> content = read(isr);
		
		BiMap<Integer, String> indices = HashBiMap.create();
		Map<String, List<String>> ret = Maps.newHashMap();
		
		int line = 0;
		for (String[] l : content) {
			if (line == 0) {
				int column = 0;
				for (String c : l) {
					indices.put(column++, c);
					List<String> list = Lists.newLinkedList();
					ret.put(c, list);
				}
			} else {
				int column = 0;
				for (String c : l) {
					String key = indices.get(column);
					ret.get(key).add(c);
					column++;
				}
			}
			line++;
		}
		
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
