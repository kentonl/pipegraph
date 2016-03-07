package edu.uw.pipegraph.util;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import edu.uw.pipegraph.util.tuple.Pair;

public class CollectionUtil {
    private CollectionUtil() {
    }

    public static <R> R getUpToIth(Stream<R> stream, int i) {
        return stream.limit(i + 1).reduce((a, b) -> b)
                .orElseThrow(() -> new RuntimeException("Empty stream"));
    }

    public static <R> Stream<R> streamWhile(Supplier<R> supplier,
                                            Predicate<R> condition) {
        return streamWhile(supplier, condition, false);
    }

    public static <R> Stream<R> streamWhile(Supplier<R> supplier,
                                            Predicate<R> condition,
                                            boolean isParallel) {
        return toStream(new Iterator<R>() {
            R next = supplier.get();

            @Override
            public boolean hasNext() {
                return condition.test(next);
            }

            @Override
            public R next() {
                final R result = next;
                next = supplier.get();
                return result;
            }
        }, isParallel);
    }

    public static <T> Stream<T> toStream(Iterator<T> iterator, boolean isParallel) {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), isParallel);
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
        return toStream(new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(iteratorA.next(), iteratorB.next());
            }
        }, a.isParallel() || b.isParallel());
    }

    public static <A, C> Stream<C> enumerate(Stream<? extends A> a,
                                             BiFunction<? super A, Integer, ? extends C> zipper) {
        return zip(a, IntStream.iterate(0, x -> x + 1).mapToObj(x -> x), zipper);
    }
}