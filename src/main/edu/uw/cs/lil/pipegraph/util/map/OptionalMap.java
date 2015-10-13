package edu.uw.cs.lil.pipegraph.util.map;

import java.util.HashMap;
import java.util.Optional;

public class OptionalMap<K, V> extends HashMap<K, V> {
	public Optional<V> getOptional(Object key) {
		return Optional.ofNullable(get(key));
	}
}
