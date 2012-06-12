package it.uniba.di.lacam.fanizzi.features.utils;

import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;

public class EIUtils {

	private AbstractConceptCache cache;
	private AbstractReasonerComponent reasoner;

	public EIUtils(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner) {
		super();
		this.cache = cache;
		this.reasoner = reasoner;
	}

	public double H(Description x, Set<Individual> individuals) {

		int numIndA = individuals.size();

		Map<LogicValue, Integer> numX = new HashMap<LogicValue, Integer>();

		for (LogicValue valore : LogicValue.values())
			numX.put(valore, 0);

		for (Individual i : individuals) {

			Inference inf = new Inference(cache, reasoner);
			LogicValue infVal = inf.cover(x, i);

			numX.put(infVal, numX.get(infVal) + 1);
		}

		Map<LogicValue, Double> prX = new HashMap<LogicValue, Double>();

		for (LogicValue valore : LogicValue.values())
			prX.put(valore, (numX.get(valore) > 0 ? (double) numX.get(valore)
					/ numIndA : Double.MIN_VALUE));

		double e = 0.0;

		for (LogicValue valore : LogicValue.values())
			e -= (prX.get(valore) * Math.log(prX.get(valore)));

		return e;
	}

	public double I(Description x, Description y, Set<Individual> individuals) {
		int numInd = individuals.size();

		Map<LogicValue, Double> numX = new HashMap<LogicValue, Double>();
		Map<LogicValue, Double> numY = new HashMap<LogicValue, Double>();

		Table<LogicValue, LogicValue, Double> prXY = HashBasedTable.create();

		for (LogicValue i : LogicValue.values()) {
			numX.put(i, 0.0);
			numY.put(i, 0.0);
			for (LogicValue j : LogicValue.values())
				prXY.put(i, j, 0.0);
		}

		for (Individual i : individuals) {

			Inference infX = new Inference(cache, reasoner);
			LogicValue infValX = infX.cover(x, i);

			Inference infY = new Inference(cache, reasoner);
			LogicValue infValY = infY.cover(x, i);

			numX.put(infValX, numX.get(infValX) + 1);
			numY.put(infValY, numY.get(infValY) + 1);

			prXY.put(infValX, infValY, prXY.get(infValX, infValY) + 1.0);

		}

		for (LogicValue i : LogicValue.values()) {
			numX.put(i, (numX.get(i) > 0 ? (double) numX.get(i) / numInd
					: Double.MIN_VALUE));
			numY.put(i, (numY.get(i) > 0 ? (double) numY.get(i) / numInd
					: Double.MIN_VALUE));

			for (LogicValue j : LogicValue.values())
				prXY.put(i, j, (prXY.get(i, j) > 0 ? (double) prXY.get(i, j)
						/ numInd : Double.MIN_VALUE));
		}

		double sum = 0.0;

		for (LogicValue i : LogicValue.values()) {

			for (LogicValue j : LogicValue.values()) {
				sum += (prXY.get(i, j) * Math.log(prXY.get(i, j)
						/ (numX.get(i) * numY.get(j))));
			}
		}

		return sum;
	}

}
