package edu.uw.pipegraph.task;

import java.util.stream.Stream;

import edu.uw.pipegraph.CommonProto.StringResource;
import edu.uw.pipegraph.core.Stage;

public class StringTask implements ITask<StringResource> {

	@Override
	public String getKey() {
		return "string";
	}

	@Override
	public Stream<StringResource> run(Stage stage) {
		return Stream.of(StringResource.newBuilder()
				.setData(stage.getArguments().getString("data")).build());
	}
}