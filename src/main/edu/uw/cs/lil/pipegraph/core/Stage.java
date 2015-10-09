package edu.uw.cs.lil.pipegraph.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class Stage {
	private final Config				arguments;
	private final Map<String, String>	inputs;
	private final String				name;
	private final String				type;

	public Stage(String name, Config config) {
		this.name = name;
		this.type = config.getString("type");
		this.arguments = config.hasPath("args")
				? config.getObject("args").toConfig() : ConfigFactory.empty();
		final Config inputConfig = config.hasPath("inputs")
				? config.getConfig("inputs") : ConfigFactory.empty();
		this.inputs = inputConfig.entrySet().stream().collect(
				Collectors.<Entry<String, ConfigValue>, String, String> toMap(
						entry -> entry.getKey(),
						entry -> inputConfig.getString(entry.getKey())));
	}

	public Config getArguments() {
		return arguments;
	}

	public Map<String, String> getInputs() {
		return inputs;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getName(),
				inputs.entrySet().stream()
						.map(entry -> String.format("%s=%s", entry.getKey(),
								entry.getValue()))
				.collect(Collectors.joining(",")));
	}
}
