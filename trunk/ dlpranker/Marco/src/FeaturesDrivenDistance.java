//package it.uniba.di.lacam.fanizzi;


//import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.Reasoner;


//import org.mindswap.pellet.jena.OWLReasoner;

//import com.hp.hpl.jena.ontology.OntClass;
//import com.hp.hpl.jena.ontology.OntModel;
//import com.hp.hpl.jena.rdf.model.Resource;




/**
 * 
 * @author Nicola Fanizzi
 * 
 */
public class FeaturesDrivenDistance {
	
	public static short[][] pi; // # features x # individuals
	private static double[] featureEntropy;
	private static double[] featureVar;
	
//	public static void computeFeatureEntropies(OWLReasoner reasoner, OntModel model, OntClass[] features) {
//		
//		int numIndA = reasoner.getIndividuals().size();
//		featureEntropy = new double[features.length];
//		double sum = 0;
//		
//		for (int f=0; f<features.length; f++) {
//			
//			OntClass complFeature = model.createComplementClass(null,features[f]);
//			reasoner.load(model);
//        	
//			int numPos = reasoner.getInstances(features[f]).size();
//        	int numNeg = reasoner.getInstances(complFeature).size();
//        	int numBoh = numIndA - numPos - numNeg;
//
//        	double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
//        	double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
//        	double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	
//        	
//        	featureEntropy[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
//        	sum += featureEntropy[f];
//        	
//		}		
//		
//		for (int f=0; f<features.length; f++) 
//			featureEntropy[f] = featureEntropy[f]/sum;
//		
//	}
	
	
	public static void computeFeatureEntropies(Reasoner reasoner, OWLDataFactory df, OWLClassExpression[] features) {
		
		int numIndA = reasoner.getIndividuals().size();
		featureEntropy = new double[features.length];
		double sum = 0;
		
		for (int f=0; f<features.length; f++) {
			
			OWLClassExpression complFeature = df.getOWLObjectComplementOf(features[f]);
			        	
			int numPos = reasoner.getIndividuals(features[f],false).size();
        	int numNeg = reasoner.getIndividuals(complFeature,false).size();
        	int numBoh = numIndA - numPos - numNeg;

        	double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
        	double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
        	double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	
        	
        	featureEntropy[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
        	sum += featureEntropy[f];
        	
		}		
		
		for (int f=0; f<features.length; f++) 
			featureEntropy[f] = featureEntropy[f]/sum;
		
	}
	
	
	
	public static void computeFeatureVariance(Reasoner reasoner, OWLDataFactory df, OWLClassExpression[] features) {
		
		int numIndA = reasoner.getIndividuals().size();
		featureVar = new double[features.length];
		
		double total = 0.0;
		
		for (int f=0; f<features.length; f++) {
			
			double fsum = 0.0;
			
			for(int i=0; i<numIndA; i++)
				for(int j=0; j<numIndA; j++)
					fsum += Math.pow(pi[f][i]-pi[f][j], 2);
        	featureVar[f] = fsum/(2*numIndA);
        	total += featureVar[f]; 
		}		
				
		for (int f=0; f<features.length; f++)
			featureVar[f] /= total;
	}

	
	
//	public static void preLoadPi(String url, OWLReasoner reasoner, OntModel model, OntClass[] features, Resource[] allExamples) {
//		
//		String path = "";
//		try {
//			URI upath = new URI(url);
//			path = upath.getPath();
//		} catch (URISyntaxException e) {
//			e.printStackTrace(); 
//		}
//		File projFile = new File(path+".dat");
//		if (projFile.exists()) {
//			System.out.printf("Reading pi elements from file: %s\n",projFile);
//			readProjections(projFile);
//		}
//		else {
//			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, allExamples.length);
//			pi = new short[features.length][allExamples.length];
//			for (int f=0; f < features.length; ++f) {
//				System.out.printf("%4d. %50s", f, features[f].getLocalName());
//				OntClass negfeature = model.createComplementClass(null, features[f]);
//				reasoner.load(model);
//				for (int i=0; i < allExamples.length; i++) {
//						// case: ind is not an instance of h
//						if 	(reasoner.isInstanceOf(allExamples[i], features[f])) 
//							pi[f][i] = 0;
//						else {
//							// case: ind is not an instance of h							
//							if (reasoner.isInstanceOf(allExamples[i],negfeature))	
//								pi[f][i] = 2;
//							else
//								// case unknown membership
//								pi[f][i] = 1;
//						}
//	//					System.out.print(".");
//				}
//				System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*allExamples.length / (features.length*allExamples.length)); 
//			}
//			System.out.println("-----------------------------------------------------------------------------------------------------------");
//			saveProjections(projFile);
//			System.out.printf("Saved pi elements to file: %s\n",projFile);
//		}
//	}

/**
 * 
 * @param ind1 first individual index
 * @param ind2 second individual index
 * @param dim dimension of the comparison

 * @return the (semi-)distance measure between the individuals
 */	
	public static double sqrDistance(int ind1, int ind2) {
		double acc = 0;
		for (int h=0; h<pi.length; h++) {	
			acc += Math.pow(pi[h][ind1] - pi[h][ind2], 2); 
		}
		return (double)Math.sqrt(acc)/(2*pi.length);
	} // distance
	
/**
 * 
 * @param ind1 index of the 1st individual
 * @param ind2 index of the 2nd individual
 * @param dim no dimensions 
 * @return
 */
	public static double simpleDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi
	} // distance
	
	
	public static double simpleEntropyDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += featureEntropy[f] * Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi 
	} // distance
	

	public static double simpleVarDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += featureVar[f] * Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi 
	} // distance

	
