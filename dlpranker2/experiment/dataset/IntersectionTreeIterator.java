package dataset;

import java.util.LinkedList;
import java.util.Queue;

import org.jgrapht.Graph;
import org.jgrapht.traverse.CrossComponentIterator;

public class IntersectionTreeIterator<V, E> extends CrossComponentIterator<V, E, Object> {

	private Queue<V> queue;

	public IntersectionTreeIterator(Graph<V, E> g) {
		this(g, null);
	}

	public IntersectionTreeIterator(Graph<V, E> g, V startVertex) {
		super(g, startVertex);
		this.queue = new LinkedList<V>();
	}

	protected boolean isConnectedComponentExhausted() {
		return queue.isEmpty();
	}

	protected void encounterVertex(V vertex, E edge) {
		//putSeenData(vertex, null);
		queue.add(vertex);
	}

	protected void encounterVertexAgain(V vertex, E edge) { }

	protected V provideNextVertex() {
		return queue.poll();
	}
}
