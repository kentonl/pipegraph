package com.github.kentonl.pipegraph.util;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaUtil {

	private LambdaUtil() {
	}

	public static <A> Function<Object, A> toFunction(Supplier<A> s) {
		return a -> s.get();
	}

	public static <A> Function<A, A> toFunction(Consumer<A> c) {
		return a -> {
			c.accept(a);
			return a;
		};
	}

	public static Consumer<Object> toConsumer(SideEffect se) {
		return a -> se.perform();
	}

	public static Supplier<Object> toSupplier(SideEffect se) {
		return () -> {
			se.perform();
			return null;
		};
	}

	public static <A> Function<A, A> toFunction(SideEffect se) {
		return a -> {
			se.perform();
			return a;
		};
	}

	public static <A, B> Function<A, B> cachedFunction(Function<A, B> f, Stream<A> inputs) {
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

	public static <A> Supplier<A> rethrowSupplier(ThrowingSupplier<A> f) {
		return () -> {
			try {
				return f.get();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static <A> Consumer<A> noOp() {
		return a -> {
		};
	}

	public interface ThrowingConsumer<T> {
		void consume(T t) throws Exception;
	}

	public interface ThrowingFunction<T, R> {
		R apply(T t) throws Exception;
	}

	public interface ThrowingSupplier<R> {
		R get() throws Exception;
	}

	public interface SideEffect {
		void perform();
	}
}
