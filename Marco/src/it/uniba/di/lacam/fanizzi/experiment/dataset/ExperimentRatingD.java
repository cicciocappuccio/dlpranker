package it.uniba.di.lacam.fanizzi.experiment.dataset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

public class ExperimentRatingD {

	private Map<Individual, Individual> ratingFilm;
	private Set<Individual> individuals;

	private int ratingMassimo;
	
	private AbstractReasonerComponent reasoner;

	
	public ExperimentRatingD(AbstractReasonerComponent reasoner) throws ParseException {
		super();
		this.reasoner = reasoner;
		
		Description film = KBParser.parseConcept("\"http://dbpedia.org/ontology/Film\"");		
		
		createIndex(film);
	}

	
	
	private void createIndex(Description concept)
	{
		System.out.println("rating");

		ratingFilm = new HashMap<Individual, Individual>();
		individuals = new HashSet<Individual>();

		int indexMatrix = 0;
		// int numeroDiIndividui = 0;
		ratingMassimo = -1;

		Set<Individual> conceptIndividuals = reasoner.getIndividuals(concept);// .getIndividuals(ontologyModel.getOntology());
		System.out.println(conceptIndividuals.size());

		ObjectProperty sameAs = new ObjectProperty("owl:sameAs");

		for (Individual individual : conceptIndividuals)
		{
			Set<Individual> sameInds = reasoner.getRelatedIndividuals(individual, sameAs);
			
			System.out.println(individual + " - " + sameInds);
	

		}

	}


}
