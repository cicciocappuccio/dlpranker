package it.uniba.di.lacam.fanizzi;

import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;


public class DLKRating {


	private static final double THRESHOLD = Math.pow(10, -8); //.1;

	
	public static void kernelPerceptronRank(ExperimentDataset dati, KernelMatrix kernel, List<OWLNamedIndividual> trainingExs, Map<OWLNamedIndividual, Double> alpha, double[] theta) {
		
//		System.out.println("kernelPerceptronRank(" + DebugUtils.toString(trainingExs) + ", " + DebugUtils.toString(ranks)
//				+ ", " + DebugUtils.toString(alpha) + ", " + DebugUtils.toString(theta) + ", " + nRatings + ")");
		
		double avgLoss = Double.POSITIVE_INFINITY, oldAvgLoss = Double.POSITIVE_INFINITY;
		
		theta[dati.maxRating()-1] = Double.MAX_VALUE;
		
		do {
			oldAvgLoss = avgLoss;
			avgLoss = 0.0;
			for (OWLNamedIndividual t : trainingExs)
			{
				OWLNamedIndividual e = dati.getIndividual(t);
				
				int yp = kernel.rank(e, alpha, theta, dati.maxRating());
				int yt = dati.getRatingValue(t);
				
//				System.out.println("yp: " + yp + ", yt: " + yt);
				
				if (yp != yt) {
					avgLoss += Math.abs(yt - yp);
					alpha.put(e, (alpha.get(e) + avgLoss));
//					alpha[e] += yt - yp;
//					alpha[t] += avgLoss;											cambio del indice di alpha per associare il peso al film
					for (int h = Math.min(yp, yt); h < Math.max(yp, yt) - 1; h++)
						theta[h] = (yp < yt ? theta[h] + 1 : theta[h] - 1);
				}
			}

			avgLoss /= trainingExs.size();
			System.out.println("avgLoss: " + avgLoss + "                                 " + (avgLoss > THRESHOLD) );
		//} while (avgLoss > THRESHOLD);
		} while ((oldAvgLoss - avgLoss) > THRESHOLD);
	}
		
}