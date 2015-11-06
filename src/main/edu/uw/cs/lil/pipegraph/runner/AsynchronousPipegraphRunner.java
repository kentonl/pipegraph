package edu.uw.cs.lil.pipegraph.runner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.util.LambdaUtil;
import edu.uw.cs.lil.pipegraph.web.PipegraphServer;

public class AsynchronousPipegraphRunner implements IPipegraphRunner {
	public static final Logger	log	= LoggerFactory
			.getLogger(AsynchronousPipegraphRunner.class);

	private final boolean		runServer;

	public AsynchronousPipegraphRunner() {
		this(true);
	}

	public AsynchronousPipegraphRunner(boolean runServer) {
		this.runServer = runServer;
	}

	private static void run(Stage s, Pipegraph graph) {
		s.getInputs().values().stream().map(graph::getStage).forEach(dep -> {
			synchronized (dep) {
				while (!dep.isOutputReady()) {
					try {
						dep.wait();
					} catch (final Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
		log.debug("Running: {}", s);
		synchronized (s) {
			s.run(graph.getStages());
			s.notifyAll();
		}
	}

	@Override
	public void run(final Pipegraph graph, final Optional<Integer> port) {
		final PipegraphServer server = new PipegraphServer(graph, port);
		if (runServer) {
			try {
				server.start();
				log.info("View pipegraph at {}", server.getURL());
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}

		final List<Thread> stageThreads = graph.getStages().values().stream()
				.map(s -> new Thread(() -> run(s, graph)))
				.collect(Collectors.toList());
		stageThreads.forEach(LambdaUtil.rethrowConsumer(Thread::start));
		stageThreads.forEach(LambdaUtil.rethrowConsumer(Thread::join));

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
