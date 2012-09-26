package utils;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import dataset.Tupla;

public class XMLFilmRatingStream {

	private static String urlFile = "res/FilmRating.xml";
//	private static String urlFile = "res/FilmRatingBalance.xml";
//	private static String urlFileOut = "res/FilmRatingBalance.xml";

	@SuppressWarnings("unchecked")
	public static List<Tupla> leggi() {
		XStream xs = new XStream(new DomDriver());

		List<Tupla> lista = Lists.newArrayList();

		try {
			FileInputStream fis = new FileInputStream(urlFile);
			lista = (List<Tupla>) xs.fromXML(fis);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			lista = null;
		}

		return lista;
	}
    
    public static void scrivi(List<Tupla> lista) {
        
        //Serialize the object
        XStream xs = new XStream(new DomDriver());

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(urlFile);
            xs.toXML(lista, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }


}
