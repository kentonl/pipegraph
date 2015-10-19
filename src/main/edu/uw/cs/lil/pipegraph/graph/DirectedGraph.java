package edu.uw.cs.lil.pipegraph.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DirectedGraph<T> {
	private final boolean[][]		adjacency;
	private final Map<T, Integer>	indexes;
	private final T[]				nodes;

	public DirectedGraph(DirectedGraph<T> other) {
		this.nodes = other.nodes.clone();
		this.indexes = new HashMap<>(other.indexes);
		this.adjacency = Arrays.stream(other.adjacency).map(row -> row.clone())
				.toArray(x -> other.adjacency.clone());
	}

	public DirectedGraph(T[] nodes, Function<T, Collection<T>> dependents) {
		this.nodes = nodes;
		indexes = new HashMap<>();
		for (int i = 0; i < nodes.length; i++) {
			indexes.put(nodes[i], i);
		}
		adjacency = new boolean[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (final T dependent : dependents.apply(nodes[i])) {
				adjacency[i][indexes.get(dependent)] = true;
			}
		}
	}

	public void addEdge(int from, int to) {
		adjacency[from][to] = true;
	}

	public Stream<T> dependencyStream(T node) {
		final int j = indexes.get(node);
		return IntStream.range(0, nodes.length).filter(i -> adjacency[i][j])
				.mapToObj(i -> nodes[j]);
	}

	public Stream<T> dependentStream(T node) {
		final int i = indexes.get(node);
		return IntStream.range(0, nodes.length).filter(j -> adjacency[i][j])
				.mapToObj(j -> nodes[j]);
	}

	public T[] getNodes() {
		return nodes;
	}

	public boolean hasEdge(int from, int to) {
		return adjacency[from][to];
	}

	public Stream<T> nodeStream() {
		return Arrays.stream(nodes);
	}
}
