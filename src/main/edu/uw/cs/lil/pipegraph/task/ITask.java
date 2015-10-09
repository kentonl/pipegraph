package edu.uw.cs.lil.pipegraph.task;

import java.util.Map;

import edu.uw.cs.lil.pipegraph.core.Pipe;
import edu.uw.cs.lil.pipegraph.core.Stage;

public interface ITask {
	String getType();

	void run(Stage stage, Map<String, Pipe> inputs, Pipe output);
}