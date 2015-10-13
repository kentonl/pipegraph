package edu.uw.cs.lil.pipegraph.util.map;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultMap<K, V> extends HashMap<K, V> {
	final private Function<K, V> defaultMapper;

	public DefaultMap(Function<K, V> defaultMapper) {
		this.defaultMapper = defaultMapper;
	}

	public DefaultMap(Supplier<V> defaultSupplier) {
		this.defaultMapper = k -> defaultSupplier.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return computeIfAbsent((K) key, defaultMapper);
	}
}
