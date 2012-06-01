package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRating;
import it.uniba.di.lacam.fanizzi.experiment.type.KFoldsCrossValitationExperiment;
import it.uniba.di.lacam.fanizzi.features.FeaturesDrivenDistance;
import it.uniba.di.lacam.fanizzi.features.FeaturesSelection;
import it.uniba.di.lacam.fanizzi.utils.SerializeUtils;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.thoughtworks.xstream.XStream;

import test.ReasonerTest;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("\nDL-KRatings");		
		
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
		
//		OWLClass film = new OWLClassImpl(ontologyModel.getDataFactory(), IRI.create("http://dbpedia.org/ontology/Film"));
//		Set<OWLNamedIndividual> filmSubClass = new HashSet<OWLNamedIndividual>();
		
		
		featuresD = FeaturesSelection.subClass(ontologyModel.getReasoner(), dati.getIndividuals());
		featuresD.addAll(FeaturesSelection.superClass(ontologyModel.getReasoner(), dati.getIndividuals()));
		//featuresD = FeaturesSelection.superClass(ontologyModel.getReasoner(), dati.getIndividuals());

		
/*		
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
*/		
		
		/*		
		System.out.println(ontologyModel.getOntology().getClassesInSignature().size());
		Set<OWLClass> mia = ontologyModel.getOntology().getClassesInSignature();
		
		Set<OWLClassExpression> mio = new HashSet<OWLClassExpression>();
		for(OWLClass c : mia)
			mio.add(c);
	*/	
	
		//Set<OWLClassExpression> featuresDS = ReasonerTest.rhoDRDownTest(urlOwlFile, featuresD);
		//Set<OWLClassExpression> featuresD = null;
		
		FeaturesDrivenDistance featuresDD = new FeaturesDrivenDistance();
		
		
		//AbstractConceptCache cache = new HibernateConceptCache(urlOwlFile);
		featuresDD.preLoadPi(ontologyModel.getReasoner(), ontologyModel.getDataFactory(), featuresD, dati.getIndividuals());
		
		//featuresDD.printAVGandVariance();
		
/*		ontologyModel.killReasoner();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream("res/piTable.xml")));
		encoder.writeObject(featuresDD);
		encoder.close();
		encoder = null;
		System.gc();
		ontologyModel.reasonerRise();
*/		
		
		
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
/*/* */
	}
}
