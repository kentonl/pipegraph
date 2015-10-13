package edu.uw.cs.lil.pipegraph.web.renderer;

import javax.servlet.http.HttpServletRequest;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;

public class IntegerResourceRenderer
		implements IResourceRenderer<IntegerResource> {
	@Override
	public String getKey() {
		return IntegerResource.integer.toString();
	}

	@Override
	public Div render(IntegerResource r, HttpServletRequest request) {
		return new Div().setCSSClass("well")
				.appendText(Integer.toString(r.getData()));
	}
}
