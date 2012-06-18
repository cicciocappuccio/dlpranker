package it.uniba.di.lacam.fanizzi.features.selection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.utils.ConceptUtils;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.utilities.owl.DLLearnerDescriptionConvertVisitor;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;

public class Wrapper {

	private AbstractReasonerComponent reasoner;
	private AbstractConceptCache cache;
	private AbstractScore tScore;
	private GreedyForward pioniere;
	
	public Wrapper(AbstractReasonerComponent reasoner,
			AbstractConceptCache cache, AbstractScore tScore,
			GreedyForward pioniere) {
		super();
		this.reasoner = reasoner;
		this.cache = cache;
		this.tScore = tScore;
		this.pioniere = pioniere;
	}

	public Wrapper(String file) throws Exception {
		super();
		int maxLength = 3;

		KnowledgeSource ks = new OWLFile(file);
		reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		reasoner.init();

		cache = new AsynchronousHibernateConceptCache(file);

		Psi2DownWrapper r = new Psi2DownWrapper(reasoner);
		r.init();

		System.out.println("Reasoner creato");

		tScore = new MHMRScore(cache, reasoner, 0.1);
		
		pioniere = new GreedyForward(cache, reasoner, r,
				maxLength);
	}
	
	public Set<Description> cerca()
	{
		
		Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
		Set<Individual> films = reasoner.getIndividuals(Film);
		
		Set<Description> risultato = pioniere.estrazione(Thing.instance, films,	tScore);

		return risultato;
		
	}
	
	public Set<OWLClassExpression> trasformer(Set<Description> insieme)
	{
		
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		for (Description i : insieme)
			ret.add(ConceptUtils.convertToOWLClassExpression(i));
	
		return ret;
		
	}
	
}
