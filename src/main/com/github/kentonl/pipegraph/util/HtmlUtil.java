package com.github.kentonl.pipegraph.util;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Dd;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Dl;
import com.hp.gagawa.java.elements.Dt;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Link;
import com.hp.gagawa.java.elements.Script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HtmlUtil {
	public static final Logger log = LoggerFactory.getLogger(HtmlUtil.class);

	private static final List<String> cssFiles = Arrays
			.asList("//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css",
					"//cdnjs.cloudflare.com/ajax/libs/c3/0.4.11/c3.min.css");
	private static final List<String> jsFiles = Arrays
			.asList("//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js",
					"//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js",
					"//cdnjs.cloudflare.com/ajax/libs/c3/0.4.11/c3.min.js",
					"//cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js");

	private HtmlUtil() {
	}

	public static Html createPage(Node content) {
		final Html html = new Html();
		final Head head = new Head();
		html.appendChild(head);
		jsFiles.stream().map(f -> new Script("text/javascript").setSrc(f)).forEach(head::appendChild);
		html.appendChild(enablePopovers());

		cssFiles.stream().map(f -> new Link().setRel("stylesheet").setHref(f)).forEach(head::appendChild);
		final Body body = new Body();
		html.appendChild(body);

		final Div nav = new Div().setCSSClass("navbar navbar-inverse navbar-static-top");
		body.appendChild(nav);

		nav.appendChild(new Div().setCSSClass("container-fluid").appendChild(new Div().setCSSClass("navbar-header")
				.appendChild(new A("/").setCSSClass("navbar-brand").appendText("Overview"))));
		final Div container = new Div().setCSSClass("container-fluid");
		body.appendChild(container);
		container.appendChild(content);
		return html;
	}

	public static Dl mapToDescriptionList(Map<String, Node> m) {
		final Dl descriptionList = new Dl();
		for (final Map.Entry<String, Node> entry : m.entrySet()) {
			descriptionList.appendChild(new Dt().appendText(entry.getKey()));
			descriptionList.appendChild(new Dd().appendChild(entry.getValue()));
		}
		return descriptionList;
	}

	public static Script enablePopovers() {
		return new Script("text/javascript").appendText("$(document).ready(function(){$('[data-toggle=\"popover\"]').popover();})");
	}
}
