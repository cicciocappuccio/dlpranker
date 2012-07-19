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
		ConceptEntailment ce = new ConceptEntailment(id, entailed);
		cmdao.merge(ce);
		HibernateSessionFactory.getSession().getTransaction().commit();
		return ce;
	}
	
	public static void addConceptEntailments(Collection<ConceptEntailment> conceptEntailments) {
		HibernateSessionFactory.getSession().beginTransaction();
		ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		for (ConceptEntailment ce : conceptEntailments) {
			cmdao.merge(ce);
		}
		HibernateSessionFactory.getSession().getTransaction().commit();
	}
	
	public static ConceptEntailment getConceptEntailment(String ontology, String concept, String individual) {
		HibernateSessionFactory.getSession().beginTransaction();
		//ConceptEntailmentDAO cmdao = new ConceptEntailmentDAO();
		ConceptEntailmentId id = new ConceptEntailmentId(ontology, concept, individual);
		//ConceptEntailment cm = cmdao.findById(id);
		ConceptEntailment cm = (ConceptEntailment) HibernateSessionFactory.getSession().createQuery(
			    "select ce from ConceptEntailment as ce where ce.id.concept = ? and ce.id.ontology = ? and ce.id.individual = ?")
			    .setString(0, id.getConcept())
			    .setString(1, id.getOntology())
			    .setString(2, id.getIndividual())
			    .setCacheable(true)
			    .uniqueResult();
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
	
	public static Collection<ConceptEntailment> getOntology(String ontology) {
		HibernateSessionFactory.getSession().beginTransaction();
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ConceptEntailment.class);
		criteria = criteria.add(Restrictions.eq("id.ontology", ontology));
		Collection<ConceptEntailment> cms = criteria.list();
		HibernateSessionFactory.getSession().getTransaction().commit();
		return cms;
	}
	
	public static Collection<ConceptEntailment> getConceptMemberships() {
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
