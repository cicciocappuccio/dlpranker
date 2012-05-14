package test;

import it.uniba.di.lacam.fanizzi.features.Specialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashSet;
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

import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;



public class ReasonerTest {
	
	public static void main(String[] args) throws ComponentInitException 
	{
		
		String[] array = null;
	    ObjectInputStream ois;      
	    
	    try {
	      ois = new ObjectInputStream(new FileInputStream("res/Array.dat"));
//	      features = (OWLClassExpression[]) ois.readObject(); 
	      array = (String[]) ois.readObject();
	      ois.close();
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();      
	    } catch (ClassNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }   
	    
	    for (String a : array)
	    	System.out.println(a);
		
		System.out.println("Press <Enter> to continue =)))");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		rhoDRDownTest("res/fragmentOntology10.owl", array);
	}
	
	
	
	@Test
	public static Set<OWLClassExpression> rhoDRDownTest(String file, String[] array) throws ComponentInitException {
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		
		
		System.out.println(file);

	    OWLOntologyManager manager;
		OWLDataFactory dataFactory;
		
		manager = OWLManager.createOWLOntologyManager();

		System.out.println(file);


        dataFactory = manager.getOWLDataFactory();
		
		
		OWLClass film = new OWLClassImpl(dataFactory, IRI.create("http://dbpedia.org/ontology/Film"));
		
		Set<OWLClassExpression> classi = new HashSet<OWLClassExpression>();
		for (String a : array) {
			if (!"<owl:Nothing>".equals(a)) {
				String tmp = (a.charAt(0) == '<' ? a.substring(1, a.length() - 1) : a);
				IRI c = IRI.create(tmp);
				OWLClassExpression b = new OWLClassImpl(dataFactory, c);
				classi.add(b);
			}
		}
		
		Set<OWLClassExpression> mio = Specialize.specialize (reasoner, classi);
		
		
		
		System.out.println(mio);
		System.out.println(mio.size());
		
		
		
		return mio;
		
		
	}

}