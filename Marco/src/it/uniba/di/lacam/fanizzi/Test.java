package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingW;
import it.uniba.di.lacam.fanizzi.experiment.type.KFoldsCrossValitationExperiment;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistanceD;
import it.uniba.di.lacam.fanizzi.features.FeaturesSelection;
import it.uniba.di.lacam.fanizzi.utils.XMLConceptStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.utilities.owl.OWLAPIDescriptionConvertVisitor;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("\nDL-KRatings");		
		
//		String urlOwlFile = "res/dataset2.rdf";
		String urlOwlFile = "res/fragmentOntology10.owl";

		Locale.setDefault(Locale.US);
		

		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		ExperimentDataset dati = new ExperimentRatingW(urlOwlFile);
		System.out.println("Numero rating: " + dati.size());
/*	*/		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("S E L E Z I O N E    F E A T U R E S");

		
		//Set<Description> descriptionD = FeaturesSelection.subSuperClass(urlOwlFile, dati.getIndividuals());
		
		
//		featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
//		featuresD.addAll(FeaturesSelection.superClass(ontologyModel.getReasoner(), dati.getIndividuals()));

		//Set<OWLClassExpression> featuresDS = ReasonerTest.rhoDRDownTest(urlOwlFile, featuresD);

		KnowledgeSource ks = new OWLFile(urlOwlFile);
		System.out.println("kb creato");
//		AbstractReasonerComponent reasoner = null; //new OWLAPIReasoner(Collections.singleton(ks));
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		System.out.println("reasoner creato");
		
		reasoner.init();
		System.out.println("reasoner inizializzato");
		AbstractConceptCache cache = null; //new AsynchronousHibernateConceptCache(urlOwlFile);
		System.out.println("cache creata");
		
		
		Set<Description> descriptionD = XMLConceptStream.leggi(1);
	
		FeaturesDrivenDistanceD featuresDD = new FeaturesDrivenDistanceD();
		
		featuresDD.preLoadPi(reasoner, cache, descriptionD, dati.getIndividuals());
		
			
		//featuresDD.printW();
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		
		
		// creazione matrice kernel

		KernelMatrix kernel = new KernelMatrix();;

		
		
		
		
		
		kernel.createKernelMatrix(featuresDD);

		kernel.CSVPrint();
		
		System.out.println("Press <Enter> to continue =)))");
		System.in.read();
		
		//BootstrapExperiment exp = new BootstrapExperiment();
		//exp.bootstrapExperiment(kernel, dati, 1000);
		
		KFoldsCrossValitationExperiment exp2 = new KFoldsCrossValitationExperiment();
		exp2.kfxvExperiment(kernel, dati, 10);
/*
/*		*/
	}
}
