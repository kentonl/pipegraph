package edu.uw.cs.lil.pipegraph.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Link;
import com.hp.gagawa.java.elements.Pre;
import com.hp.gagawa.java.elements.Script;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import com.hp.gagawa.java.elements.Ul;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.util.map.ConfigMap;
import edu.uw.cs.lil.pipegraph.web.renderer.IResourceRenderer;

public class StageHandler extends AbstractHandler {
	public static final Logger			log			= LoggerFactory
			.getLogger(StageHandler.class);

	private static final List<String>	cssFiles	= Arrays.asList(
			"//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css");
	private static final List<String>	jsFiles		= Arrays.asList(
			"//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js",
			"//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js");
	private final Pipegraph				pipegraph;

	public StageHandler(Pipegraph pipegraph) {
		this.pipegraph = pipegraph;
	}

	private static Html createPage(Div page) {
		final Html html = new Html();
		final Head head = new Head();
		html.appendChild(head);
		jsFiles.stream().map(f -> new Script("text/javascript").setSrc(f))
				.forEach(head::appendChild);

		cssFiles.stream().map(f -> new Link().setRel("stylesheet").setHref(f))
				.forEach(head::appendChild);
		final Body body = new Body();
		html.appendChild(body);

		final Div container = new Div().setCSSClass("container");
		body.appendChild(container);
		container.appendChild(page);
		return html;
	}

	private static Span renderStageReference(Stage stage) {
		if (stage.isOutputReady()) {
			return new Span().appendChild(new A().setHref(stage.getName())
					.appendText(stage.getName()));
		} else {
			return new Span().appendText(stage.getName());
		}
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		if (target.startsWith("/")) {
			final String cleanedTarget = target.substring(1);
			response.setContentType("text/html;charset=utf-8");
			if (cleanedTarget.isEmpty()) {
				response.getWriter()
						.println(createPage(createLandingElement()).write());
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
			} else if (pipegraph.getStages().containsKey(cleanedTarget)) {
				response.getWriter()
						.println(createPage(createStageElement(
								pipegraph.getStage(cleanedTarget), request))
										.write());
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
			}
		}
	}

	private Div createLandingElement() {
		final Div element = new Div();
		final Table table = new Table().setCSSClass(
				"table table-bordered table-hover table-condensed");
		element.appendChild(table);
		final Thead tableHeader = new Thead();
		table.appendChild(tableHeader);
		final Tr headerRow = new Tr();
		tableHeader.appendChild(headerRow);
		headerRow.appendChild(new Th().appendText("Stage"))
				.appendChild(new Th().appendText("Dependencies"))
				.appendChild(new Th().appendText("Status"));
		final Tbody tableBody = new Tbody();
		table.appendChild(tableBody);
		for (final Stage stage : pipegraph.getStages().values()) {
			final Tr bodyRow = new Tr();
			tableBody.appendChild(bodyRow);
			bodyRow.appendChild(
					new Td().appendChild(renderStageReference(stage)));
			final Ul inputList = new Ul().setCSSClass("list-group");
			stage.getInputs().values().stream().map(pipegraph::getStage)
					.forEach(inputStage -> inputList.appendChild(
							new Li().setCSSClass("list-group-item").appendChild(
									renderStageReference(inputStage))));
			bodyRow.appendChild(new Td().appendChild(inputList));
			bodyRow.appendChild(
					new Td().appendText(stage.getStatus().toString()));
		}
		return element;
	}

	@SuppressWarnings("unchecked")
	private Div createStageElement(Stage stage, HttpServletRequest request) {
		final Div element = new Div();
		if (stage.isOutputReady()) {
			if (stage.hasOutput()) {
				if (pipegraph.getContext().getRegistry().has(
						IResourceRenderer.class,
						stage.getOutputClass().toString())) {
					element.appendChild(pipegraph.getContext().getRegistry()
							.create(IResourceRenderer.class,
									stage.getOutputClass().toString())
							.render(stage.readOutput(),
									ConfigMap
											.<String> of(key -> Optional.of(key)
													.filter(k -> request
															.getParameterMap()
															.containsKey(k))
											.map(request::getParameter))));
				} else {
					element.appendChild(new Pre()
							.appendText(stage.readOutput().toString()));
				}
			} else {
				element.appendText("Stage: " + stage + " has no output.");
			}
		} else {
			element.appendText("Stage: " + stage + " not completed.");
		}
		return element;
	}
}
