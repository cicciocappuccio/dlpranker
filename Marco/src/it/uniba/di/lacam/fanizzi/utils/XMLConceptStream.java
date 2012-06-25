package it.uniba.di.lacam.fanizzi.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLConceptStream {

	private static String urlFile = "res/online.xml";
	
	public static void appendi(Description concetto) {
		Set<Description> insieme = leggi();
		insieme.add(concetto);
		scrivi(insieme);
	}

    @SuppressWarnings("unchecked")
	public static Set<Description> leggi() {
        XStream xs = new XStream(new DomDriver());
        Set<Description> insieme = new HashSet<Description>();

        try {
            FileInputStream fis = new FileInputStream(urlFile);
            insieme = (Set<Description>) xs.fromXML(fis);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return insieme;
    }
    
    
    public static void scrivi(Set<Description> insieme) {
    	

        //Serialize the object
        XStream xs = new XStream();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(urlFile);
            xs.toXML(insieme, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
