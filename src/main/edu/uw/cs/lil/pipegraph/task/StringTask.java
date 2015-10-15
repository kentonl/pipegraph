package edu.uw.cs.lil.pipegraph.task;

import edu.uw.cs.lil.pipegraph.CommonProto.StringResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class StringTask implements ITask<StringResource> {

	@Override
	public String getKey() {
		return "string";
	}

	@Override
	public StringResource run(Stage stage) {
		return StringResource.newBuilder()
				.setData(stage.getArguments().getString("data")).build();
	}
}