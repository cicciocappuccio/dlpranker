package test;

import it.uniba.di.lacam.fanizzi.utils.SerializeUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.dllearner.core.owl.Description;

public class XMLReader {

	public static void main(String[] args) throws Exception
	{
		FileInputStream fin;            

		
		    // Open an input stream
		    fin = new FileInputStream ("file.xml");
		    SerializeUtils su = new SerializeUtils();
		    //I don't know what to put below this, to read FileInpuStream object fin
		    String xml = fin.toString();
		    Set<Description> dexml = (Set<Description>)su.deserialize(xml);

		        System.out.println(dexml);

		    // Close our input stream
		    fin.close();        
	}
}
