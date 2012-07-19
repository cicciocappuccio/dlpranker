package com.neuralnoise.cache.hibernate;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractConceptEntailment implements java.io.Serializable {

	private static final long serialVersionUID = -8042726031995933280L;

	private ConceptEntailmentId id;
	private Boolean entailed;
	
	public AbstractConceptEntailment() { }
	
	public AbstractConceptEntailment(ConceptEntailmentId id) {
		this.setId(id);
	}
	
	public AbstractConceptEntailment(ConceptEntailmentId id, Boolean entailed) {
		this.setId(id);
		this.setEntailed(entailed);
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "ontology", column = @Column(name = "ontology", nullable = false)),
			@AttributeOverride(name = "concept", column = @Column(name = "concept", nullable = false)),
			@AttributeOverride(name = "individual", column = @Column(name = "individual", nullable = false))		
	})
	public ConceptEntailmentId getId() {
		return id;
	}

	public void setId(ConceptEntailmentId id) {
		this.id = id;
	}
	
	@Column(name = "entailed", nullable = false)
	public Boolean getEntailed() {
		return this.entailed;
	}

	public void setEntailed(Boolean entailed) {
		this.entailed = entailed;
	}
}
