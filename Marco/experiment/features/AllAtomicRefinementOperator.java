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

public class AllAtomicRefinementOperator implements RefinementOperator {

	private static final Logger log = LoggerFactory.getLogger(AllAtomicRefinementOperator.class);
	
	private AbstractReasonerComponent reasoner;
	
	public AllAtomicRefinementOperator(AbstractReasonerComponent reasoner) {
		this.reasoner = reasoner;
	}
	
	@Override
	public void init() throws ComponentInitException {

	}

	@Override
	public Set<Description> refine(Description arg0) {
		log.info("Refining " + arg0 + " ..");
		List<NamedClass> atomics = reasoner.getAtomicConceptsList();
		Set<Description> ret = Sets.newHashSet();
		for (NamedClass atomic : atomics)
			ret.add(atomic);
		return ret;
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
