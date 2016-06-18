package com.github.kentonl.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;
import com.typesafe.config.Config;

import edu.uw.pipegraph.CommonProto.IntegerResource;

public class IntegerResourceRenderer
		implements IResourceRenderer<IntegerResource> {

	@Override
	public String getKey() {
		return IntegerResource.class.toString();
	}

	@Override
	public Div render(IntegerResource r, Config arguments) {
		return new Div().setCSSClass("well")
				.appendText(Integer.toString(r.getData()));
	}
}
