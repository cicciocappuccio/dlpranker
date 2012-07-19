package refinement;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Description;
import org.dllearner.refinementoperators.PsiDown;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.refinementoperators.RefinementOperatorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Psi2DownWrapper extends RefinementOperatorAdapter {

	private static final Logger log = LoggerFactory.getLogger(Psi2DownWrapper.class);

	private RefinementOperator psiDown;
	
	public Psi2DownWrapper(AbstractReasonerComponent reasoner) {
		this.psiDown = new PsiDown(null, reasoner);
	}
	
	@Override
	public void init() throws ComponentInitException {
		this.psiDown.init();
	}

	@Override
	public Set<Description> refine(Description description) {
		Set<Description> refinements = this.psiDown.refine(description);
		return refinements;
	}
	
	@Override
	public Set<Description> refine(Description concept, int maxLength) {
		log.info("Refining " + concept + " (maximum length: " + maxLength + ") ..");
		Set<Description> refinements = this.psiDown.refine(concept);
		
		Set<Description> tooLong = new HashSet<Description>();
		for (Description refinement : refinements) {
			if (refinement.getLength() > maxLength) {
				tooLong.add(refinement);
			}
		}
		
		for (Description tl : tooLong) {
			refinements.remove(tl);
		}
		
		return refinements;
	}
	
}