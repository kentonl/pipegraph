package com.github.kentonl.pipegraph.task;

import com.github.kentonl.pipegraph.core.Stage;

import java.util.stream.Stream;

import edu.uw.pipegraph.CommonProto.StringResource;

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