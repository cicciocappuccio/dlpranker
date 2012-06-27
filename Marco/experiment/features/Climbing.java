package features;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.GreedyForward;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;

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

public class Climbing {
	
	String file = "res/fragmentOntology10.owl";
	private Set<Description> risultato;
	
	

	public Climbing(String file, Set<Description> risultato) {
		super();
		this.file = file;
		this.risultato = risultato;
	}
	public Climbing() {
		super();

	}
	
	public Set<Description> climbing()
	{
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

		risultato = pioniere.estrazione(Thing.instance, films,
				tScore);

		for (Description j : risultato)
			System.out.println(j);
		
		return risultato;
	}


	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	public Set<Description> getRisultato() {
		return risultato;
	}


	public void setRisultato(Set<Description> risultato) {
		this.risultato = risultato;
	}


	
	
}
