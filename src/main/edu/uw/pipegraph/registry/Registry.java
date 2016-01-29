package edu.uw.pipegraph.registry;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import edu.uw.pipegraph.util.LambdaUtil;

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
			final Map<String, Class<? extends IRegisterable>> classMap = registry
					.computeIfAbsent(registerableClass, this::createClassMap);
			if (!classMap.containsKey(key)) {
				throw new IllegalArgumentException(key
						+ " not found in the registry of " + registerableClass);
			}
			return (R) classMap.get(key).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <R extends IRegisterable> boolean has(Class<R> registerableClass,
			String key) {
		return registry.computeIfAbsent(registerableClass, this::createClassMap)
				.containsKey(key);
	}

	private Map<String, Class<? extends IRegisterable>> createClassMap(
			Class<? extends IRegisterable> c) {
		return reflections.getSubTypesOf(c).stream()
				.filter(subtype -> !Modifier.isAbstract(subtype.getModifiers()))
				.collect(
						Collectors.toMap(
								LambdaUtil.rethrow(subtype -> subtype
										.newInstance().getKey()),
						subtype -> subtype));
	}
}
