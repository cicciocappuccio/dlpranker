package it.uniba.di.lacam.fanizzi.features;

import it.uniba.di.lacam.fanizzi.utils.ConceptUtils;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.refinementoperators.RefinementOperator;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class Specialize {

	public static Set<OWLClassExpression> specialize(
			AbstractReasonerComponent reasoner,
			Set<OWLClassExpression> concepts, RefinementOperator r,
			int maxLength) {
		Set<Description> desc = new HashSet<Description>();
		for (OWLClassExpression concept : concepts)
			desc.addAll(specialize(reasoner,
					ConceptUtils.convertToDescription(concept), r, maxLength, 0));

		Set<OWLClassExpression> specialized = new HashSet<OWLClassExpression>();
		for (Description concept : desc)
			specialized.add(ConceptUtils.convertToOWLClassExpression(concept));

		return specialized;
	}

	public static Set<Description> specialize(
			AbstractReasonerComponent reasoner, Description concept,
			RefinementOperator r, int maxLength, int depth) {
		// RhoDown r = new RhoDown(reasoner, true, true, true, true, true,
		// true);

		Set<Description> childs = new HashSet<Description>();
		// if (Nothing.instance != concept)// .
		// toString().compareTo("owl:Nothing") != 0)
		if (concept.toString().compareTo("owl:Nothing") != 0)
		{
			// System.out.println(concept.toString());
			//childs = r.refine(concept, maxLength, null);
			childs = r.refine(concept, maxLength);
			// childs = r.refine(concept);
		}
		System.out.println("p: " + depth + " - childs.size: " + childs.size());

		Set<Description> appendChilds = new HashSet<Description>();
		for (Description child : childs) {

			// System.out.println("													contenuto");
			appendChilds.addAll(specialize(reasoner, child, r, maxLength, depth + 1));

		}

		childs.addAll(appendChilds);
		return childs;
	}

}
