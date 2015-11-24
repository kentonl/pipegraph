package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface IResourceRenderer<T> extends IRegisterable {
	default Config getDefaultArguments() {
		return ConfigFactory.empty();
	}

	Node render(T object, Config arguments);
}
