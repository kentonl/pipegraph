package edu.uw.cs.lil.pipegraph.task;

import java.util.Map;

import edu.uw.cs.lil.pipegraph.Common.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Pipe;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class IntegerTask implements ITask {

	@Override
	public String getType() {
		return "integer";
	}

	@Override
	public void run(Stage stage, Map<String, Pipe> inputs, Pipe output) {
		output.write(IntegerResource.integer, IntegerResource.newBuilder()
				.setData(stage.getArguments().getInt("data")).build());
	}
}