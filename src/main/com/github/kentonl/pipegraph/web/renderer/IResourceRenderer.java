package com.github.kentonl.pipegraph.web.renderer;

import com.google.protobuf.Message;

import com.github.kentonl.pipegraph.registry.IRegisterable;
import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.stream.Stream;

public interface IResourceRenderer<T extends Message> extends IRegisterable {
	default Config getDefaultArguments() {
		return ConfigFactory.empty();
	}
	default boolean canRenderStream() { return false; }
	default Node renderStream(Stream<T> objects, Config arguments) { throw new UnsupportedOperationException(); }

	Node render(T object, Config arguments);
}
