package it.uniba.di.lacam.fanizzi.features.utils;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;

import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.ReasonerUtils;

public class Inference {

	public enum LogicValue { TRUE, FALSE, UNKNOWN };
	private LogicValue value;

	private AbstractConceptCache cache;
	private AbstractReasonerComponent reasoner;
	
	
	
	public Inference(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner) {
		super();
		this.cache = cache;
		this.reasoner = reasoner;
	}

	public Inference(LogicValue value)
	{
		this.value = value;
	}
	
	public LogicValue getValue()
	{
		return value;
	}
	
	public void setValue(LogicValue value)
	{
		this.value = value;
	}
	
	public LogicValue cover(Description concept, Individual i) {
		Description normalised = ReasonerUtils.normalise(concept);
		Description negatedNormalised = ReasonerUtils.normalise(new Negation(concept));
		LogicValue ret = LogicValue.UNKNOWN;
		boolean get = false, nget = false;
		
		if (cache.contains(normalised, i) && cache.contains(negatedNormalised, i)) {
			get = cache.get(normalised, i);
			nget = cache.get(negatedNormalised, i);
		} else {
			get = reasoner.hasType(normalised, i);
			nget = reasoner.hasType(negatedNormalised, i);
			cache.addElement(normalised, i, get);
			cache.addElement(negatedNormalised, i, nget);
		}

		if (get)
			ret = LogicValue.TRUE;
		if (nget)
			ret = LogicValue.FALSE;
		
		return ret; 
	}
	
}