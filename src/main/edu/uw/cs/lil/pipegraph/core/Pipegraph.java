package edu.uw.cs.lil.pipegraph.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.uw.cs.lil.pipegraph.task.repo.TaskRepository;
import edu.uw.cs.lil.pipegraph.util.MapUtil;

public class Pipegraph {
	public static final Logger			log	= LoggerFactory
			.getLogger(Pipegraph.class);

	private final Config				config;
	private final File					directory;
	private final Map<String, Stage>	stages;
	private final TaskRepository		taskRepo;

	public Pipegraph(File root, String filename, TaskRepository taskRepo) {
		this.taskRepo = taskRepo;
		this.config = ConfigFactory.parseResourcesAnySyntax(filename);
		this.stages = new HashMap<>();
		if (!filename.endsWith(".conf")) {
			throw new IllegalArgumentException(
					"Invalid extension for " + filename);
		}
		this.directory = new File(root, config.hasPath("root")
				? config.getString("root")
				: filename.substring(0, filename.length() - ".conf".length()));
		config.getStringList("goals").forEach(this::populateStagesFor);
		log.info("Stages:{}", stages.values());
	}

	public Pipe createPipe(String name) {
		return new Pipe(directory, name);
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
					config.getObject(target).toConfig());
			stages.put(target, s);
			s.getInputs().values().forEach(this::populateStagesFor);
		}
	}

	public void runStage(Stage s) {
		taskRepo.create(s.getType()).run(s,
				MapUtil.mapToMap(s.getInputs(), k -> k, this::createPipe),
				createPipe(s.getName()));
	}
}
