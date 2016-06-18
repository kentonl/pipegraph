package com.github.kentonl.pipegraph.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValueFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {

	public static ConfigBuilder builder() {
		return new ConfigBuilder();
	}

	public static String encodeURL(Config arguments) {
		return "?" + arguments.entrySet().stream()
				.map(LambdaUtil.rethrow(entry -> String.format("%s=%s",
						URLEncoder.encode(entry.getKey(), "UTF-8"),
						URLEncoder.encode(
								entry.getValue().unwrapped().toString(),
								"UTF-8"))))
				.collect(Collectors.joining("&"));
	}

	public static class ConfigBuilder {
		private final Map<String, Object> values;

		public ConfigBuilder() {
			values = new HashMap<>();
		}

		public ConfigBuilder add(String key, Object value) {
			values.put(key, value);
			return this;
		}

		public Config build() {
			return ConfigFactory.parseMap(values);
		}
	}

	public static ConfigListBuilder listBuilder() {
		return new ConfigListBuilder();
	}

	public static class ConfigListBuilder {
		private final List<Object> values;

		public ConfigListBuilder() {
			values = new ArrayList<>();
		}

		public ConfigListBuilder add(Object value) {
			values.add(value);
			return this;
		}

		public ConfigList build() {
			return ConfigValueFactory.fromIterable(values);
		}
	}
}
