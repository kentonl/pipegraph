package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import edu.uw.cs.lil.pipegraph.CommonProto.Resource;
import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface ITask extends IRegisterable {
	GeneratedExtension<Resource, ?> getOutputExtension();

	void run(Stage stage);
}