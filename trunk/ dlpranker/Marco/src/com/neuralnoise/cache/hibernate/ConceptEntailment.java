package com.neuralnoise.cache.hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "ConceptEntailment")
@Table(name = "ConceptEntailment")
public class ConceptEntailment extends AbstractConceptEntailment implements java.io.Serializable {

	private static final long serialVersionUID = -7460806116369874677L;
	
	public ConceptEntailment() {
		super();
	}
	
	public ConceptEntailment(ConceptEntailmentId id) {
		super(id);
	}
	
	public ConceptEntailment(ConceptEntailmentId id, Boolean entailed) {
		super(id, entailed);
	}
	
}
