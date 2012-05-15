package com.neuralnoise.cache.hibernate;

import org.hibernate.Session;

public interface IBaseHibernateDAO {
	public Session getSession();
}