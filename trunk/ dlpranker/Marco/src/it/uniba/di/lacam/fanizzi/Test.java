package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRating;
import it.uniba.di.lacam.fanizzi.experiment.type.BootstrapExperiment;
import it.uniba.di.lacam.fanizzi.experiment.type.KFoldsCrossValitationExperiment;
import it.uniba.di.lacam.fanizzi.utils.LogUtils;
import it.uniba.di.lacam.fanizzi.utils.SerializeUtils;
import it.uniba.di.lacam.fanizzi.utils.StatUtils;
import it.uniba.di.lacam.fanizzi.features.*;

import it.uniba.di.lacam.fanizzi.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

import test.ReasonerTest;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

public class Test {
	
	private static final String KERNELMATRIX_FILE = "res/KernelMatrix.xml";
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("\nDL-KRatings");		
		
		//String urlOwlFile = "res/luf_statements_20110727.owl";
		//String urlOwlFile = "res/dataset.owl";
		//String urlOwlFile = "res/linkedmdb-latest-minidump.rdf";
		//String urlOwlFile = "res/fragmentOntology3.owl";
		//String urlOwlFile = "res/mio2.rdf";
		//String urlOwlFile = "res/mio3.rdf";
		//String urlOwlFile = "res/mio4.rdf";
		String urlOwlFile = "res/dataset2.rdf";
		//String urlOwlFile = "res/mio2Copia.rdf";
		
		
//		LogUtils mioLog = new LogUtils("Log");
		
		Locale.setDefault(Locale.US);
		OntologyModel ontologyModel = new OntologyModel(urlOwlFile);
		
	
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		ExperimentDataset dati = new ExperimentRating(ontologyModel);
		System.out.println("Numero rating: " + dati.size());

		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("S E L E Z I O N E    F E A T U R E S");

//		OWLClassExpression[] featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
//		System.out.println(featuresD.length);
		Set<OWLClassExpression> featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
		
		String[] saveYou = new String[featuresD.size()];
		int ai = 0;
		for(OWLClassExpression a : featuresD)
			saveYou[ai++] = a.toString();
		
	    ObjectOutputStream oos;
	    try {
	      oos = new ObjectOutputStream(new FileOutputStream("res/ArrayFeatures.dat"));
//	      oos.writeObject(features);
	      oos.writeObject(saveYou);
	      oos.close();
	    } catch (FileNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }

//		Set<OWLClassExpression> featuresDS = ReasonerTest.rhoDRDownTest(urlOwlFile, featuresD);
		//Set<OWLClassExpression> featuresD = null;
		
		FeaturesDrivenDistance featuresDD = new FeaturesDrivenDistance();
		featuresDD.preLoadPi(IRI.create(new URI(urlOwlFile)), FeaturesDrivenDistance2.ALL, ontologyModel.getReasoner(), ontologyModel.getDataFactory(), featuresD, dati.getIndividuals());	
		System.out.println("Press <Enter> to continue =)))");
		System.in.read();
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		// creazione matrice kernel

		KernelMatrix kernel = new KernelMatrix();;
		kernel.createKernelMatrix(featuresDD);


		//
		kernel.CSVPrint();
		
		
		BootstrapExperiment exp = new BootstrapExperiment();
		exp.bootstrapExperiment(kernel, dati, 1000);
/*		
		KFoldsCrossValitationExperiment exp2 = new KFoldsCrossValitationExperiment(ontologyModel);
		exp2.kfxvExperiment(kernel, 10 , esempi, valoreRating, nRatings);
 */
	}
}
