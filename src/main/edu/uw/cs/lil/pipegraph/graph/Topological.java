package edu.uw.cs.lil.pipegraph.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Topological {
	private Topological() {
	}

	// Any present edge is assumed to be a dependency.
	public static <N, E> List<N> sort(DirectedGraph<N, E> graph) {
		final LinkedList<N> sorted = new LinkedList<>();
		final Set<N> visited = new HashSet<>();
		final Stack<N> frontier = new Stack<>();
		while (sorted.size() < graph.getNodes().length) {
			visit(graph.nodeStream().filter(i -> !visited.contains(i))
					.findFirst().get(), visited, frontier, graph, sorted);
		}
		return sorted;
	}

	private static <N, E> void visit(N current, Set<N> visited,
			Stack<N> frontier, DirectedGraph<N, E> graph,
			LinkedList<N> sorted) {
		if (frontier.contains(current)) {
			final StringBuffer cycle = new StringBuffer(current + " <-- ");
			while (frontier.peek() != current) {
				cycle.append(frontier.pop() + " <-- ");
			}
			cycle.append(frontier.pop());
			throw new InvalidDAGException(
					"Cycles exist in dependencies: " + cycle);
		} else if (!visited.contains(current)) {
			frontier.push(current);
			graph.edgeStream(current).forEach(
					d -> visit(d.first(), visited, frontier, graph, sorted));
			if (frontier.pop() != current) {
				throw new RuntimeException("Frontier invariant violated.");
			}
			visited.add(current);
			sorted.addFirst(current);
		}
	}

	public static class InvalidDAGException extends RuntimeException {
		public InvalidDAGException(String message) {
			super(message);
		}
	}
}
