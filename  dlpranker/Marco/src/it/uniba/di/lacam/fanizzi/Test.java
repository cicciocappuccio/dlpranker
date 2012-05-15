package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRating;
import it.uniba.di.lacam.fanizzi.experiment.type.BootstrapExperiment;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance2;
import it.uniba.di.lacam.fanizzi.features.FeaturesSelection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.HibernateConceptCache;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class Test {
	
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

		Set<OWLClassExpression> featuresD = new HashSet<OWLClassExpression>();
		featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
		
		
		String[] array = null;
		ObjectInputStream ois;

		try {
			ois = new ObjectInputStream(new FileInputStream("res/Specializy.dat"));
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
		
		for (String a : array)
		{
			System.out.println(a);
			OWLClass b = new OWLClassImpl(ontologyModel.getDataFactory(), IRI.create(a));
			featuresD.add(b);
		}


		//Set<OWLClassExpression> featuresDS = ReasonerTest.rhoDRDownTest(urlOwlFile, featuresD);
		//Set<OWLClassExpression> featuresD = null;
		
		FeaturesDrivenDistance featuresDD = new FeaturesDrivenDistance();
		
		
		AbstractConceptCache cache = new HibernateConceptCache(urlOwlFile);
		featuresDD.preLoadPi(ontologyModel.getReasoner(), cache, ontologyModel.getDataFactory(), featuresD, dati.getIndividuals());
		
		System.out.println("Press <Enter> to continue =)))");
		System.in.read();
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		// creazione matrice kernel

		KernelMatrix kernel = new KernelMatrix();;
		kernel.createKernelMatrix(featuresDD);

		kernel.CSVPrint();
		
		BootstrapExperiment exp = new BootstrapExperiment();
		exp.bootstrapExperiment(kernel, dati, 1000);
/*		
		KFoldsCrossValitationExperiment exp2 = new KFoldsCrossValitationExperiment(ontologyModel);
		exp2.kfxvExperiment(kernel, 10 , esempi, valoreRating, nRatings);
 */
	}
}
