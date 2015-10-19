package edu.uw.cs.lil.pipegraph.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Topological {
	private Topological() {
	}

	public static <T> List<T> sort(DirectedGraph<T> graph) {
		final LinkedList<T> sorted = new LinkedList<>();
		final Set<T> visited = new HashSet<>();
		final Stack<T> frontier = new Stack<>();
		while (sorted.size() < graph.getNodes().length) {
			visit(graph.nodeStream().filter(i -> !visited.contains(i))
					.findFirst().get(), visited, frontier, graph, sorted);
		}
		return sorted;
	}

	private static <T> void visit(T current, Set<T> visited, Stack<T> frontier,
			DirectedGraph<T> graph, LinkedList<T> sorted) {
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
			graph.dependentStream(current)
					.forEach(d -> visit(d, visited, frontier, graph, sorted));
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
