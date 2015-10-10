package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class IntegerTask implements ITask {

	@Override
	public String getKey() {
		return "integer";
	}

	@Override
	public void run(Stage stage) {
		stage.write(IntegerResource.integer, IntegerResource.newBuilder()
				.setData(stage.getArguments().getInt("data")).build());
	}
}