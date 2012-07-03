package scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dllearner.core.owl.Individual;

import perceptron.ObjectRank;
import perceptron.OnLineKernelPerceptronRanker;
import test.KFolder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class GaussianKernel<T> {

	private static final int NFOLDS = 50;
	
	private Set<T> instances;
	private Table<T, T, Double> euclideans;
	
	public GaussianKernel(Set<T> instances, Table<T, T, Double> euclideans) {
		this.instances = instances;
		this.euclideans = euclideans;
	}
	
	public Table<T, T, Double> getKernel(List<ObjectRank<T>> training) {
		
		Map<Double, Double> accuracySigma = Maps.newTreeMap();
		
		for (double sigma = 1e-4; sigma <= 1e4; sigma *= 5.0) {

			System.out.println("Sigma: " + sigma);
			
			Table<T, T, Double> K = HashBasedTable.create();
			for (T xi : euclideans.rowKeySet()) {
				for (T xj : euclideans.columnKeySet()) {
					double sqdist = Math.pow(euclideans.get(xi, xj), 2.0);
					double val = Math.exp(-sqdist / (2.0 * Math.pow(sigma, 2.0)));
					K.put(xi, xj, val);
				}
			}

			KFolder<ObjectRank<T>> folder = new KFolder<ObjectRank<T>>(training, NFOLDS);
			
			
			
			double right = 0.0;
			double total = 0.0;
			
			for (int j = 0; j < NFOLDS; j++) {
				OnLineKernelPerceptronRanker<T> mo = new OnLineKernelPerceptronRanker<T>(films, K, 5);

				List<ObjectRank<Individual>> tr = new ArrayList<ObjectRank<Individual>>();
				for (Individual i : (List<Individual>) folder.getOtherFolds(j)) {
					ObjectRank<Individual> ii = new ObjectRank<Individual>(i, dati.getRatingMode(i));
					tr.add(ii);
				}

				//System.out.println("Training the perceptron (sigma = " + sigma + ")..");
				for (ObjectRank<Individual> i : training) {
					//System.out.println("Feeding " + i + " to the online algorithm..");
					mo.feed(i);
				}

				for (Individual i : (List<Individual>) folder.getFold(j)) {
					System.out.println("\t(sigma = " + sigma + ") Real: " + dati.getRatingMode(i) + ", Online predicted: " + mo.rank(i));
					if (dati.getRatingMode(i) == mo.rank(i)) {
						right += 1.0;
					}
					total += 1.0;
				}

			}

			
			accuracySigma.put(sigma, right / total);
		}
		
		for (Entry<Double, Double> punteggio : accuracySigma.entrySet()) {
			System.out.println("Sigma: " + punteggio.getKey() + ", Accuracy: " + punteggio.getValue());
		}
	}
	}
	
}
