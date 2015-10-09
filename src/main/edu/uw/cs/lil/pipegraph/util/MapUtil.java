package edu.uw.cs.lil.pipegraph.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {
	private MapUtil() {
	}

	public static <K1, V1, K2, V2> Map<K2, V2> mapToMap(Map<K1, V1> m,
			Function<K1, K2> keyMapper, Function<V1, V2> valueMapper) {
		return m.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> keyMapper.apply(entry.getKey()),
						entry -> valueMapper.apply(entry.getValue())));
	}
}
