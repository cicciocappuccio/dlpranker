package it.uniba.di.lacam.fanizzi.experiment.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniba.di.lacam.fanizzi.DLKRating;
import it.uniba.di.lacam.fanizzi.KernelMatrix;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class KFoldsCrossValitationExperiment {

	private DLKRating dlr;

	public KFoldsCrossValitationExperiment(DLKRating dlr) {
		this.dlr = dlr;
	}

	public void kfxvExperiment(KernelMatrix kernel, ExperimentDataset dati, int nFolds)
	{
		System.out.println("K-fold cross validation Experiment on ontology");
		System.out.println("\n\n" + nFolds + " fold");

		double foldCardinality = Math.round((((double) dati.size()) / nFolds) - 0.5);
		int mia = (int) Math.round(foldCardinality);
		System.out.println(foldCardinality);

		/*
		 * int[] ntestExs = new int[nFolds]; double[] foldLoss = new
		 * double[nFolds];
		 */

		for (int f = 0; f < nFolds; f++)
		{
			Set<OWLNamedIndividual> trainingExs = new HashSet<OWLNamedIndividual>();
			Set<OWLNamedIndividual> testExs = new HashSet<OWLNamedIndividual>();
			
			Map<OWLNamedIndividual, Double> wMap = new HashMap<OWLNamedIndividual, Double>();
			
			dati.
			
			for (int i = 0; i < foldCardinality; i++)
			{
				if ((i >= f * mio) && (i < (f + 1) * mio))
				{
					trainingExs.p
				}
			}
			
			
			
			
			
			Integer[] trainingExs = new Integer[(int) foldCardinality * (nFolds - 1)];
			Integer[] testExs = new Integer[(int) mio];
			int trainingIndex = 0;
			int testIndex = 0;

			for (int i = 0; i < (mio * nFolds); i++)
			{
				if ((i >= f * mio) && (i < (f + 1) * mio))
				{
					testExs[testIndex] = i;
					testIndex++;
					System.out.println("		" + i);
				}
				else
				{
					trainingExs[trainingIndex] = i;
					trainingIndex++;
					System.out.println(i);
				}
				// training phase: using all examples but those in the f-th
				// partition
				System.out.println("Training is starting...");

				double[] w = new double[trainingExs.length];
				double[] theta = new double[nRatings];

				System.out.println("Training is starting..." + mia + " - " + nFolds + " - " + nFolds * mia);
				System.out.println("Training is starting..." + nRatings);

				DLKRating.kernelPerceptronRank( , kernel, trainingExs, alpha, theta) 
					.kernelPerceptronRank(kernel, trainingExs, ratings, w, theta, nRatings);
				
				System.out.printf("\nmodel induced \n\n");

				System.out.println("...end of Training.\n\n");

				System.out.println("Testing is starting...");
				for (int te = 0; te < testExs.length; te++)
				{
					int indTestEx = testExs[te];

					System.out.println("\nF#" + f + "ranking example "	+ (te + 1) + "/" + testExs.length + " [" + indTestEx + "] " + examples[indTestEx]);

					// System.out.println("\ninductive Classification ------ ");

					int inducedRank = dlr.rank(kernel, indTestEx, w, theta, nRatings);
					System.out.printf("%d (%d)\t", inducedRank,	ratings[indTestEx]);
					// lossCount +=
					// Math.abs(inducedRank-ratings[indTestEx])/(double)nRatings;

					System.out.print("\n");

				}
			}
		}
	}
}
