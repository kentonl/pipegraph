package edu.uw.pipegraph.web.renderer;

import com.google.protobuf.Message;

import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.stream.Stream;

import edu.uw.pipegraph.registry.IRegisterable;

public interface IResourceRenderer<T extends Message> extends IRegisterable {
	default Config getDefaultArguments() {
		return ConfigFactory.empty();
	}
	default boolean canRenderStream() { return false; }
	default Node renderStream(Stream<T> objects, Config arguments) { throw new UnsupportedOperationException(); }

	Node render(T object, Config arguments);
}
