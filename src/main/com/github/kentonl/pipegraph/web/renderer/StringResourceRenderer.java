package com.github.kentonl.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;
import com.typesafe.config.Config;

import edu.uw.pipegraph.CommonProto.StringResource;

public class StringResourceRenderer
		implements IResourceRenderer<StringResource> {

	@Override
	public String getKey() {
		return StringResource.class.toString();
	}

	@Override
	public Div render(StringResource r, Config arguments) {
		return new Div().setCSSClass("well").appendText(r.getData());
	}
}
