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
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.neuralnoise.cache.AbstractConceptCache;

public class EIUtils {

	private Inference inference;
	
	private Map<String, Double> H;
	private Table<String, String, Double> I;
	
	private LogicValue[] values = LogicValue.values();
	//private Set<LogicValue> values = Sets.newHashSet(LogicValue.TRUE, LogicValue.UNKNOWN);

	public EIUtils(Inference inference) {
		this.inference = inference;
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

		for (LogicValue valore : values) {
			//numX.put(valore, 1);
			numX.put(valore, 0);
		}

		Double ZX = 0.0; //1.0;
		
		
		for (Individual i : individuals) {

			Inference inf = inference;
			LogicValue infVal = inf.cover(x, i);
			
			//infVal = (infVal == LogicValue.FALSE ? LogicValue.UNKNOWN : infVal);
			
			numX.put(infVal, numX.get(infVal) + 1);
			ZX += 1.0;
		}
		
	
		Map<LogicValue, Double> prX = new HashMap<LogicValue, Double>();

		for (LogicValue valore : values)
		{
			prX.put(valore, (numX.get(valore) / ZX));
			
		}
			
		double e = 0.0;

		for (LogicValue valore : values)
		{
			if (prX.get(valore) != 0.0)
				e -= (prX.get(valore) * Math.log(prX.get(valore)));
		}
		return e;
	}

	public double I(Description x, Description y, Set<Individual> individuals)
	{
		String sx = x.toString(), sy = y.toString();
		
		if (I.contains(sx, sy)) {
			return I.get(sx, sy);
		} else if (I.contains(sy, sx)) {
			return I.get(sy, sx);
		} else {
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

		for (LogicValue i : values) {
			numX.put(i, 0.0);//1.0);
			numY.put(i, 0.0);//1.0);
			for (LogicValue j : values)
				prXY.put(i, j, 0.0);//1.0);
		}

		Double ZX = 0.0;//1.0;
		Double ZC = 0.0;//1.0;
		Double ZXC = 0.0;//1.0;

		
		for (Individual i : individuals) {

			Inference infX = inference;
			LogicValue infValX = infX.cover(x, i);

			//infValX = (infValX == LogicValue.FALSE ? LogicValue.UNKNOWN : infValX);
			
			Inference infY = inference;
			LogicValue infValY = infY.cover(y, i);
			
			//infValY = (infValY == LogicValue.FALSE ? LogicValue.UNKNOWN : infValY);
			
			numX.put(infValX, numX.get(infValX) + 1.0);
			ZX += 1.0;
			numY.put(infValY, numY.get(infValY) + 1.0);
			ZC += 1.0;
			
			prXY.put(infValX, infValY, prXY.get(infValX, infValY) + 1.0);
			ZXC += 1.0;
		}

		for (LogicValue i : values) {
			numX.put(i, (numX.get(i) / ZX));
			numY.put(i, (numY.get(i) / ZC));
			
			for (LogicValue j : values)
				//prXY.put(i, j, (prXY.get(i, j) > 0 ? (double) prXY.get(i, j)/ numInd : Double.MIN_VALUE));
				prXY.put(i,j, (prXY.get(i,j) / ZXC));
			
		}

		double sum = 0.0;

			
		
		for (LogicValue i : values) {
			for (LogicValue j : values) {
				double val = 0.0;
				if (prXY.get(i, j) != 0.0)
					val = (prXY.get(i, j) * Math.log(prXY.get(i, j) / (numX.get(i) * numY.get(j))));
				sum += val;
			}
		}
		
		return sum;
	}

}
