package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLFilmRatingStream {

	private static String urlFile = "res/FilmRating.xml";
	
	@SuppressWarnings("unchecked")
	public static Table<Individual, Individual, Integer> leggi() {
        XStream xs = new XStream(new DomDriver());
        
        Table<Individual, Individual, Integer> ratings = HashBasedTable.create();
        
        try {
            FileInputStream fis = new FileInputStream(urlFile);
            ratings = (Table<Individual, Individual, Integer>) xs.fromXML(fis);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            ratings = null;
        }
        
        return ratings;
    }
    
    
    public static void scrivi(Table<Individual, Individual, Integer> ratings) {
    	

        //Serialize the object
        XStream xs = new XStream();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(urlFile);
            xs.toXML(ratings, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }


}
