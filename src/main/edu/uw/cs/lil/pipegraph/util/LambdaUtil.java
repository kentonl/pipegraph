package edu.uw.cs.lil.pipegraph.util;

import java.util.function.Function;

public class LambdaUtil {

	private LambdaUtil() {
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

	public interface ThrowingFunction<T, R> {
		R apply(T t) throws Exception;
	}
}
