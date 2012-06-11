package it.uniba.di.lacam.fanizzi.features.ie;

import it.uniba.di.lacam.fanizzi.utils.StatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.neuralnoise.cache.AbstractConceptCache;


public class IEFeaturesSelection {

	private static final double ENTROPY_THRESHOLD = ((double) 1) / Math.sqrt(2);
	private static final double MINIMUM_ENTROPY_ADDING = 0.0001;

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
	
	public Set<Description> IEFS()
	{
		Set<Description> conceptSet = new HashSet<Description>();

		double entropy = Double.NEGATIVE_INFINITY;
		boolean exit = false;
		
		Set<Description> fc = new HashSet<Description>();
		fc.addAll(features);
		
		do
		{
			BiMap<Description, Double> candidates;
			candidates = HashBiMap.create();

			for (Description i : fc)
			{
				Set<Description> newConceptSet = new HashSet<Description>();
				newConceptSet.addAll(conceptSet);
				newConceptSet.add(i);
				candidates.put(i, entropy(newConceptSet, i));
			}
			
			double max = StatUtils.max(candidates.inverse().keySet());
			
			if (entropy < max)
			{
				Description winner = candidates.inverse().get(max);
				conceptSet.add(winner);
				fc.remove(winner);
				entropy = max;
			}
			else
				exit = true;
		}
		while (entropy < ENTROPY_THRESHOLD && !exit);

		return conceptSet;
	}

	
	public Set<Description> greedy ()
	{
		Set<Description> conceptSet = new HashSet<Description>();

		double entropy = Double.NEGATIVE_INFINITY;
		
		BiMap<Description, Double> candidates;// = new HashMap<Description, Double>()
		candidates = HashBiMap.create();

		for (Description i : features)
		{
			candidates.put(i, calc.E(i, individulas));
		}
		
		int listIndex = 0;
		List<Double> sortedValue = new ArrayList<Double>(candidates.values());
		Collections.sort(sortedValue);
		
		do
		{
			Description candidate = candidates.inverse().get(sortedValue.get(listIndex++));
			
			
			Set<Description> newConceptSet = new HashSet<Description>();
			newConceptSet.addAll(conceptSet);
			newConceptSet.add(candidate);
			
			
			double iEntropy = entropy(newConceptSet, candidate);
			
			
			if (iEntropy > MINIMUM_ENTROPY_ADDING)
			{
				conceptSet.add(candidate);
				entropy += iEntropy;
			}

		}
		while (entropy < ENTROPY_THRESHOLD && !(listIndex >= sortedValue.size()));
		
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
