package edu.uw.cs.lil.pipegraph;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import edu.uw.cs.lil.pipegraph.Common.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.runner.IPipegraphRunner;
import edu.uw.cs.lil.pipegraph.runner.LocalPipegraphRunner;
import edu.uw.cs.lil.pipegraph.task.repo.CommonTaskRepository;
import edu.uw.cs.lil.pipegraph.task.repo.TaskRepository;
import junit.framework.Assert;

public class PipegraphTest {
	@Test
	public void arithmeticTest() {
		final TaskRepository taskRepo = new CommonTaskRepository();
		try {
			final Pipegraph graph = new Pipegraph(
					Files.createTempDirectory("test").toFile(), "test.conf",
					taskRepo);
			final IPipegraphRunner runner = new LocalPipegraphRunner();
			runner.run(graph);
			Assert.assertEquals(36, graph.createPipe("final")
					.read(IntegerResource.integer).getData());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
