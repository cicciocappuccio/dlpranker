package it.uniba.di.lacam.fanizzi.extraction;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class FilmRatedExtractionKB {


    private PelletReasoner reasoner;
    private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;
	
	private OWLOntology ontology;
	
	// matrice del kernel  




	public FilmRatedExtractionKB (String urlOwlFile)
	{
		System.out.println(urlOwlFile);
		manager = OWLManager.createOWLOntologyManager();
		
        ontology = null;
        
        try {
        	ontology = manager.loadOntologyFromOntologyDocument(new File(urlOwlFile));
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
        
        dataFactory = manager.getOWLDataFactory();
        //reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);
        
        System.out.println("ONTOLOGY: " + ontology);
        
        reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
        
        System.out.println("000: CONSISTENT? " + reasoner.isConsistent());
        
		System.out.println("\nKB loaded. \n");	
	}

	
	
	public OWLOntology getOntology()
	{
		return ontology;
	}
	
	public PelletReasoner getReasoner()
	{
		return reasoner;
	}
	
	public OWLOntologyManager getManager()
	{
		return manager;
	}
	
	public OWLDataFactory getDataFactory()
	{
		return dataFactory;
	}
	
	
	
	
	
	/**
	* 
	* @param concepts concetti da cui estrarre gli individui
	* @param ratingAnnProp AnnotationProperty del rating 
	* @param ratingValue AnnotationProperty dal valore del rating
	* 
	* @return  mappa con individual del film in dbpedia come chiave e media dei rating come valore
	*/
	
	public String[] filmRatingMDB (OWLAnnotationProperty ratingAnnProp)
	{
		System.out.println("filmRatingMDB");
		Set<OWLNamedIndividual> individui = ontology.getIndividualsInSignature();
		String[] dbpediaResourceTemp = new String[individui.size()];
		
//		System.out.println(individui.size());
		
		int count = 0;
		int countElse = 0;
		for (OWLNamedIndividual ind : individui)
		{
			Set<OWLAnnotation> individualAnnotations = ind.getAnnotations(ontology, ratingAnnProp);
//			System.out.println(individualAnnotations);
			if (individualAnnotations.size() > 0)
			{
				Set<OWLIndividual> sameAs = ind.getSameIndividuals(ontology);
				
				for (OWLIndividual sameAsInd : sameAs)
				{
//					System.out.println(count + " - " + ind.toStringID() + "  -  "  + sameAsInd.toStringID() + " #" + individualAnnotations.size());
					dbpediaResourceTemp[count++] = sameAsInd.toStringID();
				}
				//System.out.println(ind.toStringID() + " #" + individualAnnotations.size());
			}

		}
		
		String[] dbpediaResource = new String[count];

		for (int i = 0; i < count; i++)
		{
			dbpediaResource[i] = dbpediaResourceTemp[i];
		}

		return dbpediaResource;
	}
	
}

		
		