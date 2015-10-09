package edu.uw.cs.lil.pipegraph.util;

import java.util.Comparator;
import java.util.function.Function;

public class ComparisonUtil {
	private ComparisonUtil() {
	}

	public static <A, B> Comparator<A> mappedComparator(Function<A, B> mapper,
			Comparator<B> comparator) {
		return (x, y) -> comparator.compare(mapper.apply(x), mapper.apply(y));
	}
}
