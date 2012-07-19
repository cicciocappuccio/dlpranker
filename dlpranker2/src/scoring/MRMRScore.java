package scoring;


import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;

import utils.EIUtils;
import utils.Inference;

import com.google.common.collect.Multimap;


public class MRMRScore extends AbstractScore {

	public enum ModeType { MID, MIQ };
	private ModeType type;
	
	private Inference inference;
	private Multimap<Integer, Individual> h;
	
	private double lambda;
	private EIUtils calc;
	
	public MRMRScore(Inference inference, Multimap<Integer, Individual> h, double lambda, EIUtils calc) {
		this(inference, h, lambda, calc, ModeType.MID);
	}
	
	public MRMRScore(Inference inference, Multimap<Integer, Individual> h, double lambda, EIUtils calc, ModeType type) {
		super(inference);
		this.inference = inference;
		this.h = h;
		
		this.lambda = lambda;
		this.calc = calc;
		
		this.type = type;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public Double score(Set<Description> descriptions, Set<Individual> individuals) {
		RedundancyScore mis = new RedundancyScore(inference, calc);
		RelevanceScore rel = new RelevanceScore(inference, h, calc);
		
		Double ret = rel.score(descriptions, individuals) - (lambda * mis.score(descriptions, individuals));
		return ret;
	}

	@Override
	public Double score(Description current, Set<Description> descriptions, Set<Individual> individuals) {
		RedundancyScore mis = new RedundancyScore(inference, calc);
		RelevanceScore rel = new RelevanceScore(inference, h, calc);
		
		double _rel = rel.score(current, descriptions, individuals);
		double _red = mis.score(current, descriptions, individuals);
		
		Double ret = null;
		switch (this.type) {
		case MID: {
			ret = _rel - (lambda * _red);
		} break;
		case MIQ: {
			ret = _rel / (lambda * _red);
		} break;
		}
		
		return ret;
	}
}
