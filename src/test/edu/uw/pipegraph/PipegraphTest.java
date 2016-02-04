package edu.uw.pipegraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.Test;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Pipegraph;
import edu.uw.pipegraph.runner.AsynchronousPipegraphRunner;
import edu.uw.pipegraph.runner.IPipegraphRunner;
import junit.framework.Assert;

public class PipegraphTest {
	@Test
	public void arithmeticTest() {
		try {
			final Pipegraph graph = new Pipegraph(
					Files.createTempDirectory("test").toFile(),
					new File("src/test/resources/test.conf"));
			final IPipegraphRunner runner = new AsynchronousPipegraphRunner(
					false);
			runner.run(graph, Optional.empty());
			Assert.assertEquals(36,
					graph.getStage("final").<IntegerResource> readOutput()
							.findFirst().get().getData());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
