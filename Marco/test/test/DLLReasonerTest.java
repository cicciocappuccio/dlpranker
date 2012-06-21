package test;

import java.io.File;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;

import com.neuralnoise.cache.ReasonerUtils;

public class DLLReasonerTest {

	public static void main(String[] args) throws Exception {
		String sowl = "res/dataset2.rdf";
		File fowl = new File(sowl);
		
		AbstractReasonerComponent reasoner = ReasonerUtils.getReasoner(fowl);
		
		System.out.println("Loaded ontology: " + fowl);
		
		Description film = new NamedClass("http://dbpedia.org/ontology/Film");
		
		Set<Individual> filmIndividuals = reasoner.getIndividuals(film);
		
		System.out.println(filmIndividuals);
		
		for (Individual filmIndividual : filmIndividuals) {
		
		}
	}
	
}
