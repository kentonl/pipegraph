package edu.uw.cs.lil.pipegraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import edu.uw.cs.lil.pipegraph.util.GraphUtil;

public class GraphUtilTest {

	@Test(expected = IllegalArgumentException.class)
	public void testImpossibleTopologicalSort() {
		final Item i0 = new Item();
		final Item i1 = new Item(i0);
		i0.dependents.add(i1);
		GraphUtil.topologicalSort(Arrays.asList(i0, i1),
				i -> i.dependents.stream());
	}

	@Test
	public void testPossibleTopologicalSort() {
		final Item i0 = new Item();
		final Item i1 = new Item(i0);
		final Item i2 = new Item(i1);
		final Item i3 = new Item(i1);
		final Item i4 = new Item(i2, i3);
		final List<Item> sorted = new ArrayList<>(GraphUtil.topologicalSort(
				Arrays.asList(i0, i2, i4, i1, i3), i -> i.dependents.stream()));
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
