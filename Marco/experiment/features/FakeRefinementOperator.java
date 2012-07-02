package features;

import java.util.List;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.refinementoperators.RefinementOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.ReasonerUtils;

public class FakeRefinementOperator implements RefinementOperator {

	private static final Logger log = LoggerFactory.getLogger(FakeRefinementOperator.class);
	
	private AbstractReasonerComponent reasoner;
	private Set<Description> refinements;
	
	public FakeRefinementOperator(AbstractReasonerComponent reasoner, Set<Description> refinements) {
		this.reasoner = reasoner;
		this.refinements = refinements;
	}
	
	@Override
	public void init() throws ComponentInitException {

	}

	@Override
	public Set<Description> refine(Description arg0) {
		log.info("Refining " + arg0 + " ..");
		if ("TOP".equals(arg0.toString()))
			return refinements;
		else
			return Sets.newHashSet();
	}

	@Override
	public Set<Description> refine(Description arg0, int arg1) {
		return refine(arg0);
	}

	@Override
	public Set<Description> refine(Description arg0, int arg1, List<Description> arg2) {
		return refine(arg0);
	}

}
