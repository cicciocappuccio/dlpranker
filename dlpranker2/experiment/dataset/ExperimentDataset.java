/**
 * 
 */
package dataset;

import java.util.Iterator;
import java.util.List;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.Lists;

/**
 * @author Marco
 *
 */

public final class ExperimentDataset {

	private ExperimentDataset() { }
	
	public static final List<Tupla> getRatingsOfFilm(List<Tupla> lista, Individual film) {
		
		List<Tupla> ret = Lists.newArrayList();
		for(Tupla i : lista)
			if (i.getFilm().equals(film))
				ret.add(i);
		
		return ret;
	}
	
	public static final List<Tupla> getUsers(List<Tupla> lista)
	{
		List<Tupla> ret = Lists.newArrayList();
	
		for (Tupla i : lista)
		{
			boolean trovato = false;
			Iterator<Tupla> y = ret.iterator();
			Tupla yy = null;
			while (!trovato && y.hasNext())
			{
				yy = y.next();
				if (i.getUser().equals(yy.getUser()))
					trovato = true;
			}
			
			if (!trovato)
				ret.add(i);
		}
		
		return ret;
	}
	
	public static final List<Tupla> getFilms(List<Tupla> lista)
	{
		List<Tupla> ret = Lists.newArrayList();
	
		for (Tupla i : lista)
		{
			boolean trovato = false;
			Iterator<Tupla> y = ret.iterator();
			Tupla yy = null;
			while (!trovato && y.hasNext())
			{
				yy = y.next();
				if (i.getFilm().equals(yy.getFilm()))
					trovato = true;
			}
			
			if (!trovato)
				ret.add(i);
		}
		
		return ret;
	}
	
	public static final List<Tupla> getRatingsOfUser(List<Tupla> lista, Individual user) {

		List<Tupla> ret = Lists.newArrayList();
		for(Tupla i : lista)
			if (i.getUser().equals(user))
				ret.add(i);
		
		return ret;
	}
	
	private static final int getRatingMode(List<Tupla> lista) {
		int[] modeArray = new int[5];

		for (Tupla i : lista)
			modeArray[i.getValue() - 1]++;

		Integer mode = -1;
		
		for (Integer i : modeArray)
			if (mode < i)
				mode = i;
		
		return mode + 1;
	}

	private static final double getRatingAVG(List<Tupla> lista) {
		double sum = 0.0;
		
		for (Tupla i : lista)
		{
			sum += i.getValue();
		}
		
		return sum / ((double) lista.size());
	}

}
