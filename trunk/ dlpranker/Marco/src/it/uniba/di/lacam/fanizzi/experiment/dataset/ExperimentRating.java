package it.uniba.di.lacam.fanizzi.experiment.dataset;

import it.uniba.di.lacam.fanizzi.DLKRating;
import it.uniba.di.lacam.fanizzi.OntologyModel;
import it.uniba.di.lacam.fanizzi.utils.StatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.*;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Table;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * @author Marco
 *
 */
public class ExperimentRating implements ExperimentDataset
{
	private OntologyModel ontologyModel;
	
	private static final int SEED = 100;
	private Random generator;

	
//	private Map<OWLNamedIndividual, ArrayList<Integer>> ratingMap;
//	private Table<OWLNamedIndividual, OWLNamedIndividual, Integer> index;
	private Map<OWLNamedIndividual, OWLNamedIndividual> ratingFilm;
	private BiMap<Example, Integer> index;
	private BiMap<OWLNamedIndividual, Integer> individualIndex;
	
	private int ratingMassimo;
//	private int numeroDiIndividui;
	
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
		
		ratingFilm = new HashMap();
		index = HashBiMap.create();
		individualIndex = HashBiMap.create();
		int indexMatrix = 0;
		int numeroDiIndividui = 0;
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
								index.put(new Example((OWLNamedIndividual) individual, rating), indexMatrix++);
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
						individualIndex.put((OWLNamedIndividual) individual, numeroDiIndividui++);
						
					}
						
				}
			}
		}
	}
	
	
	public int maxRating()
	{
		return ratingMassimo;
	}
	
	public Example example(int indice)
	{
		return index.inverse().get(indice);
	}
	
	/**
	 * @param indice indice della matrice
	 * @return retituisce il valore numerico del rating
	 */
	public int getRatingValue (int indice)
	{
		OWLAnnotationProperty ratingValue = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#rating"));  
		Set<OWLAnnotation> valore = example(indice).getRating().getAnnotations(ontologyModel.getOntology(), ratingValue);
		String str = "-1";
		for (OWLAnnotation val : valore)
		{
			str = val.getValue().toString();
			str = str.substring(1, str.length() - 1);
		}
		return (int) Integer.valueOf(str);
	}
	public int getRatingValue (OWLNamedIndividual rating)
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
	
	public OWLNamedIndividual getRating (int indice)
	{
		return index.inverse().get(indice).getRating();
	}
	
	public OWLNamedIndividual getIndividual (int indice)
	{
		return index.inverse().get(indice).getIndividual();
	}
	public OWLNamedIndividual getIndividual (OWLNamedIndividual rating)
	{
		return ratingFilm.get(rating);
	}

	public OWLNamedIndividual[] getIndividuals()
	{
		OWLNamedIndividual[] individui = new OWLNamedIndividual[individualIndex.size()];
		for (int i = 0; i < individualIndex.size(); i++)
		{
			individui[i] = individualIndex.inverse().get(i);
		}
		return individui;
	}
	
	public int size()
	{
		return index.size();
	}
	
	public int indexIndividual(int indice)
	{
		return individualIndex.get(getIndividual (indice));
	}
	
	public Set<OWLNamedIndividual> getIndividualsSet()
	{
		return individualIndex.keySet();
	}
	
	public OWLNamedIndividual random()
	{
		Object[] values = ratingFilm.keySet().toArray();
		return (OWLNamedIndividual) values[generator.nextInt(values.length)];
	}
}
