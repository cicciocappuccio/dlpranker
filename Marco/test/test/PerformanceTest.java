package test;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class PerformanceTest {

	public static void main(String[] args) throws Exception {
		long t0 = System.currentTimeMillis();
		mainD(args);
		long t1 = System.currentTimeMillis();
		System.out.println("DLL - Time required: " + (t1 - t0) + " ms");
		mainP(args);
		long t2 = System.currentTimeMillis();
		System.out.println("Pellet - Time required: " + (t2 - t1) + " ms");
	}
	
	public static void mainD(String[] args) throws ComponentInitException {
		String file = "res/fragmentOntology10.owl";
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
		SortedSet<Individual> films = reasoner.getIndividuals(Film);
		System.out.println(films.size());
		
		Description refinement = new NamedClass("http://dbpedia.org/class/yago/ActionHorrorFilms");
		
		for (Individual film : films) {
			System.out.println(film + " -> " + reasoner.hasType(refinement, film));
		}
		
	}
	
	public static void mainP(String[] args) throws OWLOntologyCreationException {
		String urlOwlFile = "res/fragmentOntology10.owl";

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(urlOwlFile));

		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);

		OWLClass film = new OWLClassImpl(dataFactory, IRI.create("http://dbpedia.org/ontology/Film"));

		Set<OWLNamedIndividual> films = reasoner.getInstances(film, true).getFlattened();
		
		System.out.println(films.size());
		
		/*
		Set<OWLClass> classList = ontology.getClassesInSignature();

		OWLClassExpression[] classi = new OWLClassExpression[50];
		for (OWLClass b : classList) {
			for (OWLNamedIndividual a : films) {
				OWLClassAssertionAxiom o2 = dataFactory.getOWLClassAssertionAxiom(b, a);
				reasoner.isEntailed(o2);
			}
		}
		*/
	}

}
