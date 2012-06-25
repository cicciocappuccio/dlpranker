package it.uniba.di.lacam.fanizzi.experiment.type;

import it.uniba.di.lacam.fanizzi.DLKRating;
import it.uniba.di.lacam.fanizzi.KernelMatrix;
import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentDataset;
import it.uniba.di.lacam.fanizzi.utils.StatUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class BootstrapExperiment {

	private Random generator = new Random(SEED);
	private static final int SEED = 100;


	public BootstrapExperiment()
	{
		
		
	}
/*
	public void bootstrapExperiment(KernelMatrix kernel, ExperimentDataset dati, int nFolds) throws IOException
	{
		System.out.println("\n\n" + nFolds	+ "-fold BOOTSTRAP Experiment on ontology");

		int nRatings = dati.maxRating(); // valore massimo del rating

		// main loop on the folds
		int[] ntestExs = new int[nFolds];
		double[] foldLoss = new double[nFolds];

		for (int f = 0; f < nFolds; f++)
		{

			double lossCount = 0.0;

			System.out.println("**************************************** Fold #" + f + " ****************************************");

			List<OWLNamedIndividual> trainingExs = new ArrayList<OWLNamedIndividual>();
			Set<OWLNamedIndividual> testExs = new HashSet<OWLNamedIndividual>();
			
			Map<OWLNamedIndividual, Double> wMap = new HashMap<OWLNamedIndividual, Double>();
			
			for (int r = 0; r < (dati.size()); r++)
			{
				OWLNamedIndividual ran = dati.random();
				trainingExs.add(ran);
				wMap.put(dati.getIndividual(ran), 0.0);
			}
			
			Set<OWLNamedIndividual> a = wMap.keySet();
			
			for (OWLNamedIndividual r : dati.getIndividuals())
			{
				if (!a.contains(r))
					testExs.add(r);
			}

			ntestExs[f] = testExs.size();
			System.out.println("training: " + trainingExs.size() + "       testExs.length: " + testExs.size());
			// training phase: using all examples but those in the f-th
			// partition
			System.out.println("Training is starting...");

//			double[] w = new double[trainingExs.length]; // vettore della stessa dimensione del numero di esempi di training
//			double[] w = new double[dati.getIndividuals().length]; // MODIFICATO: vettore della stessa dimensione del numero di film

			double[] theta = new double[nRatings]; // rating 1, rating 2, ...,
													// rating MAX

			DLKRating.kernelPerceptronRank(dati, kernel, trainingExs, wMap, theta);
			System.out.printf("\nmodel induced \n");

			System.out.println("...end of Training.\n");

			System.out.println("Testing is starting...");
			// test phase: test all examples in the f-th partition
			int i = 1;
			for (OWLNamedIndividual te : testExs)
			{
				System.out.println("\nF#" + f + "ranking example " + i++ + "/" + testExs.size() + ": " + te);

				System.out.println("\ninductive Classification ------ ");

				int inducedRank = kernel.rank(te, wMap, theta, nRatings);
				System.out.printf("%d (%d)\t", (inducedRank + 1), dati.getRatingValue(te));
				lossCount += Math.abs(inducedRank - dati.getRatingValue(te)) / (double) nRatings;
				
				System.out.print("\n");
			}
/*
			System.out.println("\n\n -------------------------------------------------- Outcomes Fold #" + f);
			System.out.printf("\n%10s %10s\n", "Q.#", "Loss");
			foldLoss[f] = lossCount / (double) ntestExs[f];
			System.out.printf(" %10f\n", foldLoss[f]);
			
			//System.out.println(wMap);
			
			System.out.println("Press <Enter> to continue =)))");
			System.in.read();

			
		} // for f - fold loop

		System.out.println("----------------------------------------------------------------------------------------------");

		System.out.printf("%10s %10.2f\n", "avg loss", StatUtils.avg(foldLoss));
		System.out.printf("%10s %10.2f\n", "avg std-dev", StatUtils.stdDeviation(foldLoss));
	} // bootstrap
*/
}
