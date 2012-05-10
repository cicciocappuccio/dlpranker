package test;

import it.uniba.di.lacam.fanizzi.features.Specialize;

import java.util.Collections;
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
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;



public class ReasonerTest {
	
	public static void main(String[] args) throws ComponentInitException 
	{

//		rhoDRDownTest();
	}
	
	
	
	
	
	
	@Test
	public static Set<OWLClassExpression> rhoDRDownTest(String file, Set<OWLClassExpression> classi) throws ComponentInitException {
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		
		
		System.out.println(file);

	    OWLOntologyManager manager;
		OWLDataFactory dataFactory;
		
		manager = OWLManager.createOWLOntologyManager();

		System.out.println(file);


        dataFactory = manager.getOWLDataFactory();
		
		
		//OWLClass film = new OWLClassImpl(dataFactory, IRI.create("http://dbpedia.org/ontology/Film"));
		
		
		
		Set<OWLClassExpression> mio = Specialize.specialize (reasoner, classi);
		
		System.out.println(mio);
		System.out.println(mio.size());
		
		return mio;
	}

}
