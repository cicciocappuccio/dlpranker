package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLPi {

	private static String urlFile = "res/PiTable.xml";

	@SuppressWarnings("unchecked")
	public static Table<Description, Individual, Short> leggi()
			throws FileNotFoundException {
		XStream xs = new XStream(new DomDriver());

		Table<Description, Individual, Short> ratings = HashBasedTable.create();

		FileInputStream fis = new FileInputStream(urlFile);
		ratings = (Table<Description, Individual, Short>) xs.fromXML(fis);

		return ratings;
	}

	public static void scrivi(Table<Description, Individual, Short> ratings)
			throws FileNotFoundException {

		// Serialize the object
		XStream xs = new XStream();

		// Write to a file in the file system

		FileOutputStream fs = new FileOutputStream(urlFile);
		xs.toXML(ratings, fs);

	}

}
