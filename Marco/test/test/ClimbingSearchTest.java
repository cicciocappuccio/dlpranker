package test;

import it.uniba.di.lacam.fanizzi.features.psi.Psi2DownWrapper;
import it.uniba.di.lacam.fanizzi.features.selection.GreedyForward;
import it.uniba.di.lacam.fanizzi.features.selection.score.AbstractScore;
import it.uniba.di.lacam.fanizzi.features.selection.score.MHMRScore;
import it.uniba.di.lacam.fanizzi.utils.SerializeUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.RefinementOperator;

import com.google.common.collect.Sets;
import com.neuralnoise.cache.AbstractConceptCache;
import com.neuralnoise.cache.AsynchronousHibernateConceptCache;
import com.thoughtworks.xstream.XStream;



public class ClimbingSearchTest {

	public static void main(String[] args) throws Exception {

		String file = "res/fragmentOntology10.owl";

		int maxLength = 3;

		KnowledgeSource ks = new OWLFile(file);
		AbstractReasonerComponent reasoner = new OWLAPIReasoner(
				Collections.singleton(ks));
		reasoner.init();

		AbstractConceptCache cache = new AsynchronousHibernateConceptCache(file);

		Psi2DownWrapper r = new Psi2DownWrapper(reasoner);
		r.init();

		System.out.println("Reasoner creato");

		Description Film = new NamedClass("http://dbpedia.org/ontology/Film");
		Set<Individual> _films = reasoner.getIndividuals(Film);
		Set<Individual> films = Sets.newHashSet();
		int i = 0;
		Iterator<Individual> it = _films.iterator();
		while (it.hasNext() && i++ < 10)
			films.add(it.next());

		for (Individual y : films)
			System.out.println(y);

		System.out.println("Films selezionati\nchiamo GreedyForward");

		AbstractScore tScore = new MHMRScore(cache, reasoner, 0.1);

		GreedyForward pioniere = new GreedyForward(cache, reasoner, r,
				maxLength);

		Set<Description> risultato = pioniere.estrazione(Thing.instance, _films,
				tScore);

		for (Description j : risultato)
			System.out.println(j);

			
		
        XStream xs = new XStream();

        //Write to a file in the file system
        try {
            FileOutputStream fs = new FileOutputStream("res/provaxml.xml");
            xs.toXML(risultato, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        
        Set<Description> risultato2 = new HashSet<Description>();
        
        try {
            FileInputStream fis = new FileInputStream("res/provaxml.xml");
            xs.fromXML(fis, risultato2);

            //print the data from the object that has been read
            System.out.println(risultato2);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
		
	}
}
