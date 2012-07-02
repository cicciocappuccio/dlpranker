package it.uniba.di.lacam.fanizzi.features.utils;

import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;

public class EIUtils {

	private AbstractConceptCache cache;
	private AbstractReasonerComponent reasoner;
	
	private Map<String, Double> H;
	private Table<String, String, Double> I;
	
	public EIUtils(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner) {
		super();
		this.cache = cache;
		this.reasoner = reasoner;
		
		this.H = Maps.newHashMap();
		this.I = HashBasedTable.create();
	}

	public double H (Description x, Set<Individual> individuals)
	{
		if(H.containsKey(x.toString()))
			return H.get(x.toString());
		else
		{
			double h = HCalc(x, individuals);
			H.put(x.toString(), h);
			return h;
		}
					
	}
	
	public double HCalc(Description x, Set<Individual> individuals) {
		
		
		Map<LogicValue, Integer> numX = new HashMap<LogicValue, Integer>();

		for (LogicValue valore : LogicValue.values())
			numX.put(valore, 1);

		Double ZX = 1.0;
		
		
		for (Individual i : individuals) {

			Inference inf = new Inference(cache, reasoner);
			LogicValue infVal = inf.cover(x, i);
			numX.put(infVal, numX.get(infVal) + 1);
			ZX += 1.0;
		}
		
	
		Map<LogicValue, Double> prX = new HashMap<LogicValue, Double>();

		for (LogicValue valore : LogicValue.values())
		{
			prX.put(valore, (numX.get(valore) / ZX));
			
		}
			
		
		double e = 0.0;

		for (LogicValue valore : LogicValue.values())
			
		{	
			e -= (prX.get(valore) * Math.log(prX.get(valore)));
		}
		return e;
	}

	public double I(Description x, Description y, Set<Individual> individuals)
	{
		if(I.contains(x.toString(),y.toString()))
			return I.get(x.toString(),y.toString());
		else
		{
			double i = ICalc(x, y, individuals);
			I.put(x.toString(), y.toString(), i);
			return i;
		}
					
	}
	
	public double ICalc(Description x, Description y, Set<Individual> individuals) {
		int numInd = individuals.size();

		Map<LogicValue, Double> numX = new HashMap<LogicValue, Double>();
		Map<LogicValue, Double> numY = new HashMap<LogicValue, Double>();

		Table<LogicValue, LogicValue, Double> prXY = HashBasedTable.create();

		for (LogicValue i : LogicValue.values()) {
			numX.put(i, 1.0);
			numY.put(i, 1.0);
			for (LogicValue j : LogicValue.values())
				prXY.put(i, j, 1.0);
		}

		Double ZX = 1.0;
		Double ZC = 1.0;
		Double ZXC = 1.0;

		
		for (Individual i : individuals) {

			Inference infX = new Inference(cache, reasoner);
			LogicValue infValX = infX.cover(x, i);

			Inference infY = new Inference(cache, reasoner);
			LogicValue infValY = infY.cover(y, i);
			
			numX.put(infValX, numX.get(infValX) + 1);
			ZX += 1.0;
			numY.put(infValY, numY.get(infValY) + 1);
			ZC += 1.0;
			
			prXY.put(infValX, infValY, prXY.get(infValX, infValY) + 1.0);
			ZXC += 1.0;
		}

		for (LogicValue i : LogicValue.values()) {
			numX.put(i, (numX.get(i) / ZX));
			numY.put(i, (numY.get(i) / ZC));
			
			for (LogicValue j : LogicValue.values())
				//prXY.put(i, j, (prXY.get(i, j) > 0 ? (double) prXY.get(i, j)/ numInd : Double.MIN_VALUE));
				prXY.put(i,j, (prXY.get(i,j) / ZXC));
			
		}

		double sum = 0.0;

			
		
		for (LogicValue i : LogicValue.values()) {
			for (LogicValue j : LogicValue.values()) {
				sum += (prXY.get(i, j) * Math.log(prXY.get(i, j) / (numX.get(i) * numY.get(j))));
			}
		}
		
		return sum;
	}

}
