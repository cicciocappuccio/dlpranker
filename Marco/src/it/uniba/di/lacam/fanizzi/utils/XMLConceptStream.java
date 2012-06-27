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

	private static String urlFile = "res/concept/concept";
	private static String[] urlFileMode = {"", ".1.xml", ".2.xml", ".3.xml", ".4.xml"};
	/*
	 * 0 unsign
	 * 1 = climbing
	 * 2 = subSuperClass
	 * 3 = 
	 * 4 = 
	 * 
	 * */
	 
	
	public static void appendi(Description concetto, int mode) {
		Set<Description> insieme = leggi(mode);
		insieme.add(concetto);
		scrivi(insieme, mode);
	}

    @SuppressWarnings("unchecked")
	public static Set<Description> leggi(int mode) {
        XStream xs = new XStream(new DomDriver());
        Set<Description> insieme = new HashSet<Description>();

        try {
            FileInputStream fis = new FileInputStream(urlFile + urlFileMode[mode]);
            insieme = (Set<Description>) xs.fromXML(fis);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return insieme;
    }
    
    
    public static void scrivi(Set<Description> insieme, int mode) {
    	//Serialize the object
        XStream xs = new XStream();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream(urlFile + urlFileMode[mode]);
            xs.toXML(insieme, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
