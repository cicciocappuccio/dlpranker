package com.neuralnoise.cache;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
public abstract class AbstractConceptCache {

	public AbstractConceptCache() { }
	
	public abstract boolean contains(OWLClassExpression concept);
	
	public abstract boolean contains(OWLClassExpression concept, OWLIndividual OWLIndividual);
	
	public abstract Boolean get(OWLClassExpression concept, OWLIndividual OWLIndividual);
	
	public abstract void addElement(OWLClassExpression concept, OWLIndividual OWLIndividual, Boolean entailed);
	
	public abstract void addElements(OWLClassExpression concept, Collection<OWLIndividual> OWLIndividuals, Boolean entailed);
	
	public abstract void removeElement(OWLClassExpression concept, OWLIndividual OWLIndividual);
	
	public abstract void removeConcept(OWLClassExpression concept);
	
	public abstract void removeOWLIndividual(OWLIndividual OWLIndividual);
	
	public abstract void flush();
	
}
