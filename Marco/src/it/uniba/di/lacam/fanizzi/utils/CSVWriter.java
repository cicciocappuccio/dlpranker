package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.Table;

public class CSVWriter {

	public static void write(String sFileName, Table<Individual, Individual, Double> tabella)
	{
		Map<Individual, Map<Individual, Double>> columnMap = tabella.columnMap();
		Set<Individual> keys = columnMap.keySet();

		try {
			FileWriter writer = new FileWriter(sFileName);
			writer.append("");
			
			for (Individual hC : keys)
			{
				writer.append('\t');
				writer.append(hC.toString());
			}
			writer.append('\n');
			
			for (Individual hCi : keys) // righe
			{
				writer.append(hCi.toString());
				writer.append('\t');
				for (Individual hCy : keys) //colonne
				{
					writer.append( tabella.get(hCi, hCy).toString());
					writer.append('\t');
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
	
	public static void write2(String sFileName, Table<Description, Individual, Short> tabella)
	{
//		Map<OWLNamedIndividual, Map<OWLClassExpression, Short>> columnMap = tabella.columnMap();
//		Set<OWLNamedIndividual> keys = columnMap.keySet();

		Set<Description> keyRows = tabella.rowKeySet();
		Set<Individual> keyColumns = tabella.columnKeySet();
		
		try {
			FileWriter writer = new FileWriter(sFileName);
			writer.append("");
			
			for (Individual hC : keyColumns)
			{
				writer.append('\t');
				writer.append(hC.toString());
			}
			writer.append('\n');
			
			for (Description hCi : keyRows) // righe
			{
				writer.append(hCi.toString());
				writer.append('\t');
				for (Individual hCy : keyColumns) //colonne
				{
					writer.append( tabella.get(hCi, hCy).toString());
					writer.append('\t');
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
