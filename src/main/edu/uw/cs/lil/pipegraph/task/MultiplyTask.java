package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.Message;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class MultiplyTask implements ITask {

	@Override
	public String getKey() {
		return "multiply";
	}

	@Override
	public Class<? extends Message> getOutputClass() {
		return IntegerResource.class;
	}

	@Override
	public void run(Stage stage) {
		stage.write(IntegerResource.newBuilder()
				.setData(stage.read("x", IntegerResource.class).getData()
						* stage.read("y", IntegerResource.class).getData())
				.build());
	}
}