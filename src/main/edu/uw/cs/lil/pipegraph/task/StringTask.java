package edu.uw.cs.lil.pipegraph.task;

import java.util.Map;

import edu.uw.cs.lil.pipegraph.Common.StringResource;
import edu.uw.cs.lil.pipegraph.core.Pipe;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class StringTask implements ITask {

	@Override
	public String getType() {
		return "string";
	}

	@Override
	public void run(Stage stage, Map<String, Pipe> inputs, Pipe output) {
		output.write(StringResource.string, StringResource.newBuilder()
				.setData(stage.getArguments().getString("data")).build());
	}
}