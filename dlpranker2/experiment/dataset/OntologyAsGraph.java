package dataset;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectProperty;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scripts.AbstractRankExperiment;
import utils.EIUtils;
import utils.Inference;

public class OntologyAsGraph {

	private static final Logger log = LoggerFactory.getLogger(OntologyAsGraph.class);
	
	private static final String TYPE = "rdfs:type";
	
	private DirectedMultigraph<String, String> graph;
	
	public OntologyAsGraph(Inference inference) {
		log.info("Creating Graph..");
		
		AbstractReasonerComponent reasoner = inference.getReasoner();
		Set<Individual> individuals = reasoner.getIndividuals();
		final int N = individuals.size();

		List<NamedClass> atomicConcepts = reasoner.getAtomicConceptsList();
		Set<ObjectProperty> objectProperties = reasoner.getObjectProperties();
		SortedSet<DatatypeProperty> datatypeProperties = reasoner.getDatatypeProperties();

		this.graph = new DirectedMultigraph<String, String>(String.class);

		for (Individual ind : individuals) {
			this.graph.addVertex(ind.toString());
		}
		
		for (NamedClass concept : atomicConcepts) {
			this.graph.addVertex(concept.toString());
		}
		
		// use hierarchy between classes ? better not to (not really a hierarchy in DL i think..)
		
		for (Individual ind : individuals) {

			for (NamedClass concept : atomicConcepts) {
				if (reasoner.hasType(concept, ind)) {
					this.graph.addEdge(ind.toString(), concept.toString(), "rdfs:type");
				}
			}

			Map<ObjectProperty, Set<Individual>> ops = reasoner.getObjectPropertyRelationships(ind);
			for (Entry<ObjectProperty, Set<Individual>> e : ops.entrySet()) {
				ObjectProperty op = e.getKey();
				Set<Individual> objs = e.getValue();
				for (Individual obj : objs) {
					this.graph.addEdge(ind.toString(), obj.toString(), op.toString());
				}
			}
		}
		
		for (DatatypeProperty dp : datatypeProperties) {
			Map<Individual, SortedSet<String>> map = reasoner.getStringDatatypeMembers(dp);
			for (Entry<Individual, SortedSet<String>> entry : map.entrySet()) {
				String sind = entry.getKey().toString();
				for (String sobj : entry.getValue()) {
					if (!this.graph.containsVertex(sobj)) {
						this.graph.addVertex(sobj);
					}
					this.graph.addEdge(sind, sobj, dp.toString());
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Inference inference = AbstractRankExperiment.getInference();
		OntologyAsGraph o = new OntologyAsGraph(inference);
		
		System.out.println(o.graph);
	}

}
