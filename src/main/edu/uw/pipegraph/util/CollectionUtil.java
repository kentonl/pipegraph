package edu.uw.pipegraph.util;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;

import edu.uw.pipegraph.util.tuple.Pair;

public class CollectionUtil {
	private CollectionUtil() {
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

	public static <A, B> Stream<Pair<A, B>> zip(Stream<? extends A> a,
			Stream<? extends B> b) {
		return zip(a, b, Pair::of);
	}

	public static <A, B, C> Stream<C> zip(Stream<? extends A> a,
			Stream<? extends B> b,
			BiFunction<? super A, ? super B, ? extends C> zipper) {
		final Iterator<? extends A> iteratorA = a.iterator();
		final Iterator<? extends B> iteratorB = b.iterator();
		final Iterator<C> iteratorC = new Iterator<C>() {
			@Override
			public boolean hasNext() {
				return iteratorA.hasNext() && iteratorB.hasNext();
			}

			@Override
			public C next() {
				return zipper.apply(iteratorA.next(), iteratorB.next());
			}
		};
		final Iterable<C> iterableC = () -> iteratorC;
		return StreamSupport.stream(iterableC.spliterator(), false);
	}
}