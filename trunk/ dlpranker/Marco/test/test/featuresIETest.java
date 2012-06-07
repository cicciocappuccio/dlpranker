package test;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class featuresIETest {

	public static void main (String[] args) throws Throwable
	{
		String file = "res/fragmentOntology10.owl";
		
		AbstractConceptCache cache = new AsynchronousHibernateConceptCache(file);
		
		
		
	}
}
