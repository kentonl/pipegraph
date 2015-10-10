package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface ITask extends IRegisterable {
	void run(Stage stage);
}