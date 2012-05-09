package it.uniba.di.lacam.fanizzi.utils;

import org.dllearner.core.owl.Description;
import org.dllearner.utilities.owl.DLLearnerDescriptionConvertVisitor;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class ConceptUtils {

	public static Description convertToDescription(OWLClassExpression owlClassExpression) {
		return DLLearnerDescriptionConvertVisitor.getDLLearnerDescription(owlClassExpression);
	}
	
	public static OWLClassExpression convertToOWLClassExpression(Description description) {
		return OWLAPIConverter.getOWLAPIDescription(description);
	}
	
}
