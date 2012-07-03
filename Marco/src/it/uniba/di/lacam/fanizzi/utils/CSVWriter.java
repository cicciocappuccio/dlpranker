package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

	private FileWriter writer;

	public CSVWriter(FileWriter writer) {
		super();
		this.writer = writer;
	}

	public CSVWriter(String sFileName) throws IOException {
		super();
		this.writer = new FileWriter(sFileName);
	}

	public void append(String a) throws IOException {
		writer.append(a + ',');
	}

	public void newRow() throws IOException {
		writer.append('\n');
	}

	private void end() throws IOException {

		writer.flush();
		writer.close();

	}
}
