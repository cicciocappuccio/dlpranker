package test;

import it.uniba.di.lacam.fanizzi.experiment.dataset.ExperimentRatingD;

import java.util.Collections;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.kb.OWLFile;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.reasoning.OWLAPIReasoner;

public class DatasetTest {

	
	public static void main (String[] args) throws ComponentInitException, ParseException
	{
		
		KnowledgeSource ks = new OWLFile("res/dataset2.rdf");
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(Collections.singleton(ks));
		reasoner.init();
		
		ExperimentRatingD mio = new ExperimentRatingD(reasoner);
		
		
	}
	
}
