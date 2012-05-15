package com.neuralnoise.cache.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class Database {
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				HibernateSessionFactory.closeSession();
			}
		});
	}
	
	private Database() { }
	
	public static void clean() {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		Collection<ConceptEntailment> cms = cmdao.findAll();
		for (ConceptEntailment cm : cms) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static ConceptEntailment addConceptEntailment(String ontology, String concept, String individual, Boolean entailed) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		ConceptEntailmentId id = new ConceptEntailmentId(ontology, concept, individual);
		ConceptEntailment cm = new ConceptEntailment(id, entailed);
		cmdao.merge(cm);
		HibernateSessionFactory.getSession().getTransaction().commit();
		return cm;
	}
	
	public static ConceptEntailment getConceptMembership(String ontology, String concept, String individual) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		ConceptEntailmentId id = new ConceptEntailmentId(ontology, concept, individual);
		ConceptEntailment cm = cmdao.findById(id);
		HibernateSessionFactory.getSession().getTransaction().commit();
		return cm;
	}
	
	public static Collection<ConceptEntailment> getConcept(String ontology, String concept) {
		HibernateSessionFactory.getSession().beginTransaction();
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ConceptEntailment.class);
		criteria = criteria.add(Restrictions.eq("id.ontology", ontology));
		criteria = criteria.add(Restrictions.eq("id.concept", concept));
		Collection<ConceptEntailment> cms = criteria.list();
		HibernateSessionFactory.getSession().getTransaction().commit();
		return cms;
	}
	
	public static Collection<ConceptEntailment> getConceptEntailments() {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		Collection<ConceptEntailment> cms = cmdao.findAll();
		HibernateSessionFactory.getSession().getTransaction().commit();
		return cms;
	}

	public static void removeConceptMembership(String ontology, String concept, String individual) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		ConceptEntailmentId id = new ConceptEntailmentId(ontology, concept, individual);
		ConceptEntailment cm = cmdao.findById(id);
		if (cm != null) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static void removeConcept(String ontology, String concept) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ConceptEntailment.class);
		criteria = criteria.add(Restrictions.eq("id.ontology", ontology));
		criteria = criteria.add(Restrictions.eq("id.concept", concept));
		Collection<ConceptEntailment> cms = criteria.list();
		for (ConceptEntailment cm : cms) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static void removeOntology(String ontology) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ConceptEntailment.class);
		criteria = criteria.add(Restrictions.eq("id.ontology", ontology));
		Collection<ConceptEntailment> cms = criteria.list();
		for (ConceptEntailment cm : cms) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static void removeIndividual(String ontology, String individual) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ConceptEntailment.class);
		criteria = criteria.add(Restrictions.eq("id.ontology", ontology));
		criteria = criteria.add(Restrictions.eq("id.individual", individual));
		Collection<ConceptEntailment> cms = criteria.list();
		for (ConceptEntailment cm : cms) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static void removeConceptMembership(ConceptEntailment cm) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		if (cm != null) {
			cmdao.delete(cm);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static void removeConceptMemberships(Collection<ConceptEntailment> cms) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		for (ConceptEntailment cm : cms) {
			if (cm != null) {
				cmdao.delete(cm);
			}
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
}
