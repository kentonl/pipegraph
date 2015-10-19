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

	public static <T> DirectedGraph<T> reduction(DirectedGraph<T> graph) {
		final DirectedGraph<T> reducedGraph = closure(graph);
		for (int i = 0; i < reducedGraph.getNodes().length; i++) {
			reducedGraph.removeEdge(i, i);
		}
		for (int i = 0; i < reducedGraph.getNodes().length; i++) {
			for (int j = 0; j < reducedGraph.getNodes().length; j++) {
				if (reducedGraph.hasEdge(i, j)) {
					for (int k = 0; k < reducedGraph.getNodes().length; k++) {
						if (reducedGraph.hasEdge(j, k)) {
							reducedGraph.removeEdge(i, k);
						}
					}
				}
			}
		}
		return reducedGraph;
	}
}
