package edu.uw.cs.lil.pipegraph;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.runner.IPipegraphRunner;
import edu.uw.cs.lil.pipegraph.runner.LocalPipegraphRunner;
import junit.framework.Assert;

public class PipegraphTest {
	@Test
	public void arithmeticTest() {
		try {
			final Pipegraph graph = new Pipegraph(
					Files.createTempDirectory("test").toFile(), "test.conf");
			final IPipegraphRunner runner = new LocalPipegraphRunner();
			runner.run(graph);
			Assert.assertEquals(36, graph.getStage("final")
					.readOutput(IntegerResource.integer).getData());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
