package edu.uw.pipegraph.task;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Stage;

public class IntegerTask implements ITask<IntegerResource> {
	@Override
	public String getKey() {
		return "integer";
	}

	@Override
	public IntegerResource run(Stage stage) {
		return IntegerResource.newBuilder()
				.setData(stage.getArguments().getInt("data")).build();
	}
}