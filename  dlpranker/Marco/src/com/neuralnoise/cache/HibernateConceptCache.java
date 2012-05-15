package com.neuralnoise.cache;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.neuralnoise.cache.hibernate.ConceptEntailment;
import com.neuralnoise.cache.hibernate.Database;

public class HibernateConceptCache extends AbstractConceptCache {

	private String ontology;
	
	public HibernateConceptCache(String ontology) throws Exception {
		this.ontology = ontology;
	}

	public boolean contains(OWLClassExpression concept, OWLIndividual individual) {
		OWLClassExpression normalised = normalize(concept);
		ConceptEntailment cm = Database.getConceptMembership(ontology, normalised.toString(), individual.toString());
		return (cm != null);
	}

	public Boolean get(OWLClassExpression concept, OWLIndividual individual) {
		Boolean ret = null;
		OWLClassExpression normalised = normalize(concept);
		ConceptEntailment cm = Database.getConceptMembership(ontology, normalised.toString(), individual.toString());
		if (cm != null) {
			ret = cm.getEntailed();
		}
		return ret;
	}
	
	public void addElement(OWLClassExpression concept, OWLIndividual individual, Boolean entailment) {
		OWLClassExpression normalised = normalize(concept);
		Database.addConceptEntailment(ontology, normalised.toString(), individual.toString(), entailment);	
	}
	
	public void addElements(OWLClassExpression concept, Collection<OWLIndividual> individuals, Boolean entailed) {
		OWLClassExpression normalised = normalize(concept);
		for (OWLIndividual individual : individuals) {
			Database.addConceptEntailment(ontology, normalised.toString(), individual.toString(), entailed);
		}
	}
	
	public void removeElement(OWLClassExpression concept, OWLIndividual individual) {
		OWLClassExpression normalised = normalize(concept);
		Database.removeConceptMembership(ontology, normalised.toString(), individual.toString());
	}
	
	public void removeConcept(OWLClassExpression _concept) {
		OWLClassExpression normalised = normalize(_concept);
		Database.removeConcept(ontology, normalised.toString());
	}
	
	public void removeOWLIndividual(OWLIndividual individual) {
		Database.removeIndividual(ontology, individual.toString());
	}
	
	private static OWLClassExpression normalize(OWLClassExpression concept) {
		OWLClassExpression normalised = concept; //ReasonerUtils.normalise(concept);
		return normalised;
	}

	public void flush() {
		Database.removeOntology(ontology);
	}

	@Override
	public boolean contains(OWLClassExpression concept) {
		OWLClassExpression normalised = normalize(concept);
		return (Database.getConcept(ontology, normalised.toString()).size() != 0);
	}
}
