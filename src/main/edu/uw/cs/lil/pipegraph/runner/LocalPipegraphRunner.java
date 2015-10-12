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

public class LocalPipegraphRunner implements IPipegraphRunner {
	public static final Logger log = LoggerFactory
			.getLogger(LocalPipegraphRunner.class);

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
		for (final Stage s : sortedStages) {
			log.info("Running: {}", s);
			graph.runStage(s);
		}
		log.info("Finished running pipegraph.");
	}
}
