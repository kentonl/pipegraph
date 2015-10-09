package edu.uw.cs.lil.pipegraph.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class GraphUtil {
	private GraphUtil() {
	}

	public static <T> List<T> topologicalSort(Collection<T> items,
			Function<T, Stream<T>> dependents) {
		final LinkedList<T> sorted = new LinkedList<>();
		final Set<T> visited = new HashSet<>();
		final Set<T> frontier = new HashSet<>();
		while (sorted.size() < items.size()) {
			visit(items.stream().filter(i -> !visited.contains(i)).findFirst()
					.get(), visited, frontier, dependents, sorted);
		}
		return sorted;
	}

	private static <T> void visit(T current, Set<T> visited, Set<T> frontier,
			Function<T, Stream<T>> dependents, LinkedList<T> sorted) {
		if (frontier.contains(current)) {
			throw new IllegalArgumentException(
					"Cycles exist in dependencies: (see " + current + ")");
		} else if (!visited.contains(current)) {
			frontier.add(current);
			dependents.apply(current).forEach(
					d -> visit(d, visited, frontier, dependents, sorted));
			frontier.remove(current);
			visited.add(current);
			sorted.addFirst(current);
		}
	}
}
