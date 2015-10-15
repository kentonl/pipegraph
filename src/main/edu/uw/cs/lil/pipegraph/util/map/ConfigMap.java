package edu.uw.cs.lil.pipegraph.util.map;

import java.util.Optional;
import java.util.function.Function;

public class ConfigMap<T> {
	private final Function<T, Optional<String>> m;

	private ConfigMap(Function<T, Optional<String>> m) {
		this.m = m;
	}

	public static <T> ConfigMap<T> of(Function<T, Optional<String>> m) {
		return new ConfigMap<>(m);
	}

	public Optional<Boolean> getBoolean(T key) {
		return m.apply(key).map(Boolean::parseBoolean);
	}

	public Optional<Double> getDouble(T key) {
		try {
			return m.apply(key).map(Double::parseDouble);
		} catch (final NumberFormatException e) {
			return Optional.empty();
		}
	}

	public Optional<Integer> getInt(T key) {
		try {
			return m.apply(key).map(Integer::parseInt);
		} catch (final NumberFormatException e) {
			return Optional.empty();
		}
	}

	public Optional<String> getString(T key) {
		return m.apply(key);
	}
}
