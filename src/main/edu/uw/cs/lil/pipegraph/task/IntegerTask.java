package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

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