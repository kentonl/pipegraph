package edu.uw.cs.lil.pipegraph.task;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import edu.uw.cs.lil.pipegraph.CommonProto.Resource;
import edu.uw.cs.lil.pipegraph.CommonProto.StringResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class StringTask implements ITask {

	@Override
	public String getKey() {
		return "string";
	}

	@Override
	public GeneratedExtension<Resource, ?> getOutputExtension() {
		return StringResource.string;
	}

	@Override
	public void run(Stage stage) {
		stage.write(StringResource.newBuilder()
				.setData(stage.getArguments().getString("data")).build());
	}
}