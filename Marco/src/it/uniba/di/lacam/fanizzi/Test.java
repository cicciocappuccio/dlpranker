package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRating;
import it.uniba.di.lacam.fanizzi.experiment.type.KFoldsCrossValitationExperiment;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance;
import it.uniba.di.lacam.fanizzi.features.FeaturesSelection;
import it.uniba.di.lacam.fanizzi.features.selection.Wrapper;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("\nDL-KRatings");		
		
		String urlOwlFile = "res/dataset2.rdf";

		Set<OWLClassExpression> featuresD = new HashSet<OWLClassExpression>();
		Wrapper mio = new Wrapper("res/fragmentOntology10.owl");
		
		featuresD = mio.trasformer(mio.cerca());
		
		
		mio = null;
		System.gc();
		
		Locale.setDefault(Locale.US);
		OntologyModel ontologyModel = new OntologyModel(urlOwlFile);
		
	
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		ExperimentDataset dati = new ExperimentRating(ontologyModel);
		System.out.println("Numero rating: " + dati.size());

		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("S E L E Z I O N E    F E A T U R E S");

		
//		OWLClass film = new OWLClassImpl(ontologyModel.getDataFactory(), IRI.create("http://dbpedia.org/ontology/Film"));
		
//		featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
//		featuresD.addAll(FeaturesSelection.superClass(ontologyModel.getReasoner(), dati.getIndividuals()));

		//Set<OWLClassExpression> featuresDS = ReasonerTest.rhoDRDownTest(urlOwlFile, featuresD);
		

		
		FeaturesDrivenDistance featuresDD = new FeaturesDrivenDistance();
		
		featuresDD.preLoadPi(ontologyModel.getReasoner(), ontologyModel.getDataFactory(), featuresD, dati.getIndividuals());
		
		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		// creazione matrice kernel

/*		KernelMatrix kernel = new KernelMatrix();;
		kernel.createKernelMatrix(featuresDD);

		kernel.CSVPrint();
		
		System.out.println("Press <Enter> to continue =)))");
		System.in.read();
		
		//BootstrapExperiment exp = new BootstrapExperiment();
		//exp.bootstrapExperiment(kernel, dati, 1000);
		
		KFoldsCrossValitationExperiment exp2 = new KFoldsCrossValitationExperiment();
		exp2.kfxvExperiment(kernel, dati, 10);

		*/
	}
}
