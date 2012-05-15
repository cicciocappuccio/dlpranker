package com.neuralnoise.cache.hibernate;

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptMembershipDAO extends BaseHibernateDAO {
	
	private static final Logger log = LoggerFactory.getLogger(ConceptMembershipDAO.class);
	
	public void save(ConceptEntailment transientInstance) {
		log.debug("saving ConceptMembership instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ConceptEntailment persistentInstance) {
		log.debug("deleting ConceptMembership instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ConceptEntailment findById(ConceptEntailmentId id) {
		log.debug("getting ConceptMembership instance with id: " + id);
		try {
			ConceptEntailment instance = (ConceptEntailment) getSession().get(ConceptEntailment.class, id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ConceptEntailment> findByExample(ConceptEntailment instance) {
		log.debug("finding ConceptMembership instance by example");
		try {
			List<ConceptEntailment> results = (List<ConceptEntailment>) getSession()
					.createCriteria(ConceptEntailment.class).add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding ConceptMembership instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from ConceptMembership as model where model." + propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all ConceptMembership instances");
		try {
			String queryString = "from ConceptMembership";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public ConceptEntailment merge(ConceptEntailment detachedInstance) {
		log.debug("merging ConceptMembership instance");
		try {
			ConceptEntailment result = (ConceptEntailment) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ConceptEntailment instance) {
		log.debug("attaching dirty ConceptMembership instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ConceptEntailment instance) {
		log.debug("attaching clean ConceptMembership instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}