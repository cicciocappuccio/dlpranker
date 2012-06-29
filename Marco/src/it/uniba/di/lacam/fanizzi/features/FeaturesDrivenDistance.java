package it.uniba.di.lacam.fanizzi.features;

//conforming to new OWLAPI


import it.uniba.di.lacam.fanizzi.utils.SerializeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
 *         Marco ha trasformato i metodi static, ha aggiunto il costruttore e
 *         getFeatures() ed ha aggiunto individuals fra i membri
 * 
 */
public class FeaturesDrivenDistance {

	public static final short ALL = 0; // when all concepts used as features
	public static final short PRIM = 1; // only primitives
	public static final short GEN = 2; // generated by feature construction
	public static final String[] EXT = { ".all", ".prim", ".gen" };

	private Table<OWLClassExpression, OWLNamedIndividual, Short> pi;

	private Map<OWLClassExpression, Double> featuresWeight;

	public FeaturesDrivenDistance() {

	}

	public Set<OWLClassExpression> getFeatures() {
		return pi.rowKeySet();
	}

	/*
	 * DA ELIMINARE! Per ottenere l'array degl'individuals con lo stesso ordine
	 * rivolgersi ad ExperimentDataset
	 */
	public Set<OWLNamedIndividual> getIndividuals() {
		return pi.columnKeySet();
	}

	public void setPi(Table<OWLClassExpression, OWLNamedIndividual, Short> pi) {
		this.pi = pi;
	}

	public void computeFeatureEntropies(PelletReasoner reasoner,
			OWLDataFactory df) {

		int numIndA = pi.columnKeySet().size();
		double sum = 0;

		for (OWLClassExpression feature : pi.rowKeySet()) {
			OWLClassExpression complFeature = df
					.getOWLObjectComplementOf(feature);

			int numPos = reasoner.getInstances(feature, false).getFlattened()
					.size();
			int numNeg = reasoner.getInstances(complFeature, false)
					.getFlattened().size();
			int numBoh = numIndA - numPos - numNeg;

			double prPos = (numPos > 0 ? (double) numPos / numIndA
					: Double.MIN_VALUE);
			double prNeg = (numNeg > 0 ? (double) numNeg / numIndA
					: Double.MIN_VALUE);
			double prBoh = (numBoh > 0 ? (double) numBoh / numIndA
					: Double.MIN_VALUE);

			featuresWeight.put(feature, -(prPos * Math.log(prPos) + prNeg
					* Math.log(prNeg) + prBoh * Math.log(prBoh)));
			sum += featuresWeight.get(feature);
		}

		for (OWLClassExpression feature : pi.rowKeySet())
			featuresWeight.put(feature, featuresWeight.get(feature) / sum);
	}

	public void computeFeatureVariance(PelletReasoner reasoner,
			OWLDataFactory df) {

		int numIndA = pi.columnKeySet().size();

		double total = 0.0;

		for (OWLClassExpression feature : pi.rowKeySet()) {
			double fsum = 0.0;

			for (OWLNamedIndividual individualI : pi.columnKeySet()) {
				for (OWLNamedIndividual individualJ : pi.columnKeySet()) {
					fsum += Math.pow(
							pi.get(feature, individualI)
									- pi.get(feature, individualJ), 2);
				}
			}
			featuresWeight.put(feature, fsum / (numIndA * numIndA));
			total += featuresWeight.get(feature);
		}

		for (OWLClassExpression feature : pi.rowKeySet())
			featuresWeight.put(feature, featuresWeight.get(feature) / total);
	}

	/**
	 * 
	 * @param ind1
	 *            first individual index
	 * @param ind2
	 *            second individal index
	 * @return the (semi-)distance measure between the individuals
	 */
	public double sqrDistance(OWLNamedIndividual ind1, OWLNamedIndividual ind2) {
		double acc = 0;
		// System.out.println("pi.length: " + pi.length);

		for (OWLClassExpression feature : pi.rowKeySet()) {
			acc += Math.pow(
					featuresWeight.get(feature)
							* (pi.get(feature, ind1) - pi.get(feature, ind2)),
					2);
		}
		return (double) Math.sqrt(acc) / (2 * featuresWeight.size());
	} // distance

