package test;

import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.Set;

import org.dllearner.core.owl.Description;

public class TestRead {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		int mode = 2;
		
		Set<Description> descriptionD = XMLConceptStream.leggi(mode);
		
		for (Description i : descriptionD)
			System.out.println(i);
		
	}

}
