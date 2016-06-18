package com.github.kentonl.pipegraph.task;

import com.github.kentonl.pipegraph.core.Stage;

import java.util.stream.Stream;

import edu.uw.pipegraph.CommonProto.IntegerResource;

public class IntegerTask implements ITask<IntegerResource> {
	@Override
	public String getKey() {
		return "integer";
	}

	@Override
	public Stream<IntegerResource> run(Stage stage) {
		return Stream.of(IntegerResource.newBuilder()
				.setData(stage.getArguments().getInt("data")).build());
	}
}