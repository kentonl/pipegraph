package edu.uw.cs.lil.pipegraph.web.renderer;

import javax.servlet.http.HttpServletRequest;

import com.hp.gagawa.java.elements.Div;

import edu.uw.cs.lil.pipegraph.CommonProto.StringResource;

public class StringResourceRenderer
		implements IResourceRenderer<StringResource> {
	@Override
	public String getKey() {
		return StringResource.string.toString();
	}

	@Override
	public Div render(StringResource r, HttpServletRequest request) {
		return new Div().setCSSClass("well").appendText(r.getData());
	}
}
