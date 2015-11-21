package edu.uw.cs.lil.pipegraph.util;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Link;
import com.hp.gagawa.java.elements.Script;

public class HtmlUtil {
	public static final Logger			log			= LoggerFactory
			.getLogger(HtmlUtil.class);

	private static final List<String>	cssFiles	= Arrays.asList(
			"//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css");
	private static final List<String>	jsFiles		= Arrays.asList(
			"//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js",
			"//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js");

	private HtmlUtil() {
	}

	public static Html createPage(Node content) {
		final Html html = new Html();
		final Head head = new Head();
		html.appendChild(head);
		jsFiles.stream().map(f -> new Script("text/javascript").setSrc(f))
				.forEach(head::appendChild);

		cssFiles.stream().map(f -> new Link().setRel("stylesheet").setHref(f))
				.forEach(head::appendChild);
		final Body body = new Body();
		html.appendChild(body);

		final Div nav = new Div()
				.setCSSClass("navbar navbar-default navbar-static-top");
		body.appendChild(nav);

		nav.appendChild(new Div().setCSSClass("container-fluid")
				.appendChild(new Div().setCSSClass("navbar-header")
						.appendChild(new A("/").setCSSClass("navbar-brand")
								.appendText("Home"))));
		final Div container = new Div().setCSSClass("container-fluid");
		body.appendChild(container);
		container.appendChild(content);
		return html;
	}
}
