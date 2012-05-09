package it.uniba.di.lacam.fanizzi.experiment.dataset;

import it.uniba.di.lacam.fanizzi.DLKRating;
import it.uniba.di.lacam.fanizzi.OntologyModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class ExperimentFilm implements ExperimentDataset {
	
	
	private OntologyModel ontologyModel;
	
//	private Map<OWLNamedIndividual, ArrayList<Integer>> ratingMap;
//	private BiMap<Example, Integer> index;
	private BiMap<FilmAVG, Integer> individualIndex;
	
	private int ratingMassimo;
//	private int numeroDiIndividui;
	
	public ExperimentFilm(OntologyModel ontologyModel)
	{
		this.ontologyModel = ontologyModel;
		OWLClass film = new OWLClassImpl(ontologyModel.getDataFactory(), IRI.create("http://dbpedia.org/ontology/Film"));
		OWLClass[] mio = {film};
		
		OWLAnnotationProperty ratingAnnProp = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#hasReview"));  
		OWLAnnotationProperty ratingValue = new OWLAnnotationPropertyImpl(ontologyModel.getDataFactory(), IRI.create("http://purl.org/stuff/rev#rating"));  
		
		createIndex (mio, ratingAnnProp, ratingValue);
	}
	
		

	public void createIndex(OWLClass[] concepts, OWLAnnotationProperty ratingAnnProp, OWLAnnotationProperty ratingValue)
	{
		System.out.println("filmRating");
		
		individualIndex = HashBiMap.create();

//		int indexMatrix = 0;
		int numeroDiIndividui = 0;
		ratingMassimo = -1;
		
		
		// OLD
//		Map<OWLNamedIndividual, Double> mappa = new HashMap<OWLNamedIndividual, Double>();
		// end OLD

		for (OWLClass concept : concepts)
		{
			
			Set<OWLIndividual> conceptIndividuals = concept.getIndividuals(ontologyModel.getOntology());
			System.out.println(conceptIndividuals.size());
			
			for (OWLIndividual individual : conceptIndividuals)
			{
				System.out.println(individual);
				Set<OWLIndividual> sameInds = individual.getSameIndividuals(ontologyModel.getOntology());
				for (OWLIndividual sameInd : sameInds)
				{
					OWLNamedIndividual sameNamedInd = (OWLNamedIndividual) sameInd;
					// System.out.println(sameNamedInd);
					Set<OWLAnnotation> individualAnnotations = sameNamedInd.getAnnotations(ontologyModel.getOntology(), ratingAnnProp);
					// System.out.println(individualAnnotations.size());
					double sum = 0, count = 0;
					for (OWLAnnotation ann : individualAnnotations)
					{
						if (ontologyModel.getOntology().containsIndividualInSignature(IRI.create(ann.getValue().toString())))
						{
							OWLNamedIndividual rating = new OWLNamedIndividualImpl(ontologyModel.getDataFactory(), IRI.create(ann.getValue().toString()));
							Set<OWLAnnotation> valore = rating.getAnnotations(ontologyModel.getOntology(), ratingValue);
							for (OWLAnnotation val : valore)
							{
								String str = val.getValue().toString();
								str = str.substring(1, str.length() - 1);
								sum = sum + Integer.parseInt(str);
								count++;
								
								if (numeroDiIndividui == 0)						// start with the first value
									ratingMassimo = Integer.valueOf(str);
								else if (ratingMassimo < Integer.valueOf(str))
									ratingMassimo = Integer.valueOf(str);
							}
						}
					}
					// System.out.println(sum/count);
					if (count > 0)
					{
						individualIndex.put(new FilmAVG((OWLNamedIndividual) individual, (int) Math.round(Double.valueOf(sum / count))), numeroDiIndividui++);
					}
				}
			}
		}
	}
		
	public int getRatingValue (int indice)
	{
		return individualIndex.inverse().get(indice).getRating();
	}
	
	public OWLNamedIndividual getIndividual (int indice)
	{
		return individualIndex.inverse().get(indice).getIndividual();
	}
	
	public int maxRating()
	{
		return ratingMassimo;
	}
	
	public int size()
	{
		return individualIndex.size();
	}
	
	public OWLNamedIndividual[] getIndividuals()
	{
		OWLNamedIndividual[] individui = new OWLNamedIndividual[individualIndex.size()];
		for (int i = 0; i < individualIndex.size(); i++)
		{
			individui[i] = individualIndex.inverse().get(i).getIndividual();
		}
		return individui;
	}
	
	public int indexIndividual(int indice)
	{
		return indice;
	}
}
