package refinement;

import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Description;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.refinementoperators.RefinementOperatorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsiWrapper extends RefinementOperatorAdapter {

	private static final Logger log = LoggerFactory.getLogger(PsiWrapper.class);

	private RefinementOperator psiUp;
	private RefinementOperator psiDown;
	
	public PsiWrapper(AbstractReasonerComponent reasoner) {
		this.psiUp = new PsiUpWrapper(null, reasoner);
		this.psiDown = new PsiDownWrapper(null, reasoner);
	}
	
	@Override
	public void init() throws ComponentInitException {
		this.psiUp.init();
		this.psiDown.init();
	}

	@Override
	public Set<Description> refine(Description concept) {
		Set<Description> refinements = this.psiUp.refine(concept);
		refinements.addAll(this.psiDown.refine(concept));
		return refinements;
	}
	
	@Override
	public Set<Description> refine(Description concept, int maxLength) {
		
		log.info("Refining " + concept + " (maximum length: " + maxLength + ") ..");
		
		Set<Description> refinements = this.psiUp.refine(concept, maxLength);
		refinements.addAll(this.psiDown.refine(concept, maxLength));
		return refinements;
	}
	
}
