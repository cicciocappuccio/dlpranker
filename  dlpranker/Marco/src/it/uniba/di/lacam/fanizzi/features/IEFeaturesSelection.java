package it.uniba.di.lacam.fanizzi.features;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.AbstractConceptCache;

public class IEFeaturesSelection {
	private AbstractConceptCache cache;
	
	public IEFeaturesSelection(AbstractConceptCache cache)
	{
		this.cache = cache;
	}
	
	public Set<Description> IEFS()
	{
		
		Set<Description> insieme = new HashSet<Description>();
		
		
		return new HashSet<Description>();
	}
	
	
	public double E(Description x, Set<Individual> individuals)
	{
		int e = 0; 
		for (Individual i : individuals)
		{
			if (!cache.contains(x, i)) {
				if(cache.get(x, i))
					e++;
			}
		}
		
		return ((double)e)/((double)individuals.size());
	}
	
	
	public double I(Description x, Description y, Set<Individual> individuals)
	{
		double tot = 0;
		for (Individual m individuals)
		{
			for (Individual n individuals)
			{
				tot += p(x,y) * Math.log(p(x,y)/(p(x)*p(y)));
			}
		}
		 
		return tot;
	}
	
	
	
	
	

	
	public double p (Description x, Description y, Set<Individual> individuals)
	{
		return 0;
	}

	public double p (Description x, Set<Individual> individuals)
	{
		int e = 0; 
		for (Individual i : individuals)
		{
			if (cover(x, i))
				e++;
		}
		
		return ((double)e)/((double)individuals.size());
	}


}
