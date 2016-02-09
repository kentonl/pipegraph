package edu.uw.pipegraph.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
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

	public static <A, B, C> Iterator<C> zip(Stream<? extends A> a,
			Stream<? extends B> b,
			BiFunction<? super A, ? super B, ? extends C> zipper) {
		Objects.requireNonNull(zipper);
		@SuppressWarnings("unchecked")
		final Spliterator<A> aSpliterator = (Spliterator<A>) Objects
				.requireNonNull(a).spliterator();
		@SuppressWarnings("unchecked")
		final Spliterator<B> bSpliterator = (Spliterator<B>) Objects
				.requireNonNull(b).spliterator();

		final Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
		final Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
		return new Iterator<C>() {
			@Override
			public boolean hasNext() {
				return aIterator.hasNext() && bIterator.hasNext();
			}

			@Override
			public C next() {
				return zipper.apply(aIterator.next(), bIterator.next());
			}
		};
	}

	public static <A, B> Stream<Pair<A, B>> zipStream(Stream<? extends A> a,
			Stream<? extends B> b) {
		return zipStream(a, b, Pair::of);
	}

	public static <A, B, C> Stream<C> zipStream(Stream<? extends A> a,
			Stream<? extends B> b,
			BiFunction<? super A, ? super B, ? extends C> zipper) {

		final Iterator<C> cIterator = zip(a, b, zipper);

		final int both = a.spliterator().characteristics()
				& b.spliterator().characteristics()
				& ~(Spliterator.DISTINCT | Spliterator.SORTED);
		final int characteristics = both;

		final long zipSize = (characteristics & Spliterator.SIZED) != 0
				? Math.min(a.spliterator().getExactSizeIfKnown(),
						b.spliterator().getExactSizeIfKnown())
				: -1;

		final Spliterator<C> split = Spliterators.spliterator(cIterator,
				zipSize, characteristics);
		return a.isParallel() || b.isParallel()
				? StreamSupport.stream(split, true)
				: StreamSupport.stream(split, false);
	}
}
