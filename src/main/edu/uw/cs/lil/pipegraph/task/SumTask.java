package edu.uw.cs.lil.pipegraph.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class SumTask implements ITask<IntegerResource> {

	public static final Logger log = LoggerFactory.getLogger(SumTask.class);

	@Override
	public String getKey() {
		return "sum";
	}

	@Override
	public IntegerResource run(Stage stage) {
		final int x = stage.read("x", IntegerResource.class).getData();
		final int y = stage.read("y", IntegerResource.class).getData();
		log.debug("{} + {} = {}", new Integer[] { x, y, x + y });
		return IntegerResource.newBuilder().setData(x + y).build();
	}
}