package edu.uw.cs.lil.pipegraph.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.core.Stage;

public class LogTask implements ITask {
	public static final Logger log = LoggerFactory.getLogger(LogTask.class);

	@Override
	public String getKey() {
		return "log";
	}

	@Override
	public void run(Stage stage) {
		log.info("{}", stage.read("data"));
	}
}