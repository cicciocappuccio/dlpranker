package test;

import it.uniba.di.lacam.fanizzi.features.Specialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractKnowledgeSource;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.kb.OWLFile;
import org.dllearner.parser.KBParser;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.reasoning.PelletReasoner;
import org.dllearner.refinementoperators.RhoDown;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import static org.junit.Assert.assertTrue;


import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dllearner.algorithms.ocel.OCEL;
import org.dllearner.core.AbstractLearningProblem;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.owl.ClassHierarchy;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.kb.OWLFile;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.OperatorInverter;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.test.junit.TestOntologies.TestOntology;
import org.dllearner.utilities.Helper;
import org.junit.Test;




public class ReasonerTest {

	public static void main(String[] args) throws ComponentInitException {

		String[] array = null;
		ObjectInputStream ois;

		try {
			ois = new ObjectInputStream(new FileInputStream("res/Array.dat"));
			// features = (OWLClassExpression[]) ois.readObject();
			array = (String[]) ois.readObject();
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * for (String a : array) System.out.println(a);
		 * 
		 * System.out.println("Press <Enter> to continue =)))"); try {
		 * System.in.read(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		Set<OWLClassExpression> mio= rhoDRDownTest(
				"res/fragmentOntology10.owl");
		String[] mioArray = new String[mio.size()];
		int i = 0;
		for (OWLClassExpression m : mio) {
			mioArray[i++] = m.toString();
		}

		ObjectOutputStream oos;

		try {
			oos = new ObjectOutputStream(new FileOutputStream(
					"res/Specializy.dat"));
			oos.writeObject(mioArray);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void rhoDRDownTest(String file) {
		try {
			KnowledgeSource ks = new OWLFile(file);
			AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
			reasoner.init();
			String baseURI = reasoner.getBaseURI();
//			ReasonerComponent rs = cm.reasoningService(rc);
			
			// TODO the following two lines should not be necessary
//			rs.prepareSubsumptionHierarchy();
//			rs.prepareRoleHierarchy();
			
			RhoDown op = new RhoDown(reasoner, false, false, true, true, true, true);
/*			op.setReasoner(reasoner);
			op.setSubHierarchy(reasoner.getClassHierarchy());
			op.setObjectPropertyHierarchy(reasoner.getObjectPropertyHierarchy());
			op.setDataPropertyHierarchy(reasoner.getDatatypePropertyHierarchy());
			
*/
			op.init();
			Description concept = KBParser.parseConcept(uri("Compound"));
			Set<Description> results = op.refine(concept, 4, null);

			for(Description result : results) {
				System.out.println(result);
			}
			
			int desiredResultSize = 141;
			if(results.size() != desiredResultSize) {
				System.out.println(results.size() + " results found, but should be " + desiredResultSize + ".");
			}
			assertTrue(results.size()==desiredResultSize);
		} catch(ComponentInitException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

/*	
	@Test
	public static Set<OWLClassExpression> rhoDRDownTest(String file, String[] array)
	{
		String baseURI;
		try {
			ComponentManager cm = ComponentManager.getInstance();
			KnowledgeSource ks = cm.knowledgeSource(OWLFile.class);
			//KnowledgeSource ks = new OWLFile();
			try {
				//cm.applyConfigEntry(new OWLFile(), "url", );
				//cm.a
				cm.applyConfigEntry(ks, "url", new File(file).toURI().toURL());
			} catch (MalformedURLException e) {
				// should never happen
				e.printStackTrace();
			}
			ks.init();
			ReasonerComponent rc = cm.reasoner(OWLAPIReasoner.class, ks);
			rc.init();
			baseURI = rc.getBaseURI();
			// ReasonerComponent rs = cm.reasoningService(rc);

			// TODO the following two lines should not be necessary
			// rs.prepareSubsumptionHierarchy();
			// rs.prepareRoleHierarchy();

			RhoDown op = new RhoDown(rc);
			Description concept = KBParser.parseConcept(uri("Compound"));
			Set<Description> results = op.refine(concept, 4, null);

			for (Description result : results) {
				System.out.println(result);
			}

			int desiredResultSize = 141;
			if (results.size() != desiredResultSize) {
				System.out.println(results.size()
						+ " results found, but should be " + desiredResultSize
						+ ".");
			}
			assertTrue(results.size() == desiredResultSize);
		} catch (ComponentInitException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// ###########################################################################################################

		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner();
		reasoner.setSources(Collections.singleton(ks));
		try {
			reasoner.init();
		} catch (ComponentInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(file);

		OWLOntologyManager manager;
		OWLDataFactory dataFactory;

		manager = OWLManager.createOWLOntologyManager();

		// System.out.println(file);

		dataFactory = manager.getOWLDataFactory();

		RhoDown r = new RhoDown(reasoner, false, false, true, true, true, true);

		Set<OWLClassExpression> classi = new HashSet<OWLClassExpression>();

		OWLClass film = new OWLClassImpl(dataFactory,
				IRI.create("http://dbpedia.org/ontology/Film"));
		classi.add(film);
		Set<OWLClassExpression> mio = Specialize
				.specialize(reasoner, classi, r);

		System.out.println(mio);
		System.out.println(mio.size());

		return mio;

	}
*/
}