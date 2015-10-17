package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;

import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface IResourceRenderer<T> extends IRegisterable {
	Config getDefaultArguments();

	Node render(T object, Config arguments);
}
