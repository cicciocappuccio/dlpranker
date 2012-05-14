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
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.refinementoperators.RhoDown;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;


public class Specialize {

	public static Set<OWLClassExpression> specialize (AbstractReasonerComponent reasoner, Set<OWLClassExpression> concepts, RefinementOperator r)
	{
		Set<Description> desc = new HashSet<Description>();
		for (OWLClassExpression concept : concepts)
			desc.addAll(specialize(reasoner, ConceptUtils.convertToDescription(concept), r, 0));
		
		Set<OWLClassExpression> specialized = new HashSet<OWLClassExpression>();
		for(Description concept : desc)
			specialized.add(ConceptUtils.convertToOWLClassExpression(concept));
			
		return specialized;
	}
	
	public static Set<Description> specialize (AbstractReasonerComponent reasoner, Description concept, RefinementOperator r, int p)
	{
	//	RhoDown r = new RhoDown(reasoner, true, true, true, true, true, true);
		
		Set<Description> childs = new HashSet<Description>();
		if (concept.toString().compareTo("owl:Nothing") != 0)
		{
			System.out.println(concept.toString());
			childs = r.refine(concept, 2, null);
		}
		System.out.println(p + " - " + childs.size());
		for (Description child : childs)
			childs.addAll(specialize(reasoner,  child, r, 1));

		return childs;
	}
}
