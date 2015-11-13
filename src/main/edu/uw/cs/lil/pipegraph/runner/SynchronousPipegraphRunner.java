package edu.uw.cs.lil.pipegraph.runner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.graph.DirectedGraph;
import edu.uw.cs.lil.pipegraph.graph.Topological;
import edu.uw.cs.lil.pipegraph.util.EnumUtil.Unit;
import edu.uw.cs.lil.pipegraph.util.MapUtil;
import edu.uw.cs.lil.pipegraph.util.tuple.Pair;
import edu.uw.cs.lil.pipegraph.web.PipegraphServer;

public class SynchronousPipegraphRunner implements IPipegraphRunner {
	public static final Logger	log	= LoggerFactory
			.getLogger(SynchronousPipegraphRunner.class);

	private final boolean		runServer;

	public SynchronousPipegraphRunner() {
		this(true);
	}

	public SynchronousPipegraphRunner(boolean runServer) {
		this.runServer = runServer;
	}

	@Override
	public void run(Pipegraph graph, Optional<Integer> port) {
		final PipegraphServer server = new PipegraphServer(graph, port);
		if (runServer) {
			try {
				server.start();
				log.info("View pipegraph at {}", server.getURL());
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		final Map<Stage, List<Pair<Stage, Unit>>> edges = MapUtil.mapToMap(
				graph.getStages(), graph::getStage,
				s -> graph.getStages().values().stream()
						.filter(d -> d.getInputs().values().stream()
								.map(graph::getStage).anyMatch(s::equals))
						.map(d -> Pair.of(d, Unit.unit))
						.collect(Collectors.toList()));
		final DirectedGraph<Stage, Unit> stageGraph = new DirectedGraph<>(
				graph.getStages().values().toArray(new Stage[0]),
				s -> edges.get(s), Unit.class);
		final List<Stage> sortedStages = Topological.sort(stageGraph);

		for (final Stage s : sortedStages) {
			log.debug("Running: {}", s);
			s.run(graph.getStages());
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