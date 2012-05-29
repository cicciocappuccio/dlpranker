package it.uniba.di.lacam.fanizzi.features.psi;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Description;
import org.dllearner.refinementoperators.PsiDown;
import org.dllearner.refinementoperators.PsiUp;
import org.dllearner.refinementoperators.RefinementOperator;
import org.dllearner.refinementoperators.RefinementOperatorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Psi2Wrapper extends RefinementOperatorAdapter {

	private static final Logger log = LoggerFactory.getLogger(Psi2Wrapper.class);

	private RefinementOperator psiUp;
	private RefinementOperator psiDown;
	
	public Psi2Wrapper(AbstractReasonerComponent reasoner) {
		this.psiUp = new PsiUp(null, reasoner);
		this.psiDown = new PsiDown(null, reasoner);
	}
	
	@Override
	public void init() throws ComponentInitException {
		this.psiUp.init();
		this.psiDown.init();
	}

	@Override
	public Set<Description> refine(Description description) {
		Set<Description> refinements = this.psiUp.refine(description);
		refinements.addAll(this.psiDown.refine(description));
		return refinements;
	}
	
	@Override
	public Set<Description> refine(Description concept, int maxLength) {
		log.info("Refining " + concept + " (maximum length: " + maxLength + ") ..");
		Set<Description> refinements = this.psiUp.refine(concept);
		refinements.addAll(this.psiDown.refine(concept));
		
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
