package com.neuralnoise.cache;

import java.util.Collection;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class VolatileConceptCache extends AbstractConceptCache {

	private static final Logger log = LoggerFactory.getLogger(VolatileConceptCache.class);
	
	private String ontology;
	private Table<String, String, Boolean> cache;

	public VolatileConceptCache(String ontology) throws Exception {
		this.ontology = ontology;
		this.cache = HashBasedTable.create();
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
		}
		return ret;
	}
	
	public void addElement(Description concept, Individual individual, Boolean entailed) {
		Description normalised = normalize(concept);
		this.cache.put(normalised.toString(), individual.toString(), entailed);
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

	}

}
