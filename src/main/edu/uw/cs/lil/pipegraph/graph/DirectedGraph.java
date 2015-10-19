package edu.uw.cs.lil.pipegraph.graph;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.uw.cs.lil.pipegraph.util.tuple.Pair;

public class DirectedGraph<N, E> {
	private final E[][]				adjacency;
	private final Map<N, Integer>	indexes;
	private final N[]				nodes;

	public DirectedGraph(DirectedGraph<N, E> other) {
		this.nodes = other.nodes.clone();
		this.indexes = new HashMap<>(other.indexes);
		this.adjacency = Arrays.stream(other.adjacency).map(row -> row.clone())
				.toArray(x -> other.adjacency.clone());
	}

	@SuppressWarnings("unchecked")
	public DirectedGraph(N[] nodes,
			Function<N, Collection<Pair<N, E>>> adjacencyMapper,
			Class<E> edgeClass) {
		this.nodes = nodes;
		indexes = new HashMap<>();
		for (int i = 0; i < nodes.length; i++) {
			indexes.put(nodes[i], i);
		}
		adjacency = (E[][]) Array.newInstance(edgeClass, nodes.length,
				nodes.length);
		for (int i = 0; i < nodes.length; i++) {
			for (final Pair<N, E> edge : adjacencyMapper.apply(nodes[i])) {
				adjacency[i][indexes.get(edge.first())] = edge.second();
			}
		}
	}

	public Stream<Pair<N, E>> edgeStream(N node) {
		final int i = indexes.get(node);
		return IntStream.range(0, nodes.length)
				.filter(j -> adjacency[i][j] != null)
				.mapToObj(j -> Pair.of(nodes[j], adjacency[i][j]));
	}

	public Stream<N> edgeStream(N node, E type) {
		final int j = indexes.get(node);
		return IntStream.range(0, nodes.length)
				.filter(i -> adjacency[i][j] == type).mapToObj(i -> nodes[i]);
	}

	public E getEdge(int from, int to) {
		return adjacency[from][to];
	}

	public N getNode(int i) {
		return nodes[i];
	}

	public N[] getNodes() {
		return nodes;
	}

	public Stream<N> nodeStream() {
		return Arrays.stream(nodes);
	}

	public void setEdge(int from, int to, E type) {
		adjacency[from][to] = type;
	}

	@Override
	public String toString() {
		final String nodeString = nodeStream().map(Object::toString)
				.collect(Collectors.joining(","));
		final String edgeString = Arrays.stream(adjacency)
				.map(row -> Arrays.stream(row).map(Objects::toString)
						.collect(Collectors.joining(",")))
				.collect(Collectors.joining("\n"));
		return Stream.of("Nodes:", nodeString, "Edges:", edgeString)
				.collect(Collectors.joining("\n"));
	}
}
