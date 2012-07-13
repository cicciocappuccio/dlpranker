package com.neuralnoise.committee.scoring;

import java.util.Set;

import com.neuralnoise.committee.Committee;
import com.neuralnoise.example.AbstractInstance;
import com.neuralnoise.feature.AbstractFeatureContent;
import com.neuralnoise.inference.AbstractInference;
import com.neuralnoise.search.AbstractModel;
import com.neuralnoise.search.AbstractScoring;

public class RelevanceScoring<I extends AbstractInstance> extends AbstractScoring<I> {

	private AbstractInference inference;
	
	public RelevanceScoring(AbstractInference inference,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		super(positives, negatives, neutrals);
		this.inference = inference;
	}

	@Override
	public Double score(AbstractModel m) {
		if (!(m instanceof Committee)) {
			throw new IllegalStateException("The model should be an instance of Committee");
		}
		
		Committee c = (Committee) m;
		Set<AbstractFeatureContent> components = c.getComponents();
		double sS = components.size();
		
		InformationTheoryUtils<I> itu = new InformationTheoryUtils<I>(this.inference);
		double sum = 0.0;
		
		for (AbstractFeatureContent X : components) {
			sum += itu.I(X, positives, negatives);
		}
		
		//double ret = (sS > 0.0 ? sum / sS : Double.NEGATIVE_INFINITY);
		
		double ret = sum;
		
		return ret;
	}

}
