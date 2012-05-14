package it.uniba.di.lacam.fanizzi.features;

import it.uniba.di.lacam.fanizzi.utils.ConceptUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.RhoDown;
import org.semanticweb.owlapi.model.OWLClassExpression;


public class Specialize {

	public static Set<OWLClassExpression> specialize (String file, OWLClassExpression concept) throws ComponentInitException
	{
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		
		reasoner.init();
		return specialize (reasoner, concept);
	}
	
	public static Set<OWLClassExpression> specialize (String file, Set<OWLClassExpression> concepts) throws ComponentInitException
	{
		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		
		reasoner.init();
		return specialize (reasoner, concepts);
	}
	
	
	public static Set<OWLClassExpression> specialize (AbstractReasonerComponent reasoner, OWLClassExpression concept)
	{
		RhoDown r = new RhoDown(reasoner, true, true, true, true, true, true);

		System.out.println(concept);
		Set<Description> descriptions = r.refine(ConceptUtils.convertToDescription(concept), 4, null);
		
		Set<OWLClassExpression> concepts = new HashSet<OWLClassExpression>();
		for(Description description : descriptions)
			concepts.add(ConceptUtils.convertToOWLClassExpression(description));
		
		return concepts;
	}
	
	public static Set<OWLClassExpression> specialize (AbstractReasonerComponent reasoner, Set<OWLClassExpression> concepts)
	{
		RhoDown r = new RhoDown(reasoner, true, true, true, true, true, true);
		
		Set<Description> descriptionsSet = new HashSet<Description>();
		for(OWLClassExpression concept : concepts)
		{
			System.out.println(concept);
			if (!concept.toString().equals("<owl:Nothing>")) {
				Set<Description> descriptions = r.refine(ConceptUtils.convertToDescription(concept), 3, null);
				descriptionsSet.addAll(descriptions);
			}
		}
		
		Set<OWLClassExpression> classes = new HashSet<OWLClassExpression>();
		for(Description description : descriptionsSet)
			classes.add(ConceptUtils.convertToOWLClassExpression(description));
		
		return classes;
	}
}
