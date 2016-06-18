package com.github.kentonl.pipegraph.runner;

import java.util.Optional;

import com.github.kentonl.pipegraph.core.Pipegraph;

public interface IPipegraphRunner {
	void run(Pipegraph graph, Optional<Integer> port);
}
