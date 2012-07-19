package refinement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.learningproblems.PosNegLP;
import org.dllearner.refinementoperators.PsiDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.ReasonerUtils;
public class PsiDownWrapper extends PsiDown {

	private static final Logger log = LoggerFactory.getLogger(PsiDownWrapper.class);
	
	public PsiDownWrapper(PosNegLP learningProblem, AbstractReasonerComponent reasoningService) {
		super(learningProblem, reasoningService);	
	}

	@Override
	public Set<Description> refine(Description concept, int maxLength) {

		Set<Description> refinements = new HashSet<Description>();
		Set<String> srefinements = new HashSet<String>();

		refinements.add(concept);
		srefinements.add(concept.toString());
		
		int prevSize = 0;

		do {
			prevSize = refinements.size();
			
			Set<Description> tmp = new HashSet<Description>(refinements);
			Set<String> stmp = new HashSet<String>(srefinements);
			
			for (Description refinement : refinements) {
				
				Set<Description> tmpRefs = super.refine(refinement);
				
				for (Description _tmpRef : tmpRefs) {
					Description tmpRef = ReasonerUtils.normalise(_tmpRef);
					
					if (tmpRef.getLength() <= maxLength && !stmp.contains(tmpRef.toString())) {
						tmp.add(tmpRef);
						stmp.add(tmpRef.toString());
					}
				}
			}
			
			log.info("Refinements so far: " + tmp.size());
			Set<String> difference = Sets.difference(stmp, srefinements);
			if (difference.size() > 0) {
				log.info("\tFor example: " + difference.iterator().next());
			}
			
			refinements = tmp;
			srefinements = stmp;
		} while (refinements.size() > prevSize);

		refinements.remove(concept);
		
		log.info(concept + " has " + refinements.size() + " refinements (maximum length: " + maxLength + ")");
		
		return refinements;
	}
	
	@Override
	public Set<Description> refine(Description concept, int maxLength, List<Description> knownRefinements) {
		throw new RuntimeException();
	}
	
}
