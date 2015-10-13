package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.CommonProto.Resource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class SumTask implements ITask {

	@Override
	public String getKey() {
		return "sum";
	}

	@Override
	public GeneratedExtension<Resource, ?> getOutputExtension() {
		return IntegerResource.integer;
	}

	@Override
	public void run(Stage stage) {
		stage.write(IntegerResource.newBuilder()
				.setData(stage.read("x", IntegerResource.integer).getData()
						+ stage.read("y", IntegerResource.integer).getData())
				.build());
	}
}