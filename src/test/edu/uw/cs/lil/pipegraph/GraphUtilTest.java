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
import edu.uw.cs.lil.pipegraph.util.EnumUtil.Unit;
import edu.uw.cs.lil.pipegraph.util.tuple.Pair;

public class GraphUtilTest {

	@Test(expected = InvalidDAGException.class)
	public void testImpossibleTopologicalSort() {
		final Item[] items = new Item[2];
		items[0] = new Item();
		items[1] = new Item(items[0]);
		items[0].addDependent(items[1]);
		final DirectedGraph<Item, Unit> testGraph = new DirectedGraph<>(items,
				i -> i.dependents, Unit.class);
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
		final DirectedGraph<Item, Unit> testGraph = new DirectedGraph<>(items,
				i -> i.dependents, Unit.class);
		final List<Item> sorted = new ArrayList<>(Topological.sort(testGraph));
		for (int i = 0; i < sorted.size(); i++) {
			for (int j = i + 1; j < sorted.size(); j++) {
				Assert.assertFalse(
						sorted.get(j).dependents.contains(sorted.get(i)));
			}
		}
	}

	private static class Item {
		public List<Pair<Item, Unit>> dependents;

		public Item(Item... dependents) {
			this.dependents = Arrays.stream(dependents)
					.map(d -> Pair.of(d, Unit.unit))
					.collect(Collectors.toList());
		}

		public void addDependent(Item d) {
			dependents.add(Pair.of(d, Unit.unit));
		}
	}
}
