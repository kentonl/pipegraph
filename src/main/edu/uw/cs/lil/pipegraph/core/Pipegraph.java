package edu.uw.cs.lil.pipegraph.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Pipegraph {
	public static final Logger			log	= LoggerFactory
			.getLogger(Pipegraph.class);

	private final Config				config;
	private final Context				context;
	private final Map<String, Stage>	stages;

	public Pipegraph(File root, File configFile) {
		if (!configFile.exists()) {
			throw new IllegalArgumentException(configFile + " not found.");
		}
		this.context = new Context(root, configFile);
		this.config = ConfigFactory.parseFileAnySyntax(configFile).resolve();
		this.stages = new HashMap<>();

		config.getStringList("goals").forEach(this::populateStagesFor);

		log.info("Stages:{}", stages.values());
	}

	public Context getContext() {
		return context;
	}

	public Stage getStage(String name) {
		return stages.get(name);
	}

	public Map<String, Stage> getStages() {
		return stages;
	}

	public void populateStagesFor(String target) {
		if (!stages.containsKey(target)) {
			final Stage s = new Stage(target,
					config.getObject(target).toConfig(), context);
			stages.put(target, s);
			s.getInputs().values().forEach(this::populateStagesFor);
		}
	}
}
