package edu.uw.pipegraph.runner;

import java.util.Optional;

import edu.uw.pipegraph.core.Pipegraph;

public interface IPipegraphRunner {
	public void run(Pipegraph graph, Optional<Integer> port);
}
