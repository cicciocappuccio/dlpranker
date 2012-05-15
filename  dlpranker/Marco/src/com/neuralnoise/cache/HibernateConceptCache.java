package com.neuralnoise.cache;

import java.util.Collection;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.neuralnoise.cache.hibernate.ConceptEntailment;
import com.neuralnoise.cache.hibernate.Database;

public class HibernateConceptCache extends AbstractConceptCache {

	private String ontology;
	
	public HibernateConceptCache(String ontology) throws Exception {
		this.ontology = ontology;
	}

	public boolean contains(Description concept, Individual individual) {
		Description normalised = normalize(concept);
		ConceptEntailment cm = Database.getConceptMembership(ontology, normalised.toString(), individual.toString());
		return (cm != null);
	}

	public Boolean get(Description concept, Individual individual) {
		Boolean ret = null;
		Description normalised = normalize(concept);
		ConceptEntailment cm = Database.getConceptMembership(ontology, normalised.toString(), individual.toString());
		if (cm != null) {
			ret = cm.getEntailed();
		}
		return ret;
	}
	
	public void addElement(Description concept, Individual individual, Boolean entailment) {
		Description normalised = normalize(concept);
		Database.addConceptEntailment(ontology, normalised.toString(), individual.toString(), entailment);	
	}
	
	public void addElements(Description concept, Collection<Individual> individuals, Boolean entailed) {
		Description normalised = normalize(concept);
		for (Individual individual : individuals) {
			Database.addConceptEntailment(ontology, normalised.toString(), individual.toString(), entailed);
		}
	}
	
	public void removeElement(Description concept, Individual individual) {
		Description normalised = normalize(concept);
		Database.removeConceptMembership(ontology, normalised.toString(), individual.toString());
	}
	
	public void removeConcept(Description _concept) {
		Description normalised = normalize(_concept);
		Database.removeConcept(ontology, normalised.toString());
	}
	
	public void removeIndividual(Individual individual) {
		Database.removeIndividual(ontology, individual.toString());
	}
	
	private static Description normalize(Description concept) {
		Description normalised = ReasonerUtils.normalise(concept);
		return normalised;
	}

	public void flush() {
		Database.removeOntology(ontology);
	}

	@Override
	public boolean contains(Description concept) {
		Description normalised = normalize(concept);
		return (Database.getConcept(ontology, normalised.toString()).size() != 0);
	}
}
