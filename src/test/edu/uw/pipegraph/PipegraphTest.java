package edu.uw.pipegraph;

import junit.framework.Assert;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import edu.uw.pipegraph.CommonProto.IntegerResource;
import edu.uw.pipegraph.core.Pipegraph;
import edu.uw.pipegraph.runner.AsynchronousPipegraphRunner;
import edu.uw.pipegraph.runner.IPipegraphRunner;

public class PipegraphTest {
	@Test
	public void arithmeticTest() {
		doTest(new File("src/test/resources/test.conf"), "final");
	}

	@Test
	public void includeTest() {
		doTest(new File("src/test/resources/test_include.conf"), "test.final");
	}

	@Test
	public void nestedIncludeTest() {
		doTest(new File("src/test/resources/test_nested_include.conf"), "test_include.test.final");
	}

	private void doTest(final File configFile, final String goal) {
		try {
			final Pipegraph graph = new Pipegraph(
                    Files.createTempDirectory("test").toFile(),
					configFile,
                    Optional.empty());
			final IPipegraphRunner runner = new AsynchronousPipegraphRunner(
					false);
			runner.run(graph, Optional.empty());
			Assert.assertEquals(36,
					graph.getStage(goal).<IntegerResource> readOutput()
							.findFirst().get().getData());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
