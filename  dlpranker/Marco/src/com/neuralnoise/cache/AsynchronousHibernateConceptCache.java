package com.neuralnoise.cache;

import java.util.Collection;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.neuralnoise.cache.hibernate.ConceptEntailment;
import com.neuralnoise.cache.hibernate.ConceptEntailmentId;
import com.neuralnoise.cache.hibernate.Database;

public class AsynchronousHibernateConceptCache extends AbstractConceptCache {

	private static int _N = 100000;
	
	private static final Logger log = LoggerFactory.getLogger(AsynchronousHibernateConceptCache.class);
	
	private String ontology;
	private Table<String, String, Boolean> cache;
	private Table<String, String, Boolean> transactions;
	
	private int n;
	
	public AsynchronousHibernateConceptCache(String ontology) throws Exception {
		this(ontology, _N);
	}
	
	public AsynchronousHibernateConceptCache(String ontology, int n) throws Exception {
		this.ontology = ontology;
		this.cache = HashBasedTable.create();
		Collection<ConceptEntailment> entailments = Database.getOntology(ontology);
		log.info("The cache contains " + entailments.size() + " elements");
		for (ConceptEntailment e : entailments) {
			if (e.getEntailed() != null) {
				this.cache.put(e.getId().getConcept().toString(), e.getId().getIndividual().toString(), e.getEntailed());
			}
		}
		this.transactions = HashBasedTable.create();
		this.n = n;
	}
	
	public boolean contains(Description concept, Individual individual) {
		Boolean e = get(concept, individual);
		return (e != null);
	}
	
	public Boolean get(Description concept, Individual individual) {
		Boolean ret = null;
		Description normalised = normalize(concept);
		if (this.cache.contains(normalised.toString(), individual.toString())) {
			ret = this.cache.get(normalised.toString(), individual.toString());
		} else if (this.transactions.contains(normalised.toString(), individual.toString())) {
			ret = this.transactions.get(normalised.toString(), individual.toString());
		}
		return ret;
	}
	
	public void addElement(Description concept, Individual individual, Boolean entailed) {
		Description normalised = normalize(concept);
		this.transactions.put(normalised.toString(), individual.toString(), entailed);
		if (this.transactions.size() > n) {
			save();
		}
	}
	
	public void addElements(Description concept, Collection<Individual> individuals, Boolean entailed) {
		for (Individual individual : individuals) {
			addElement(concept, individual, entailed);
		}
	}
	
	private static Description normalize(Description concept) {
		Description normalised = ReasonerUtils.normalise(concept);
		return normalised;
	}

	@Override
	public void save() {
		Collection<ConceptEntailment> ces = Lists.newLinkedList();
		for (Cell<String, String, Boolean> transaction : this.transactions.cellSet()) {
			String normalised = transaction.getRowKey();
			String individual = transaction.getColumnKey();
			Boolean entailed = transaction.getValue();
			ConceptEntailmentId id = new ConceptEntailmentId(ontology, normalised, individual);
			ConceptEntailment ce = new ConceptEntailment(id, entailed);
			ces.add(ce);
		}
		Database.addConceptEntailments(ces);
		for (Cell<String, String, Boolean> transaction : this.transactions.cellSet()) {
			String normalised = transaction.getRowKey();
			String individual = transaction.getColumnKey();
			Boolean entailed = transaction.getValue();
			this.cache.put(normalised, individual, entailed);
		}
		this.transactions.clear();
	}

}
