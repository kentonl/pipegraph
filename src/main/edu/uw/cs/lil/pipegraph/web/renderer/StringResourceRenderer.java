package edu.uw.cs.lil.pipegraph.web.renderer;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.CommonProto.StringResource;
import edu.uw.cs.lil.pipegraph.util.map.ConfigMap;

public class StringResourceRenderer
		implements IResourceRenderer<StringResource> {
	@Override
	public String getKey() {
		return StringResource.class.toString();
	}

	@Override
	public Div render(StringResource r, ConfigMap<String> arguments) {
		return new Div().setCSSClass("well").appendText(r.getData());
	}
}
