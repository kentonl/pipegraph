package edu.uw.cs.lil.pipegraph.runner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.util.GraphUtil;
import edu.uw.cs.lil.pipegraph.util.MapUtil;
import edu.uw.cs.lil.pipegraph.web.PipegraphServer;

public class LocalPipegraphRunner implements IPipegraphRunner {
	public static final Logger	log	= LoggerFactory
			.getLogger(LocalPipegraphRunner.class);

	private final boolean		runServer;

	public LocalPipegraphRunner() {
		this(true);
	}

	public LocalPipegraphRunner(boolean runServer) {
		this.runServer = runServer;
	}

	@Override
	public void run(Pipegraph graph) {
		final Map<Stage, List<Stage>> dependents = MapUtil.mapToMap(
				graph.getStages(), graph::getStage,
				s -> graph.getStages().values().stream()
						.filter(d -> d.getInputs().values().stream()
								.map(graph::getStage).anyMatch(s::equals))
						.collect(Collectors.toList()));
		final List<Stage> sortedStages = GraphUtil.topologicalSort(
				graph.getStages().values(), s -> dependents.get(s).stream());

		final PipegraphServer server = new PipegraphServer(graph);
		if (runServer) {
			try {
				server.start();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		for (final Stage s : sortedStages) {
			log.info("Running: {}", s);
			s.setStatus(Stage.Status.RUNNING);
			graph.runStage(s);
			s.setStatus(Stage.Status.COMPLETED);
		}
		log.info("Finished running pipegraph.");
		if (runServer) {
			try {
				server.join();
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
