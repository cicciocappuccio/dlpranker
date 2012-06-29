package it.uniba.di.lacam.fanizzi.experiment.dataset;

import it.uniba.di.lacam.fanizzi.OntologyModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Marco
 *
 */
public class ExperimentRating
{
	private OntologyModel ontologyModel;
	
	private static final int SEED = 100;
	private Random generator;

	private Map<OWLNamedIndividual, OWLNamedIndividual> ratingFilm;
	private Set<OWLNamedIndividual> individuals;
	
	private int ratingMassimo;
	
	
	public Table<Individual, Individual, Integer> createTable()
	{
		
		Table<Individual, Individual, Integer> ret = HashBasedTable.create();
		
		for (OWLNamedIndividual i : ratingFilm.keySet())
		{
			ret.put(OWLAPIConverter.convertIndividual(i), OWLAPIConverter.convertIndividual(ratingFilm.get(i)), (Integer) getRatingValue(i));
		}

		return ret;
		
	}
	
	
	
	
	/**
	 * @param ontologyModel
	 * 
	 * costruttore: richiama rating con le giuste classi ed AnnotationPropertys  
	 * 
	 */
	public ExperimentRating(OntologyModel ontologyModel)
	{
		this.ontologyModel = ontologyModel;
		OWLClass film = new OWLClassImpl(ontologyModel.getDataFactory(), IRI.create("http://dbpedia.org/ontology/Film"));
		OWLClass[] mio = {film};
		
		OWLAnnotationProperty ratingAnnProp = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#hasReview"));  
		OWLAnnotationProperty ratingValue = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#rating"));  
		
		
		
		createIndex (mio, ratingAnnProp, ratingValue);
		
		generator = new Random(SEED);
	}
	
	
	/**
	 * 
	 * @param concepts
	 *            concetti da cui estrarre gli individui
	 * @param ratingAnnProp
	 *            AnnotationProperty del rating
	 * @param ratingValue
	 *            AnnotationProperty dal valore del rating
	 * 
	 * @return mappa con individual del film in dbpedia come chiave e media dei
	 *         rating come valore
	 */

	private void createIndex(OWLClass[] concepts, OWLAnnotationProperty ratingAnnProp, OWLAnnotationProperty ratingValue)
	{
		System.out.println("rating");
		
		ratingFilm = new HashMap<OWLNamedIndividual, OWLNamedIndividual>();
		individuals = new HashSet<OWLNamedIndividual>();
		
		int indexMatrix = 0;
//		int numeroDiIndividui = 0;
		ratingMassimo = -1;

		for (OWLClass concept : concepts)
		{
			Set<OWLIndividual> conceptIndividuals = concept.getIndividuals(ontologyModel.getOntology());
			System.out.println(conceptIndividuals.size());
			
			for (OWLIndividual individual : conceptIndividuals)
			{
				Set<OWLIndividual> sameInds = individual.getSameIndividuals(ontologyModel.getOntology());
				for (OWLIndividual sameInd : sameInds)
				{
					boolean trovato = false;
					
					OWLNamedIndividual sameNamedInd = (OWLNamedIndividual) sameInd;
					Set<OWLAnnotation> individualAnnotations = sameNamedInd.getAnnotations(ontologyModel.getOntology(), ratingAnnProp);
					//rev#hasReview

					for (OWLAnnotation ann : individualAnnotations)
					{ //se esiste un individuo con uri    ann
						if (ontologyModel.getOntology().containsIndividualInSignature(IRI.create(ann.getValue().toString())))
						{
							OWLNamedIndividual rating = new OWLNamedIndividualImpl(ontologyModel.getDataFactory(), IRI.create(ann.getValue().toString()));
							Set<OWLAnnotation> valore = rating.getAnnotations(ontologyModel.getOntology(), ratingValue);
							for (OWLAnnotation val : valore)
							{
								String str = val.getValue().toString();
								str = str.substring(1, str.length() - 1);

								if (indexMatrix == 0)						// start with the first value
									ratingMassimo = Integer.valueOf(str);
								else if (ratingMassimo < Integer.valueOf(str))
									ratingMassimo = Integer.valueOf(str);
								
								ratingFilm.put(rating, (OWLNamedIndividual) individual);
								trovato = true;
							}
						}
					}
					if (trovato)
					{
						/*
						System.out.println(individual);
						System.out.println(numeroDiIndividui);
						*/
						//numeroDiIndividui++;
						individuals.add((OWLNamedIndividual) individual);
					}
				}
			}
		}
	}
	

	
	
	
	
	
	protected int maxRating()
	{
		return ratingMassimo;
	}

	/**
	 * @param rating rating di cui si vuol conoscere il valore
	 * @return retituisce il valore numerico del rating
	 */

	protected int getRatingValue (OWLNamedIndividual rating)
	{
		OWLAnnotationProperty ratingValue = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#rating"));  
		Set<OWLAnnotation> valore = rating.getAnnotations(ontologyModel.getOntology(), ratingValue);
		String str = "-1";
		for (OWLAnnotation val : valore)
		{
			str = val.getValue().toString();
			str = str.substring(1, str.length() - 1);
		}
		return (int) Integer.valueOf(str);
	}
	

	protected OWLNamedIndividual getIndividual (OWLNamedIndividual rating)
	{
		return ratingFilm.get(rating);
	}
	
	protected int size()
	{
		return ratingFilm.size();
	}

	protected Set<OWLNamedIndividual> getIndividuals(int a)
	{
		return individuals;
	}
	
	protected OWLNamedIndividual random(int a)
	{
		Object[] values = ratingFilm.keySet().toArray();
		return (OWLNamedIndividual) values[generator.nextInt(values.length)];
	}
	
	protected Set<OWLNamedIndividual> getRatings(int a)
	{
		return ratingFilm.keySet();
	}
	
	protected Set<OWLNamedIndividual> getRatings(OWLNamedIndividual individual)
	{
		Set<OWLNamedIndividual> ritorno = new HashSet<OWLNamedIndividual>();
		
		for (OWLNamedIndividual rating : ratingFilm.keySet())
		{
			//System.out.println(getIndividual(rating).toString() + " - " + individual.toString());
			if (getIndividual(rating) == individual)
			{
				ritorno.add(rating);
			}
		}
		return ritorno;
	}
}