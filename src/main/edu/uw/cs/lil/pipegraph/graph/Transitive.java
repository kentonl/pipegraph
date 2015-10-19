package edu.uw.cs.lil.pipegraph.graph;

public class Transitive {
	private Transitive() {
	}

	public static <T> DirectedGraph<T> closure(DirectedGraph<T> graph) {
		final DirectedGraph<T> closedGraph = new DirectedGraph<>(graph);
		for (int k = 0; k < graph.getNodes().length; k++) {
			for (int j = 0; j < graph.getNodes().length; j++) {
				for (int i = 0; i < graph.getNodes().length; i++) {
					if (closedGraph.hasEdge(i, k)
							&& closedGraph.hasEdge(k, j)) {
						closedGraph.addEdge(i, j);
					}
				}
			}
		}
		return closedGraph;
	}
}
