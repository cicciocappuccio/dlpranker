package features;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;

import java.util.Collections;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class AllPrimitivesExists {

	Set<Description> risultato;
	String urlOwlFile;

	public AllPrimitivesExists() {

	}
	
	public Set<Description> allPrimitivesExists() {

		String file = "res/fragmentOntology10.owl";

		ExperimentDataset dati = new ExperimentRatingW(file);
		
		
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		AbstractConceptCache cache = null;
		
		Description start = Thing.instance;
		Psi2DownWrapper op4 = new Psi2DownWrapper(reasoner);
		
		try {
			reasoner.init();
			cache = new AsynchronousHibernateConceptCache(file);
			op4.init();
		} catch (ComponentInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<Description> risultato = Specialize.specialize(start, op4, 3, 0);
		
		Set<Individual> individui = dati.getIndividuals();
		
		for (Description i : risultato)
		{
			System.out.println(i);
			
			for (Individual y : individui)
			{
				if (!cache.contains(i, y)) {
					boolean entailed = reasoner.hasType(i, y);
					cache.addElement(i, y, entailed);
				}
			}
		}
		
		return risultato;
	}

	public AllPrimitivesExists(Set<Description> risultato, String urlOwlFile) {
		super();
		this.risultato = risultato;
		this.urlOwlFile = urlOwlFile;
	}

	public Set<Description> getRisultato() {
		return risultato;
	}

	public void setRisultato(Set<Description> risultato) {
		this.risultato = risultato;
	}

	public String getUrlOwlFile() {
		return urlOwlFile;
	}

	public void setUrlOwlFile(String urlOwlFile) {
		this.urlOwlFile = urlOwlFile;
	}
}
