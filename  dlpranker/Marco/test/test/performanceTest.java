package test;

import java.io.File;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class performanceTest {

	public static void mainP(String[] args) throws OWLOntologyCreationException {

		{
			PelletReasoner reasoner;
			OWLOntologyManager manager;
			OWLDataFactory dataFactory;
			OWLOntology ontology;
			String urlOwlFile = "res/fragmentOntology10.owl";

			// System.out.println(urlOwlFile);
			manager = OWLManager.createOWLOntologyManager();

			ontology = manager.loadOntologyFromOntologyDocument(new File(
						urlOwlFile));

			dataFactory = manager.getOWLDataFactory();
			System.out.println("ONTOLOGY: " + ontology);
			reasoner = PelletReasonerFactory.getInstance().createReasoner(
					ontology);
		
			Set<OWLNamedIndividual> a = ontology.getIndividualsInSignature();
		
			for(OWLNamedIndividual b : a) {
				
			}
				
		
		
		
		}
		
		
		
		
		
	}

}
