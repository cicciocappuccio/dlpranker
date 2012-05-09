package it.uniba.di.lacam.fanizzi.extraction;

import java.util.Locale;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;


public class DbpediaExtractionTest {


	
	public static void main(String[] args) throws Exception {
		
		System.out.println("\nDL-KRatings");		
		
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/RatingsOntCopia.owl";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/SmallRatingsOnt.owl";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/SmallRatingsOnt3.owl";
		//static String urlOwlFile = "res/luf_statements_20110727.owl";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/dataset/dataset.owl";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/dataset/linkedmdb-latest-minidump.rdf";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/Marco/fragmentOntology3.owl";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/dataset/mio2.rdf";
		//String urlOwlFile = "res/mio3.rdf";
		String urlOwlFile = "res/luf_statements_plus_LMDB_interlink.rdf";
		//String urlOwlFile = "res/mio4.rdf";
		//static String urlOwlFile = "C:/Users/Marco/Desktop/tesi/dataset/mio2Copia.rdf";
		
		
		Locale.setDefault(Locale.US);
		FilmRatedExtractionKB filmExtractor = new FilmRatedExtractionKB(urlOwlFile);

		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		OWLAnnotationProperty ratingAnnProp = new OWLAnnotationPropertyImpl(filmExtractor.getDataFactory(), IRI.create("http://purl.org/stuff/rev#hasReview"));  
		
		
		String[] risorse = filmExtractor.filmRatingMDB(ratingAnnProp);
		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		System.out.println(risorse.length);
		for(String a : risorse)
		{
			System.out.println(a);
		}

		//SparqlExtractionTest.estraction(risorse);

	}
	
}
