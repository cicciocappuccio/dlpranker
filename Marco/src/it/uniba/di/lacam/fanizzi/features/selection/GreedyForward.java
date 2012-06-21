package it.uniba.di.lacam.fanizzi.features.selection;

import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.refinementoperators.RefinementOperator;

import com.neuralnoise.cache.AbstractConceptCache;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GreedyForward {

	protected AbstractConceptCache cache;
	protected AbstractReasonerComponent reasoner;
	private RefinementOperator r;
	private int maxLength;

	public GreedyForward(AbstractConceptCache cache,
			AbstractReasonerComponent reasoner, RefinementOperator r,
			int maxLength) {
		super();
		this.cache = cache;
		this.reasoner = reasoner;
		this.r = r;
		this.maxLength = maxLength;
	}

	public Set<Description> estrazione(Description rootConcept,
			Set<Individual> individuals, AbstractScore tScore) {

		ClimbingSearch scalatore = new ClimbingSearch(r, maxLength, tScore);

		Set<Description> conceptSet = new HashSet<Description>();

		double score = 0.0, previousScore;

		System.out.println("Inizia ciclo");

		Description candidate;
		do {
			previousScore = score;
			candidate = scalatore.extraction(rootConcept, conceptSet,
					individuals);
			conceptSet.add(candidate);
			
			score = tScore.score(conceptSet, individuals);
			System.out.println("estrazione: " + candidate + " with score: "
					+ score + " previousScore: " + previousScore);

		} while (score > previousScore);

		return conceptSet;
	}

	
}
