package edu.uw.pipegraph.task;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Stage;

public class MultiplyTask implements ITask<IntegerResource> {

	@Override
	public String getKey() {
		return "multiply";
	}

	@Override
	public IntegerResource run(Stage stage) {
		return IntegerResource.newBuilder()
				.setData(stage.read("x", IntegerResource.class).getData()
						* stage.read("y", IntegerResource.class).getData())
				.build();
	}
}