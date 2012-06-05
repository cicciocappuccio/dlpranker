package it.uniba.di.lacam.fanizzi.features;

import it.uniba.di.lacam.fanizzi.features.utils.InformationTheoryUtils;
import it.uniba.di.lacam.fanizzi.utils.StatUtils;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.neuralnoise.cache.AbstractConceptCache;


public class IEFeaturesSelection {

	private static final double ENTROPY_THRESHOLD = ((double) 1) / Math.sqrt(2);

	private AbstractConceptCache cache;
	private InformationTheoryUtils calc;

	private Set<Individual> individulas;
	private Set<Description> features;

	public IEFeaturesSelection(AbstractConceptCache cache, Set<Description> features, Set<Individual> individuals)
	{
		this.cache = cache;

		this.individulas = individuals;
		this.features = features;

		calc = new InformationTheoryUtils(this.cache);
	}

	
	
	public Set<Description> IEFS(Description rootConcept)
	{
		Set<Description> conceptSet = new HashSet<Description>();

		double entropy = Double.NEGATIVE_INFINITY;
		boolean exit = false;
		
		do
		{
			BiMap<Description, Double> candidates;
			candidates = HashBiMap.create();

			for (Description i : features)
			{
				Set<Description> newConceptSet = new HashSet<Description>();
				newConceptSet.addAll(conceptSet);
				newConceptSet.add(i);
				candidates.put(i, entropy(newConceptSet, i));
			}
			
			double max = StatUtils.max(candidates.inverse().keySet());
			
			if (entropy < max)
			{
				conceptSet.add(candidates.inverse().get(max));
				entropy = max;
			}
			else
				exit = true;
		}
		while (entropy < ENTROPY_THRESHOLD && !exit);

		return conceptSet;
	}

	
	
	public double entropy(Set<Description> set, Description i)
	{
		double sum = 0;
		double e = calc.E(i, individulas);

		for (Description y : set)
			sum += e - calc.I(i, y, individulas);

		return sum;
	}
}
