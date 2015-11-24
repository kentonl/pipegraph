package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;
import com.typesafe.config.Config;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;

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
