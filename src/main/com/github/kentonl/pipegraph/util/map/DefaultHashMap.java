package com.github.kentonl.pipegraph.util.map;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultHashMap<K, V> extends HashMap<K, V> {
	final private Function<K, V> defaultMapper;

	public DefaultHashMap(Supplier<V> defaultSupplier) {
		this.defaultMapper = k -> defaultSupplier.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return computeIfAbsent((K) key, defaultMapper);
	}
}
