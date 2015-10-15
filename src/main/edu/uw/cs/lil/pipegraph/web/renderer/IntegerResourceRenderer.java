package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.CommonProto.IntegerResource;
import edu.uw.cs.lil.pipegraph.util.map.ConfigMap;

public class IntegerResourceRenderer
		implements IResourceRenderer<IntegerResource> {
	@Override
	public String getKey() {
		return IntegerResource.class.toString();
	}

	@Override
	public Div render(IntegerResource r, ConfigMap<String> arguments) {
		return new Div().setCSSClass("well")
				.appendText(Integer.toString(r.getData()));
	}
}
