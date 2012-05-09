package it.uniba.di.lacam.fanizzi.experiment.type;

import it.uniba.di.lacam.fanizzi.DLKRating;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class KFoldsCrossValitationExperiment {

	private DLKRating dlr;

	public KFoldsCrossValitationExperiment(DLKRating dlr) {
		this.dlr = dlr;
	}

	public void kfxvExperiment(double[][] kernel, int nFolds, OWLNamedIndividual[] examples, int[] ratings, int nRatings)
	{
		System.out.println("K-fold cross validation Experiment on ontology");
		System.out.println("\n\n" + nFolds + " fold");

		double mio = Math.round((((double) examples.length) / nFolds) - 0.5);
		int mia = (int) Math.round(mio);
		System.out.println(mio);

		/*
		 * int[] ntestExs = new int[nFolds]; double[] foldLoss = new
		 * double[nFolds];
		 */

		for (int f = 0; f < nFolds; f++)
		{

			Integer[] trainingExs = new Integer[(int) mio * (nFolds - 1)];
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

				dlr.kernelPerceptronRank(kernel, trainingExs, ratings, w, theta, nRatings);
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
