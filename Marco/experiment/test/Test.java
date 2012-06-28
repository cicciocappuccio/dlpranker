package test;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.Set;

import org.dllearner.core.owl.Description;

import features.AllPrimitivesExists;
import features.Climbing;
import features.FeaturesSelection;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int mode = 3;
		
		/*
		 * 0 unsign
		 * 1 = climbing
		 * 2 = subSuperClass
		 * 3 = 
		 * 4 = 
		 * 
		 * */
		
		switch (mode)
		{
		case 1: 
			Climbing i = new Climbing();
			Set<Description> insieme = i.climbing();
			break;
		
		
		case 2:

			String urlOwlFile = "res/fragmentOntology10.owl";

			ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);
			
			Set<Description> descriptionD = FeaturesSelection.subSuperClass(urlOwlFile, dati.getIndividuals());
			
			XMLConceptStream.scrivi(descriptionD, 2);
			
			break;
		case 3:
			
			AllPrimitivesExists a = new AllPrimitivesExists();
			
			Set<Description> descriptionA = a.allPrimitivesExists();
			
			for (Description m : descriptionA)
				System.out.println(m);
			
			XMLConceptStream.scrivi(descriptionA, 3);
			
			
			break;
			default:
				break;
		}
		
		
		
		
		
		
		
	}

}
