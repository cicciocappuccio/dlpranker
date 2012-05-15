package com.neuralnoise.cache.hibernate;

import org.hibernate.Session;

public class BaseHibernateDAO implements IBaseHibernateDAO {
	
	public Session getSession() {
		return HibernateSessionFactory.getSession();
	}
	
}