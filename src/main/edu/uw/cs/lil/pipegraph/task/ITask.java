package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.Message;

import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface ITask extends IRegisterable {
	Class<? extends Message> getOutputClass();

	void run(Stage stage);
}