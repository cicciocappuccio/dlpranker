package it.uniba.di.lacam.fanizzi.experiment.type;

import it.uniba.di.lacam.fanizzi.DLKRating;
import it.uniba.di.lacam.fanizzi.KernelMatrix;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.utils.StatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class KFoldsCrossValitationExperiment {

	public KFoldsCrossValitationExperiment() {
	}

	public void kfxvExperiment(KernelMatrix kernel, ExperimentDataset dati, int nFolds)
	{
		System.out.println("K-fold cross validation Experiment on ontology");
		System.out.println(nFolds + " fold");
		
		int nRatings = dati.maxRating();
		
		double foldCardinality = Math.round((((double) dati.getIndividuals().size()) / nFolds) - 0.5);
		int mia = (int) Math.round(foldCardinality);
		//System.out.println(foldCardinality);

		
		double[] foldLoss = new double[nFolds];

		for (int f = 0; f < nFolds; f++)
		{
/*			System.out.println("Press <Enter> to continue =)))");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
			double lossCount = 0.0;
			List<Individual> trainingExs = new ArrayList<Individual>();
			Set<Individual> testExs = new HashSet<Individual>();
			
			Map<Individual, Double> wMap = new HashMap<Individual, Double>();
			
			List<Individual> black = new ArrayList<Individual>(dati.getIndividuals());
			
			int i = 0;
			for (Individual mio : black)
			{
				if ((i >= f * foldCardinality) && (i < (f + 1) * foldCardinality))
				{
					testExs.add(mio);
				}
				else
				{
					trainingExs.addAll(dati.getRatings(mio));
					wMap.put(mio, 0.0);
				}
				i++;
			}
			
			
//			System.out.println("#################### starting wc empty test");
			//for (OWLNamedIndividual w : wMap.keySet())
			//	System.out.println(w);
//			System.out.println("#################### ending wc empty test");
			
			
			
			double[] theta = new double[nRatings];
/*			for (int t = 0; t < 5; t++)
				System.out.printf(t + "(" + theta[t] + ") - ");
*/
			System.out.println("------------------------------------------ Fold " + (f+1)  + "/" + nFolds + " ------------------------------------------\n" + "trainingExs: " + trainingExs.size() + " | wMap: " + wMap.size() + "\nTraining is starting...-----------------------------------------+");
			//System.out.println();
			
			DLKRating.kernelPerceptronRank(dati, kernel, trainingExs, wMap, theta);
/*						for (int t = 0; t < 5; t++)
				System.out.printf(t + "(" + theta[t] + ") - ");

*
*/
//			System.out.printf("\nmodel induced \n\n");

			System.out.println("                                               ...end of Training.");
			
			
/*			System.out.println("#################### starting wc empty test");
			for (OWLNamedIndividual w : wMap.keySet())
				System.out.println("wc(" + w + "):" + wMap.get(w));
			System.out.println("#################### ending wc empty test");
*/			
			
			System.out.println("Testing is starting... ---------------------------------------------------------------------------------------+---+---+");
			
			int y = 1;
			for (Individual te : testExs)
			{
				
				int inducedRank = kernel.rank2(te, wMap, theta, nRatings);
				System.out.printf( " %2d/%2d | %100s | %d | %d | %f |\n", y++, testExs.size(), te, (inducedRank + 1), dati.getRatingMode(te), dati.getAVGRating(te));
				lossCount += Math.abs(inducedRank - dati.getRatingMode(te)) / (double) nRatings;
			}
			
			System.out.println("-------+------------------------------------------------------------------------------------------------------+---+---+");
			System.out.printf("\n%10s %10s\n", "Q.#", "Loss");
			foldLoss[f] = lossCount / (double) foldCardinality;
			System.out.printf(" %10f\n", foldLoss[f]);
			
			//System.out.println(wMap);
			
//			for (int t = 0; t < 5; t++)
//				System.out.printf(t + "(" + theta[t] + ") - ");

			
		} // for f - fold loop

		System.out.println("----------------------------------------------------------------------------------------------");

		System.out.printf("%10s %10.2f\n", "avg loss", StatUtils.avg(foldLoss));
		System.out.printf("%10s %10.2f\n", "avg std-dev", StatUtils.stdDeviation(foldLoss));
			
	}
}
