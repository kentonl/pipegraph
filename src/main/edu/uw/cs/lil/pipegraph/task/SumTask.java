package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class SumTask implements ITask {

	@Override
	public String getKey() {
		return "sum";
	}

	@Override
	public void run(Stage stage) {
		stage.write(IntegerResource.integer, IntegerResource.newBuilder()
				.setData(stage.read("x", IntegerResource.integer).getData()
						+ stage.read("y", IntegerResource.integer).getData())
				.build());
	}
}