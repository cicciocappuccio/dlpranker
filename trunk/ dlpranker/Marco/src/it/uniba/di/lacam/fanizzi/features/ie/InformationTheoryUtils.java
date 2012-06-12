package it.uniba.di.lacam.fanizzi.features.ie;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.neuralnoise.cache.AbstractConceptCache;

public class InformationTheoryUtils {

	AbstractConceptCache cache;
	
	public InformationTheoryUtils(AbstractConceptCache cache) {
		this.cache = cache;
	}
	
	
	public double E(Description X, Set<Individual> individuals) {
	
		Map<Boolean, Double> pX = new HashMap<Boolean, Double>();
		
		pX.put(true, 1.0);
		pX.put(false, 1.0);
		
		Double ZX = 1.0;

		for (Individual individual : individuals) {
			Boolean Xcov = cover(X, individual);
			if (Xcov != null) {
				pX.put(Xcov, pX.get(Xcov) + 1.0);
				ZX += 1.0;
			}
		}
		
		for (Entry<Boolean, Double> px : pX.entrySet()) {
			pX.put(px.getKey(), px.getValue() / ZX);
		}
		
		double ret = 0.0;
		Double pxi = pX.get(true);
		ret -= (pxi * Math.log(pxi));
		pxi = pX.get(false);	
		ret -= (pxi * Math.log(pxi));
		
		return ret;
	}
	
	public double I(Description X, Description Y, Set<Individual> individuals) {
		
		Map<Boolean, Double> pX = new HashMap<Boolean, Double>();
		Map<Boolean, Double> pY = new HashMap<Boolean, Double>();
		Table<Boolean, Boolean, Double> pXY = HashBasedTable.create();
		
		pX.put(true, 1.0);
		pX.put(false, 1.0);
		
		pY.put(true, 1.0);
		pY.put(false, 1.0);
		
		pXY.put(true, true, 1.0);
		pXY.put(true, false, 1.0);
		pXY.put(false, true, 1.0);
		pXY.put(false, false, 1.0);
		
		Double ZX = 1.0;
		Double ZY = 1.0;
		Double ZXY = 1.0;

		for (Individual individual : individuals) {
			Boolean Xcov = cover(X, individual);
			if (Xcov != null) {
				pX.put(Xcov, pX.get(Xcov) + 1.0);
				ZX += 1.0;
			}
			
			Boolean Ycov = cover(Y, individual);
			if (Ycov != null) {
				pY.put(Ycov, pY.get(Ycov) + 1.0);
				ZY += 1.0;
			}
			
			if (Xcov != null && Ycov != null) {
				pXY.put(Xcov, Ycov, pXY.get(Xcov, Ycov) + 1.0);
				ZXY += 1.0;
			}
		}
		
		for (Entry<Boolean, Double> px : pX.entrySet()) {
			pX.put(px.getKey(), px.getValue() / ZX);
		}
		
		for (Entry<Boolean, Double> py : pY.entrySet()) {
			pY.put(py.getKey(), py.getValue() / ZY);
		}
		
		for (Cell<Boolean, Boolean, Double> pxy : pXY.cellSet()) {
			pXY.put(pxy.getRowKey(), pxy.getColumnKey(), pxy.getValue() / ZXY);
		}
		
		double ret = 0.0;
		
		for (Boolean xi = false; xi == false; xi = true) {
			for (Boolean yj = false; yj == false; yj = true) {
				Double pxiyj = pXY.get(xi, yj);
				Double pxi = pX.get(xi);
				Double pyj = pY.get(yj);
				ret += pxiyj * Math.log(pxiyj / (pxi * pyj));
			}
		}
		
		return ret;
	}
	
	
	public boolean cover(Description x,  Individual i)
	{
		if (!cache.contains(x, i))
			return cache.get(x, i);
		else	
			return false;									// correct me.
	}
}
