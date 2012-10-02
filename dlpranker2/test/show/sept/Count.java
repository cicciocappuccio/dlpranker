package show.sept;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectProperty;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dataset.ExperimentDataset;
import dataset.OntologyAsGraph;
import dataset.Tupla;
import scripts.AbstractRankExperiment;
import utils.Inference;
import utils.XMLFilmRatingStream;

public class Count extends AbstractRankExperiment {

	public static Set<Description> getSubClasses(Inference inference, NamedClass C) {
		Set<Description> ret = Sets.newHashSet();
		Set<String> seen = Sets.newHashSet();
		Queue<Description> queue = new LinkedList<Description>();
		queue.add(C);
		//queue.add(new NamedClass("http://dbpedia.org/ontology/Film"));
		//queue.add(new NamedClass("http://dbpedia.org/class/yago/Movie106613686"));
		//queue.add(new NamedClass("http://schema.org/Movie"));
		while (!queue.isEmpty()) {
			Description p = queue.poll();
			if (!seen.contains(p.toString())) {
				ret.add(p);
				seen.add(p.toString());
				queue.addAll(inference.getReasoner().getSubClasses(p));
			}
		}
		System.out.println("Film subclasses: " + ret.size());
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		Inference inference = getInference();

		/*
		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> _utenti = ExperimentDataset.getUsers(lista);
		List<Tupla> utenti = Lists.newArrayList();
		
		for (Tupla u : _utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, u.getUser());
			System.out.print(ratingsUser.size() + " ");
		}
		System.out.println();
		*/
		
		/*
		String A = "http://dbpedia.org/ontology/Film";
		String B = "http://dbpedia.org/class/yago/Movie106613686";
		String C = "http://schema.org/Movie";

		NamedClass a = new NamedClass(A);
		NamedClass b = new NamedClass(B);
		NamedClass c = new NamedClass(C);
		
		System.out.println(getSubClasses(inference, a).size());
		System.out.println(getSubClasses(inference, b).size());
		System.out.println(getSubClasses(inference, c).size());
		*/
		
		AbstractReasonerComponent reasoner = inference.getReasoner();
		List<NamedClass> atomics = reasoner.getAtomicConceptsList();
		Set<ObjectProperty> objectProperties = reasoner.getObjectProperties();	
		SortedSet<DatatypeProperty> dataProperties = reasoner.getDatatypeProperties();
		
		System.out.println("Number of atomic concepts: " + atomics.size());
		System.out.println("Number of object properties: " + objectProperties.size());
		
		for (ObjectProperty op : objectProperties) {
			System.out.println("\t" + op);
		}
		
		System.out.println("Number of data properties: " + dataProperties.size());
	}

}
