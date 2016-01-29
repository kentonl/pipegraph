package edu.uw.pipegraph.util;

import java.util.Optional;

public class TypeUtil {
	private TypeUtil() {

	}

	public static <T> Optional<T> maybeCast(Object obj, Class<T> clazz) {
		if (clazz.isInstance(obj)) {
			return Optional.of(clazz.cast(obj));
		} else {
			return Optional.empty();
		}
	}
}
