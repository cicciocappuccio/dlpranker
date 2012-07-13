package com.neuralnoise.committee.scoring;

import java.util.Set;

import com.neuralnoise.example.AbstractInstance;
import com.neuralnoise.inference.AbstractInference;
import com.neuralnoise.search.AbstractModel;
import com.neuralnoise.search.AbstractScoring;

/**
 * 
 * @author pasquale
 *
 * Peng, H.C., Long, F., and Ding, C., "Feature selection based on mutual information: criteria of max-dependency,
 * max-relevance, and min-redundancy," IEEE Transactions on Pattern Analysis and Machine Intelligence, Vol. 27,
 * ,No. 8, pp. 1226–1238, 2005.
 */
public class MRMRScoring<I extends AbstractInstance> extends AbstractScoring<I> {

	private AbstractInference inference;
	private double lambda;
	
	public MRMRScoring(AbstractInference inference, double lambda,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		super(positives, negatives, neutrals);
		this.inference = inference;
		this.lambda = lambda;
	}
	
	public MRMRScoring(AbstractInference inference,
			Set<I> positives, Set<I> negatives, Set<I> neutrals) {
		this(inference, 1.0, positives, negatives, neutrals);
	}
	
	@Override
	public Double score(AbstractModel m) {
		RedundancyScoring<I> red = new RedundancyScoring<I>(inference, positives, negatives, neutrals);
		RelevanceScoring<I> rel = new RelevanceScoring<I>(inference, positives, negatives, neutrals);
		
		Double ret = rel.score(m) - (lambda * red.score(m));
		return ret;
	}
}
