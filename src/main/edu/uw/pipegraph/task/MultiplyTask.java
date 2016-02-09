package edu.uw.pipegraph.task;

import java.util.stream.Stream;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Stage;

public class MultiplyTask implements ITask<IntegerResource> {

	@Override
	public String getKey() {
		return "multiply";
	}

	@Override
	public Stream<IntegerResource> run(Stage stage) {
		return Stream.of(IntegerResource.newBuilder()
				.setData(stage.read("x", IntegerResource.class).findFirst()
						.get().getData()
						* stage.read("y", IntegerResource.class).findFirst()
								.get().getData())
				.build());
	}
}