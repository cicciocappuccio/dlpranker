package com.neuralnoise.cache;

import java.util.Collection;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.neuralnoise.cache.hibernate.ConceptEntailment;
import com.neuralnoise.cache.hibernate.Database;
//import com.neuralnoise.utils.ReasonerUtils;

public class HibernateConceptCache extends AbstractConceptCache {

	private String ontology;
	
	public HibernateConceptCache(String ontology) throws Exception {
		this.ontology = ontology;
		this.cache = HashBasedTable.create();
		
		Collection<ConceptEntailment> entailments = Database.getOntology(ontology);
		for (ConceptEntailment e : entailments) {
			if (e.getEntailed() != null) {
				this.cache.put(e.getId().getConcept(), e.getId().getIndividual(), e.getEntailed());
			}
		}
		
	}

	private Table<String, String, Boolean> cache;
	
	public boolean contains(Description concept, Individual individual) {
		Boolean e = get(concept, individual);
		return (e != null);
	}
	
	public Boolean get(Description concept, Individual individual) {
		Boolean ret = null;
		Description normalised = normalize(concept);
		if (!cache.contains(normalised.toString(), individual.toString())) {
			//System.out.print("-"); System.out.flush();
			ConceptEntailment cm = Database.getConceptEntailment(ontology, normalised.toString(), individual.toString());
			if (cm != null) {
				ret = cm.getEntailed();
				this.cache.put(normalised.toString(), individual.toString(), ret);
			}
		} else {
			//System.out.print("+"); System.out.flush();
			ret = this.cache.get(normalised.toString(), individual.toString());
		}
		return ret;
	}
	
	public void addElement(Description concept, Individual individual, Boolean entailment) {
		Description normalised = normalize(concept);
		Database.addConceptEntailment(ontology, normalised.toString(), individual.toString(), entailment);
		this.cache.put(normalised.toString(), individual.toString(), entailment);
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

	public boolean contains(Description concept) {
		Description normalised = normalize(concept);
		return (Database.getConcept(ontology, normalised.toString()).size() != 0);
	}

	@Override
	public void save() { }
}