	/**
	 * 
	 * @param ind1
	 *            index of the 1st individual
	 * @param ind2
	 *            index of the 2nd individual
	 * @return the distance value
	 */
	public double simpleDistance(int ind1, int ind2) {
		double acc = 0;

		for (OWLClassExpression feature : pi.rowKeySet()) {
			acc += featuresWeight.get(feature)
					* Math.abs(pi.get(feature, ind1) - pi.get(feature, ind2));
		}

		return acc / (2 * featuresWeight.size()); // divisione per 2 perche'
													// doppi in pi
	} // distance

	private void saveProjections(File oFile) {

		Object[] a = new Object[2];

		a[0] = pi;
		a[1] = featuresWeight;

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(oFile));
			oos.writeObject(SerializeUtils.serialize(a));
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readProjections(File iFile) throws IOException {

		ObjectInputStream ois;

		try {
			Object[] a = new Object[2];

			ois = new ObjectInputStream(new FileInputStream(iFile));

			a = (Object[]) SerializeUtils
					.deserialize((String) ois.readObject());

			pi = (Table<OWLClassExpression, OWLNamedIndividual, Short>) a[0];
			featuresWeight = (Map<OWLClassExpression, Double>) a[1];

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
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void computeProjections(
			// why public???
			PelletReasoner reasoner,
			// AbstractConceptCache cache,
			OWLDataFactory factory, Set<OWLClassExpression> features,
			Set<OWLNamedIndividual> individuals) {

		pi = HashBasedTable.create();
		featuresWeight = new HashMap<OWLClassExpression, Double>();

		for (OWLClassExpression feature : features) {
			double cell = 1.0 / (double) features.size();
			featuresWeight.put(feature, cell);
		}
		
		
		int fSet = 0;
		for (OWLClassExpression feature : features) {
			System.out.printf("%4d. %120s", fSet, feature);
//			int featuresEntailed = 0;
			OWLClassExpression negfeature = feature.getComplementNNF();

			//System.out.println("Individuals: " + individuals.size());
			
			for (OWLNamedIndividual individual : individuals) {

					OWLClassAssertionAxiom o1 = factory
							.getOWLClassAssertionAxiom(feature, individual);
					OWLClassAssertionAxiom o2 = factory
							.getOWLClassAssertionAxiom(negfeature, individual);
					
					if (reasoner.isEntailed(o1)) {
						pi.put(feature, individual, (short) 0);
//						featuresEntailed++;
						// System.out.print(pi[f][i]);
					} else if (reasoner.isEntailed(o2)) {
						pi.put(feature, individual, (short) 2);
//						featuresEntailed++;
						// System.out.print(pi[f][i]);
					} else {
						pi.put(feature, individual, (short) 1);
						// System.out.print(pi[f][i]);
					}

			}
			//System.out.printf("%4d. %120s | completed. %5.1f%% \n", fSet,	feature, 100.0 * (fSet++ + 1) * individuals.size() / (features.size() * individuals.size()));

			System.out.printf(" | completed. %5.1f%%\n", 100.0 * (fSet++ + 1) * individuals.size() / (features.size() * individuals.size()));
		}
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
	}

	public void preLoadPi(PelletReasoner reasoner,
			// AbstractConceptCache cache,

			OWLDataFactory factory, Set<OWLClassExpression> features,
			Set<OWLNamedIndividual> individuals) {

		System.out.printf("Pre-computing %d x %d pi elements \n",
				features.size(), individuals.size());

		computeProjections(reasoner, factory, features, individuals);
		//CSVWriter.write2("res/piMatrix.txt", pi);

	}

	public Table<OWLClassExpression, OWLNamedIndividual, Short> getPi() {
		return pi;
	}
	

} // class