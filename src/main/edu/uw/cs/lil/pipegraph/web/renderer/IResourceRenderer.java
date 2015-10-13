package edu.uw.cs.lil.pipegraph.web.renderer;

import javax.servlet.http.HttpServletRequest;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface IResourceRenderer<T> extends IRegisterable {
	Div render(T object, HttpServletRequest request);
}
