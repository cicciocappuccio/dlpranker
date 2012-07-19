package refinement;


import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.neuralnoise.cache.ReasonerUtils;

public class CachedPsi extends PsiWrapper {

	private Table<String, Integer, Set<Description>> cache;
	
	public CachedPsi(AbstractReasonerComponent reasoner) {
		super(reasoner);
		this.cache = HashBasedTable.create();
	}

	@Override
	public Set<Description> refine(Description _concept, int maxLength) {
		Set<Description> ret = null;
		Description concept = ReasonerUtils.normalise(_concept);
		if (cache.contains(concept, maxLength)) {
			ret = cache.get(concept.toString(), maxLength);
		} else {
			ret = super.refine(_concept, maxLength);
			cache.put(concept.toString(), maxLength, ret);
		}
		
		return ret;
	}
	
}
