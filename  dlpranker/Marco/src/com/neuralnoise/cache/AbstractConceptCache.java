package com.neuralnoise.cache;

import java.util.Collection;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

public abstract class AbstractConceptCache {

	public AbstractConceptCache() { }
	
	public abstract boolean contains(Description concept);
	
	public abstract boolean contains(Description concept, Individual individual);
	
	public abstract Boolean get(Description concept, Individual individual);
	
	public abstract void addElement(Description concept, Individual individual, Boolean entailed);
	
	public abstract void addElements(Description concept, Collection<Individual> individuals, Boolean entailed);
	
	public abstract void removeElement(Description concept, Individual individual);
	
	public abstract void removeConcept(Description concept);
	
	public abstract void removeIndividual(Individual individual);
	
	public abstract void flush();
	
}
