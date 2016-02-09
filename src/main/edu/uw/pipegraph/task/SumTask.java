package edu.uw.pipegraph.task;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Stage;

public class SumTask implements ITask<IntegerResource> {

	public static final Logger log = LoggerFactory.getLogger(SumTask.class);

	@Override
	public String getKey() {
		return "sum";
	}

	@Override
	public Stream<IntegerResource> run(Stage stage) {
		final int x = stage.read("x", IntegerResource.class).findFirst().get()
				.getData();
		final int y = stage.read("y", IntegerResource.class).findFirst().get()
				.getData();
		log.debug("{} + {} = {}", new Integer[] { x, y, x + y });
		return Stream.of(IntegerResource.newBuilder().setData(x + y).build());
	}
}