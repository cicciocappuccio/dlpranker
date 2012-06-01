package it.uniba.di.lacam.fanizzi.features;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Nothing;
import org.dllearner.refinementoperators.RefinementOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neuralnoise.cache.AbstractConceptCache;

public class SpecializeProbable {

	private static final Logger log = LoggerFactory.getLogger(SpecializeProbable.class);
	
	private static final double EPS = 0.05;
	private static final boolean MATERIALIZE = false;
	
	private static void materialize(AbstractReasonerComponent reasoner, AbstractConceptCache cache, Description concept, Set<Individual> individuals) {
		Set<Individual> members = reasoner.getIndividuals(concept);
		for (Individual i : individuals) {
			if (!cache.contains(concept, i)) {
				boolean entailed = (members.contains(i) ? true : false);
				cache.addElement(concept, i, entailed);
			}
		}
	}
	
	private static double conceptProbability(AbstractReasonerComponent reasoner, AbstractConceptCache cache,
			Description concept, Set<Individual> individuals) {
		
		double members = 0;
		double total = individuals.size();
		
		if (MATERIALIZE) {
			boolean need = false;
			
			for (Individual i : individuals) {
				if (!cache.contains(concept, i)) {
					need = true;
				}
			}
			
			if (need) {
				log.info("Materializing concept " + concept + " ..");
				materialize(reasoner, cache, concept, individuals);
				log.info("Materialization of concept " + concept + " finished.");
			}
		}
		
		for (Individual i : individuals) {
			boolean ent = false;
			
			if (cache != null) {
				if (!cache.contains(concept, i)) {
					log.info("Entailing " + concept + " and " + i + " ..");
					ent = reasoner.hasType(concept, i);
					log.info("\tResult: " + ent);
					cache.addElement(concept, i, ent);
				} else {
					ent = cache.get(concept, i);
				}
			} else {
				ent = reasoner.hasType(concept, i);
			}
			
			if (ent)
				members += 1.0;
		}
		
		double ret = members / total;
		
		return ret;
		
	}
	
	public static Set<Description> specialize(
			AbstractReasonerComponent reasoner, AbstractConceptCache cache,
			Description concept, Set<Individual> individuals,
			RefinementOperator r, int maxLength, int depth) {

		String str = concept.toString();

		if (str.contains(" OR ") || str.contains(" AND "))
			return new HashSet<Description>();
		
		double prob = conceptProbability(reasoner, cache, concept, individuals);
		
		log.info("Concept " + concept + " has probability: " + prob);
		
		if (prob < EPS)
			return new HashSet<Description>();
		
		Set<Description> childs = r.refine(concept, maxLength);

		Set<Description> appendChilds = new HashSet<Description>();
		for (Description child : childs) {
			appendChilds.addAll(specialize(reasoner, cache, child, individuals, r, maxLength, depth + 1));
		}

		childs.addAll(appendChilds);
		return childs;
	}
	
}
