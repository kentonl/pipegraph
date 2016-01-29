package edu.uw.pipegraph.util;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaUtil {

	private LambdaUtil() {
	}

	public static <A, B> Function<A, B> cachedFunction(Function<A, B> f,
			Stream<A> inputs) {
		final Map<A, B> cache = inputs.collect(Collectors.toMap(a -> a, f));
		return cache::get;
	}

	public static <A, B> Function<A, B> rethrow(ThrowingFunction<A, B> f) {
		return a -> {
			try {
				return f.apply(a);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static <A> Consumer<A> rethrowConsumer(ThrowingConsumer<A> f) {
		return a -> {
			try {
				f.consume(a);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public interface ThrowingConsumer<T> {
		void consume(T t) throws Exception;
	}

	public interface ThrowingFunction<T, R> {
		R apply(T t) throws Exception;
	}
}
