package com.neuralnoise.cache.hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ConceptEntailmentId implements java.io.Serializable {

	private static final long serialVersionUID = -3658467167700380101L;

	private String ontology;
	private String concept;
	private String individual;
	
	public ConceptEntailmentId() { }
	
	public ConceptEntailmentId(String ontology, String concept, String individual) {
		this.setOntology(ontology);
		this.setConcept(concept);
		this.setIndividual(individual);
	}

	@Column(name = "ontology", nullable = false, length = 256)
	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	@Column(name = "concept", nullable = false, length = 4096)
	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	@Column(name = "individual", nullable = false, length = 4096)
	public String getIndividual() {
		return individual;
	}

	public void setIndividual(String individual) {
		this.individual = individual;
	}
	
}
