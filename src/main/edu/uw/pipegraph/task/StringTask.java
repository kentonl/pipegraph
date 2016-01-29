package edu.uw.pipegraph.task;

import edu.uw.pipegraph.CommonProto.StringResource;
import edu.uw.pipegraph.core.Stage;

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