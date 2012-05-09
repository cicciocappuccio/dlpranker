package it.uniba.di.lacam.fanizzi.features;

//conforming to new OWLAPI

//import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


/**
* 
* @author Nicola Fanizzi
* 
* Marco ha trasformato i metodi static, ha aggiunto il costruttore e getFeatures() ed ha aggiunto individuals fra i membri
* 
*/
public class FeaturesDrivenDistance {
	
	public static final short ALL = 0; 									// when all concepts used as features
	public static final short PRIM = 1; 								// only primitives
	public static final short GEN = 2; 									// generated by feature construction
	public static final String[] EXT = {".all", ".prim", ".gen"};
	
	protected short[][] pi; 											// # features x # individuals				why protected??
	protected OWLClassExpression[] features;							//											why protected??
	private OWLNamedIndividual[] individuals;
	private double[] w; 												// weights for the distance
	
	private Set<OWLClassExpression> featuresSet;
	private Set<OWLNamedIndividual> individualsSet;
	private Table<OWLClassExpression, OWLNamedIndividual, Short> piTable;
	
	private Map<OWLClassExpression, Double> featuresWeight;
	
	public FeaturesDrivenDistance (OWLClassExpression[] features, Set<OWLClassExpression> featuresSet, OWLNamedIndividual[] individuals, Set<OWLNamedIndividual> individualsSet)
	{
		this.features = features;
		this.individuals = individuals;
		
		this.featuresSet = featuresSet;
		this.individualsSet = individualsSet;
	}
	
	public OWLClassExpression[] getFeatures()
	{
		return features;
	}

/*
 *  DA ELIMINARE!
 *  Per ottenere l'array degl'individuals con lo stesso ordine rivolgersi ad ExperimentDataset
 * 
 */	
	public OWLNamedIndividual[] getIndividuals()
 	{
		return individuals;
	}

//  ********   fine da eliminare    ********
	
	
	public int getIndividualsLength()
	{
		return individuals.length;
	}

	public void computeFeatureEntropies(
			PelletReasoner reasoner, 
			OWLDataFactory df, 
			OWLNamedIndividual[] individuals) {
		
		int numIndA = individuals.length;
		double sum = 0;
		
		for (int f=0; f<features.length; f++) {
			
			OWLClassExpression complFeature = df.getOWLObjectComplementOf(features[f]);
			        	
			int numPos = reasoner.getInstances(features[f],false).getFlattened().size();
	     	int numNeg = reasoner.getInstances(complFeature,false).getFlattened().size();
	     	int numBoh = numIndA - numPos - numNeg;
	
	     	double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
	     	double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
	     	double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	
	     	
	     	w[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
	     	sum += w[f];
     	
		}		
		
		for (int f=0; f<features.length; f++) 
			w[f] = w[f]/sum;
		
	}
	
	
	
	public void computeFeatureVariance(
			PelletReasoner reasoner, 
			OWLDataFactory df,  
			OWLNamedIndividual[] individuals) {
		
		int numIndA = individuals.length;
		
		double total = 0.0;
		
		for (int f=0; f<features.length; f++) {
			
			double fsum = 0.0;
			
			for(int i=0; i<numIndA; i++)
				for(int j=0; j<numIndA; j++)
					fsum += Math.pow(pi[f][i]-pi[f][j], 2);
     	w[f] = fsum/(numIndA*numIndA);
     	total += w[f]; 
		}		
		
		for (int f=0; f<features.length; f++)
			w[f] /= total;
	}

	

/**
* 
* @param ind1 first individual index
* @param ind2 second individal index
* @return the (semi-)distance measure between the individuals
*/	
	public double sqrDistance(OWLNamedIndividual ind1, OWLNamedIndividual ind2) {
		double acc = 0;
//		System.out.println("pi.length: " + pi.length);
		for (OWLClassExpression feature : featuresSet)
		{
			acc += Math.pow(featuresWeight.get(feature) * (piTable.get(feature, ind1) - piTable.get(feature, ind2)), 2);
		}
		return (double)Math.sqrt(acc)/(2*featuresWeight.size());
	} // distance
	
/**
* 
* @param ind1 index of the 1st individual
* @param ind2 index of the 2nd individual
* @return the distance value
*/	
	public double simpleDistance(int ind1, int ind2) {
		double acc = 0;

		for (OWLClassExpression feature : featuresSet)
		{
			acc += featuresWeight.get(feature) * Math.abs(piTable.get(feature, ind1) - piTable.get(feature, ind2));
			
		}
		
		return acc/(2*featuresWeight.size()); // divisione per 2 perche' doppi in pi 
	} // distance
	

	
	private void saveProjections(File oFile) {
		
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(oFile));

			oos.writeObject(piTable);
			oos.writeObject(featuresWeight);
			
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void readProjections(File iFile) throws IOException {
		
		ObjectInputStream ois;			
		
		try {
			ois = new ObjectInputStream(new FileInputStream(iFile));

			piTable = (Table<OWLClassExpression, OWLNamedIndividual, Short>) ois.readObject();
			featuresWeight = (Map<OWLClassExpression, Double>) ois.readObject();
			
			ois.close();
		} catch (FileNotFoundException e) {
			System.err.println("to be loaded...");
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	public void computeProjections(													// why public???
			PelletReasoner reasoner,
			OWLDataFactory factory) {

		piTable = HashBasedTable.create();
		
		featuresWeight = new HashMap<OWLClassExpression, Double>();
		
		for (OWLClassExpression feature : featuresSet)
			featuresWeight.put(feature, (double)(1/featuresSet.size()));
		
		int fSet= 0;
		for (OWLClassExpression feature : featuresSet)
		{
			System.out.printf("%4d. %50s", fSet, feature);
			
			OWLClassExpression negfeature = feature.getComplementNNF();
			
			for (OWLNamedIndividual individual : individualsSet)
			{
				OWLClassAssertionAxiom o1 = factory.getOWLClassAssertionAxiom(feature, individual);
				OWLClassAssertionAxiom o2 = factory.getOWLClassAssertionAxiom(negfeature, individual);
				
				if (reasoner.isEntailed(o1)) {
					piTable.put(feature, individual, (short) 0);
//					System.out.print(pi[f][i]);
				} else if (reasoner.isEntailed(o2)) {
					piTable.put(feature, individual, (short) 2);
//					System.out.print(pi[f][i]);
				} else {
					piTable.put(feature, individual, (short) 1);
//					System.out.print(pi[f][i]);
				}
			}
			System.out.printf(" | completed. %5.1f%% \n", 100.0*(fSet++ +1)*individualsSet.size() / (featuresSet.size()*individualsSet.size())); 

		}
		System.out.println("-----------------------------------------------------------------------------------------------------------");			
	
	}

	
	
	public void preLoadPi(IRI iri, short mode,
			PelletReasoner reasoner,
			OWLDataFactory factory) {
		
		File datafileName = new File(iri.toURI().getPath()+EXT[mode]);
		System.out.printf("Pre-computing %d x %d pi elements \n", features.length, individuals.length);
		
		try { readProjections(datafileName);	
		}  
		catch (FileNotFoundException e) {
			computeProjections(reasoner, factory);
			saveProjections(datafileName);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}	// class