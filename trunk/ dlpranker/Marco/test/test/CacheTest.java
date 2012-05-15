package test;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.HibernateConceptCache;
import com.neuralnoise.cache.hibernate.Database;

public class CacheTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		AbstractConceptCache cache = new HibernateConceptCache("abc");
		
		Database.getConceptEntailments();
		
		System.out.println("AAA");
		
		System.in.read();
	}

}
