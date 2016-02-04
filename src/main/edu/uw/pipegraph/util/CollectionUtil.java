package edu.uw.pipegraph.util;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;

import edu.uw.pipegraph.util.tuple.Pair;

public class CollectionUtil {
	private CollectionUtil() {
	}

	public static <A, B> Iterable<Pair<A, B>> zip(Iterable<A> a,
			Iterable<B> b) {
		return () -> new ZippedIterator<>(a.iterator(), b.iterator());
	}

	public static <A, B, C> Iterable<Pair<B, C>> zip(Map<A, B> a, Map<A, C> b) {
		Preconditions.checkArgument(a.size() == b.size(),
				"Maps have different size.");
		a.forEach((k, v) -> Preconditions.checkArgument(b.containsKey(k),
				"Missing entry for: " + k));
		return a.entrySet().stream()
				.map(entry -> Pair.of(entry.getValue(), b.get(entry.getKey())))
				.collect(Collectors.toList());
	}

	public static <A, B> Stream<Pair<A, B>> zipStream(Iterable<A> a,
			Iterable<B> b) {
		return StreamSupport.stream(zip(a, b).spliterator(), false);
	}

	private static class ZippedIterator<A, B> implements Iterator<Pair<A, B>> {
		private final Iterator<A>	a;
		private final Iterator<B>	b;

		public ZippedIterator(Iterator<A> a, Iterator<B> b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean hasNext() {
			return a.hasNext() && b.hasNext();
		}

		@Override
		public Pair<A, B> next() {
			return Pair.of(a.next(), b.next());
		}
	}
}
