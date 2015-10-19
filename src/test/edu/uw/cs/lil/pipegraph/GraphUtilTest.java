package edu.uw.cs.lil.pipegraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import edu.uw.cs.lil.pipegraph.graph.DirectedGraph;
import edu.uw.cs.lil.pipegraph.graph.Topological;
import edu.uw.cs.lil.pipegraph.graph.Topological.InvalidDAGException;

public class GraphUtilTest {

	@Test(expected = InvalidDAGException.class)
	public void testImpossibleTopologicalSort() {
		final Item[] items = new Item[2];
		items[0] = new Item();
		items[1] = new Item(items[0]);
		items[0].dependents.add(items[1]);
		final DirectedGraph<Item> testGraph = new DirectedGraph<>(items,
				i -> i.dependents);
		Topological.sort(testGraph);
	}

	@Test
	public void testPossibleTopologicalSort() {
		final Item[] items = new Item[5];
		items[0] = new Item();
		items[1] = new Item(items[0]);
		items[2] = new Item(items[1]);
		items[3] = new Item(items[1]);
		items[4] = new Item(items[2], items[3]);
		final DirectedGraph<Item> testGraph = new DirectedGraph<>(items,
				i -> i.dependents);
		final List<Item> sorted = new ArrayList<>(
				Topological.sort(testGraph));
		for (int i = 0; i < sorted.size(); i++) {
			for (int j = i + 1; j < sorted.size(); j++) {
				Assert.assertFalse(
						sorted.get(j).dependents.contains(sorted.get(i)));
			}
		}
	}

	private static class Item {
		public List<Item> dependents;

		public Item(Item... dependents) {
			this.dependents = Arrays.stream(dependents)
					.collect(Collectors.toList());
		}
	}
}
