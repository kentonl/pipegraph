package edu.uw.cs.lil.pipegraph.runner;

import java.util.Optional;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;

public interface IPipegraphRunner {
	public void run(Pipegraph graph, Optional<Integer> port);
}