//	private static double pi(URI ind, Concept h, KB theKB) {
//		// case: ind is an instance of h
//		if 	(theKB.instanceOf(ind,h)) return 0;
//		
//		// case: ind is not an instance of h
//		Concept negH = NegatedConceptDescription.create(h);
//		if (theKB.instanceOf(ind,negH))	return 1;
//		
//		// case unknown membership
//		return (double)0.5;
//	}	// pi
	
	
	static void saveProjections(File oFile) {
		
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(oFile));
			oos.writeObject(pi);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	static void readProjections(File iFile) {
		
		ObjectInputStream ois;
		try {			
			ois = new ObjectInputStream(new FileInputStream(iFile));
			pi = (short[][]) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static void preLoadPi(String urlOwlFile,
			Reasoner reasoner, OWLOntologyManager manager,
			OWLClassExpression[] features, OWLNamedIndividual[] allExamples) {
		// TODO Auto-generated method stub
		
		String path = "";
		try {
			URI upath = new URI(urlOwlFile);
			path = upath.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace(); 
		}
		File projFile = new File(path+".dat");
		if (projFile.exists()) {
			System.out.printf("Reading pi elements from file: %s\n",projFile);
			readProjections(projFile);
		}
		else {
			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, allExamples.length);
			pi = new short[features.length][allExamples.length];
			for (int f=0; f < features.length; ++f) {
				System.out.printf("%4d. %50s\n", f,features[f] );
				
				OWLClassExpression negfeature = manager.getOWLDataFactory().getOWLObjectComplementOf(features[f]);

				for (int i=0; i < allExamples.length; i++) {
						// case: ind is not an instance of h
					System.out.printf("sdsdsdf\n");	
					try {
							if 	(reasoner.hasType(allExamples[i], features[f], false)) 
								pi[f][i] = 0;
							else {
								// case: ind is not an instance of h							
								if (reasoner.hasType(allExamples[i], negfeature, false))	
									pi[f][i] = 2;
								else
									// case unknown membership
									pi[f][i] = 1;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	//					System.out.print(".");
				}
				System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*allExamples.length / (features.length*allExamples.length)); 
			}
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			saveProjections(projFile);
			System.out.printf("Saved pi elements to file: %s\n",projFile);
		}
		
	}
	
	
	
	public static void preLoadPi(Reasoner reasoner, OWLOntologyManager manager,
			OWLClassExpression[] features, OWLNamedIndividual[] individuals) {

			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, individuals.length);
			pi = new short[features.length][individuals.length];
			for (int f=0; f < features.length; ++f) {
				System.out.printf("%4d. %50s", f, features[f]);
				
				OWLClassExpression negfeature = manager.getOWLDataFactory().getOWLObjectComplementOf(features[f]);

				for (int i=0; i < individuals.length; i++) {
						// case: ind is not an instance of h
						try {
							if 	(reasoner.hasType(individuals[i], features[f], false)) 
								pi[f][i] = 0;
							else {
								// case: ind is not an instance of h
								if (reasoner.hasType(individuals[i], negfeature, false))	
									pi[f][i] = 2;
								else
									// case unknown membership
									pi[f][i] = 1;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// System.out.print(".");
				}
				System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*individuals.length / (features.length*individuals.length)); 
			}
			System.out.println("-----------------------------------------------------------------------------------------------------------");			
	}
	
}	// class
	
	
