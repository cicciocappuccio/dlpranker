package test;

import features.FeaturesSelection;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.GreedyForward;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.Collections;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int mode = 1;
		
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
			String file = "res/fragmentOntology10.owl";

			int maxLength = 3;

			KnowledgeSource ks = new OWLFile(file);
			AbstractReasonerComponent reasoner = new OWLAPIReasoner(
					Collections.singleton(ks));
			try {
				reasoner.init();
			} catch (ComponentInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			AbstractConceptCache cache = null;
			
			Psi2DownWrapper r = new Psi2DownWrapper(reasoner);
			
			try {
				reasoner.init();
				cache = new AsynchronousHibernateConceptCache(file);
				r.init();
			} catch (ComponentInitException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Reasoner creato");

			Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
			Set<Individual> films = reasoner.getIndividuals(Film);


			System.out.println("Films selezionati\nchiamo GreedyForward");

			AbstractScore tScore = new MHMRScore(cache, reasoner, 0.8);

			GreedyForward pioniere = new GreedyForward(cache, reasoner, r,
					maxLength);

			Set<Description> risultato = pioniere.estrazione(Thing.instance, films,
					tScore);

			for (Description j : risultato)
				System.out.println(j);
			break;
		
		
		case 2:

			String urlOwlFile = "res/fragmentOntology10.owl";

			ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);
			System.out.println("Numero rating: " + dati.size());
			
			Set<Description> descriptionD = FeaturesSelection.subSuperClass(urlOwlFile, dati.getIndividuals());
			
			XMLConceptStream.scrivi(descriptionD, 2);
			
			break;
		case 3:
			break;
		case 4:
			break;
			default:
				break;
		}
		
		
		
		
		
		
		
	}

}
