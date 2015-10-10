package edu.uw.cs.lil.pipegraph.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import edu.uw.cs.lil.pipegraph.util.LambdaUtil;

public class Registry {
	private final Reflections																		reflections;
	private final Map<Class<? extends IRegisterable>, Map<String, Class<? extends IRegisterable>>>	registry;

	public Registry(Reflections reflections) {
		this.reflections = reflections;
		this.registry = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <R extends IRegisterable> R create(Class<R> registerableClass,
			String key) {
		try {
			return (R) registry
					.computeIfAbsent(registerableClass, this::createClassMap)
					.get(key).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Class<? extends IRegisterable>> createClassMap(
			Class<? extends IRegisterable> c) {
		return reflections.getSubTypesOf(c).stream()
				.collect(
						Collectors.toMap(
								LambdaUtil.rethrow(subtype -> subtype
										.newInstance().getKey()),
				subtype -> subtype));
	}
}
