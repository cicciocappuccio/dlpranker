package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.Table;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

public class CSVWriter {

	public static void write(String sFileName, Table<OWLNamedIndividual, OWLNamedIndividual, Double> tabella)
	{
		Map<OWLNamedIndividual, Map<OWLNamedIndividual, Double>> columnMap = tabella.columnMap();
		Set<OWLNamedIndividual> keys = columnMap.keySet();

		try {
			FileWriter writer = new FileWriter(sFileName);
			writer.append("");
			
			for (OWLNamedIndividual hC : keys)
			{
				writer.append(',');
				writer.append(hC.toString());
			}
			writer.append('\n');
			
			for (OWLNamedIndividual hCi : keys) // righe
			{
				writer.append(hCi.toString());
				writer.append(',');
				for (OWLNamedIndividual hCy : keys) //colonne
				{
					writer.append( tabella.get(hCi, hCy).toString());
					writer.append(',');
				}
				writer.append('\n');
			}



			// generate whatever data you want

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
