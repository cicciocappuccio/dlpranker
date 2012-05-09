package test;

import it.uniba.di.lacam.fanizzi.OntologyModel;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRating;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance2;
import it.uniba.di.lacam.fanizzi.features.FeaturesSelection;
import it.uniba.di.lacam.fanizzi.features.Specialize;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;



public class ReasonerTest {
	
	public static void main(String[] args) throws ComponentInitException 
	{

		rhoDRDownTest();
	}
	
	
	
	
	
	
	@Test
	public  static void rhoDRDownTest() throws ComponentInitException {
		String file = "res/dataset2.rdf";
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		reasoner.init();
		
		
		System.out.println(file);

	    OWLOntologyManager manager;
		OWLDataFactory dataFactory;
		OWLOntology ontology;
		
		manager = OWLManager.createOWLOntologyManager();


       
        try {
        	ontology = manager.loadOntologyFromOntologyDocument(new File(file));
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
		System.out.println(file);
        
        dataFactory = manager.getOWLDataFactory();
		
		
		OWLClass film = new OWLClassImpl(dataFactory, IRI.create("http://dbpedia.org/ontology/Film"));
		
		Set<OWLClassExpression> mio = Specialize.specialize (reasoner, film);
		
		System.out.println(mio);
		//
	}

}
