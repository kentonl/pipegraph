package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

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