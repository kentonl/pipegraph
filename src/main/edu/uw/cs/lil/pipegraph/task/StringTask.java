package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.Message;

import edu.uw.cs.lil.pipegraph.CommonProto.StringResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class StringTask implements ITask {

	@Override
	public String getKey() {
		return "string";
	}

	@Override
	public Class<? extends Message> getOutputClass() {
		return StringResource.class;
	}

	@Override
	public void run(Stage stage) {
		stage.write(StringResource.newBuilder()
				.setData(stage.getArguments().getString("data")).build());
	}
}