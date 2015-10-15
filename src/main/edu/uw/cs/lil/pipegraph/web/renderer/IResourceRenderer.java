package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.registry.IRegisterable;
import edu.uw.cs.lil.pipegraph.util.map.ConfigMap;

public interface IResourceRenderer<T> extends IRegisterable {
	Div render(T object, ConfigMap<String> arguments);
}
