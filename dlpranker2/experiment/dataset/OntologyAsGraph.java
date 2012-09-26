package dataset;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectProperty;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.neuralnoise.cache.ReasonerUtils;
import utils.Inference;

public class OntologyAsGraph {

	private static final Logger log = LoggerFactory.getLogger(OntologyAsGraph.class);
	
	private static final String TYPE = "rdfs:type";
	private static final String SOURCE = "source";
	
	private DirectedMultigraph<String, RoleEdge> graph;
	private final ITreeNode root = new ITreeNode(SOURCE, 0);
	
	public OntologyAsGraph(DirectedMultigraph<String, RoleEdge> graph) {
		this.graph = graph;
	}
	
	public OntologyAsGraph(Inference inference) {
		log.info("Creating MultiGraph..");
		
		AbstractReasonerComponent reasoner = inference.getReasoner();
		Set<Individual> individuals = reasoner.getIndividuals();
		final int N = individuals.size();

		List<NamedClass> atomicConcepts = reasoner.getAtomicConceptsList();
		//Set<ObjectProperty> objectProperties = reasoner.getObjectProperties();
		SortedSet<DatatypeProperty> datatypeProperties = reasoner.getDatatypeProperties();

		this.graph = new DirectedMultigraph<String, RoleEdge>(RoleEdge.class);

		for (Individual ind : individuals) {
			this.graph.addVertex(ind.toString());
		}
		
		for (NamedClass concept : atomicConcepts) {
			this.graph.addVertex(concept.toString());
		}
		
		// use hierarchy between classes ? better not to (not really a hierarchy in DL i think..)
		
		int c = 0;
		
		for (Individual ind : individuals) {
			String sind = ind.toString();
			
			System.out.print((++c) + "/" + N + ".. \r");
			
			for (NamedClass concept : atomicConcepts) {
				String sconcept = concept.toString();
				if (reasoner.hasType(concept, ind)) {
					this.graph.addEdge(sind, sconcept, new RoleEdge(sind, sconcept, TYPE));
				}
			}

			Map<ObjectProperty, Set<Individual>> ops = reasoner.getObjectPropertyRelationships(ind);
			for (Entry<ObjectProperty, Set<Individual>> e : ops.entrySet()) {
				ObjectProperty op = e.getKey();
				String sop = op.toString();
				Set<Individual> objs = e.getValue();
				for (Individual obj : objs) {
					String sobj = obj.toString();
					this.graph.addEdge(sind, sobj, new RoleEdge(sind, sobj, sop));
				}
			}
		}
		
		System.out.println();
		
		for (DatatypeProperty dp : datatypeProperties) {
			String sdp = dp.toString();
			Map<Individual, SortedSet<String>> map = reasoner.getStringDatatypeMembers(dp);
			for (Entry<Individual, SortedSet<String>> entry : map.entrySet()) {
				String sind = entry.getKey().toString();
				for (String sobj : entry.getValue()) {
					if (!this.graph.containsVertex(sobj)) {
						this.graph.addVertex(sobj);
					}
					this.graph.addEdge(sind, sobj, new RoleEdge(sind, sobj, sdp));
				}
			}
		}
	}
	
	public DirectedMultigraph<ITreeNode, RoleEdge> intersectionTree(String e1, String e2, int d) {		
		DirectedMultigraph<ITreeNode, RoleEdge> tree = new DirectedMultigraph<ITreeNode, RoleEdge>(RoleEdge.class);	
		tree.addVertex(root);

		Queue<ITreeNode> queue = Lists.newLinkedList();
		queue.add(root);

		while (!queue.isEmpty()) {
			ITreeNode leaf = queue.poll();
			int lev = leaf.getLevel();

			if (lev < d) {
				Set<RoleEdge> ce = Sets.newHashSet();

				if (leaf.getLabel().equals(SOURCE)) {
					Set<RoleEdge> out1 = this.graph.outgoingEdgesOf(e1);
					Set<RoleEdge> out2 = this.graph.outgoingEdgesOf(e2);
					
					// add to ce common neighbors of e1 and e2
					
					for (RoleEdge o1 : out1) {
						boolean add = false;
						String t1 = o1.getTarget();
						for (RoleEdge o2 : out2) {
							String t2 = o2.getTarget();
							// if same neigh. and same role, add to common neighs.
							if (t1.equals(t2) && o1.getLabel().equals(o2.getLabel())) {
								add = true;
							}
						}
						if (add) {
							ce.add(o1);
						}
					}
					
					for (RoleEdge o1 : out1) {
						boolean toAdd = false;
						for (RoleEdge o2 : out2) {
							if (o1.getTarget().equals(e2) && o2.getTarget().equals(e1) && o1.getLabel().equals(o2.getLabel())) {
								toAdd = true;
							}
						}
						if (toAdd) {
							ce.add(o1);
						}
					}
					
				} else {
					Set<RoleEdge> out = this.graph.outgoingEdgesOf(leaf.getLabel());
					ce.addAll(out);
				}

				// actually walks in the graph can also differentiate only for predicates ..
				//Set<String> targets = Sets.newHashSet();
				
				for (RoleEdge c : ce) {
					String p = c.getLabel();
					String target = c.getTarget();
					String label = (target.equals(e1) || target.equals(e2) ? SOURCE : target);
					
					ITreeNode child = new ITreeNode(label, lev + 1);
					tree.addVertex(child);
					tree.addEdge(leaf, child, new RoleEdge(leaf.getLabel(), child.getLabel(), p));
					
					queue.add(child);
				}
			}
		}
		
		return tree;
	}
	
	public double st(DirectedMultigraph<ITreeNode, RoleEdge> itree, double lambda) {
		return st(itree, root, lambda);
	}
	
	private double st(DirectedMultigraph<ITreeNode, RoleEdge> itree, ITreeNode node, double lambda) {
		double ret = 1.0;
		double sum = 0.0;
		for (RoleEdge out : itree.outgoingEdgesOf(node)) {
			ITreeNode child = itree.getEdgeTarget(out);
			sum += st(itree, child, lambda);
		}
		sum *= lambda;
		ret += sum;
		return ret;
	}
	
	
	public static void main(String[] args) throws Exception {
		AbstractReasonerComponent reasoner = ReasonerUtils.getReasoner(new File(args[0]));
		Inference inference = new Inference(null, reasoner);
		OntologyAsGraph o = new OntologyAsGraph(inference);
		DirectedMultigraph<String, RoleEdge> graph = o.graph;

		/*
		DirectedMultigraph<String, RoleEdge> graph = new DirectedMultigraph<String, RoleEdge>(RoleEdge.class);
		OntologyAsGraph o = new OntologyAsGraph(graph);
		graph.addVertex("p1");
		graph.addVertex("p2");
		graph.addEdge("p1", "p2", new RoleEdge("p1", "p2", "knows"));
		graph.addVertex("a");
		graph.addEdge("p2", "a", new RoleEdge("p2", "a", "has"));
		*/
		
		for (String e1 : graph.vertexSet()) {
			for (String e2 : graph.vertexSet()) {
				DirectedMultigraph<ITreeNode, RoleEdge> itree = o.intersectionTree(e1, e2, 2);
				System.out.println(e1 + ", " + e2 + " : " + o.st(itree, 1.0));
				//System.out.println(e1 + ", " + e2 + " ..");
				//System.out.println("  -> " + itree);
			}
		}
	}

}
