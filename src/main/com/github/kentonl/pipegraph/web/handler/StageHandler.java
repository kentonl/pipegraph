package com.github.kentonl.pipegraph.web.handler;

import com.google.protobuf.Message;

import com.github.kentonl.pipegraph.core.Stage;
import com.github.kentonl.pipegraph.core.Stage.Status;
import com.github.kentonl.pipegraph.util.ConfigUtil;
import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.B;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Pre;
import com.hp.gagawa.java.elements.Ul;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.LongStream;

import com.github.kentonl.pipegraph.core.Pipegraph;
import com.github.kentonl.pipegraph.util.CollectionUtil;
import com.github.kentonl.pipegraph.web.renderer.IResourceRenderer;

public class StageHandler extends TargetedHandler {
	public static final Logger log = LoggerFactory.getLogger(StageHandler.class);

	private final Pipegraph pipegraph;

	public StageHandler(Pipegraph pipegraph) {
		this.pipegraph = pipegraph;
	}

	@Override
	public Node createContent(Config params) {
		final Stage stage = pipegraph.getStage(params.getString("name"));
		final Div element = new Div();
		if (stage.hasStatus(Status.COMPLETED, Status.CACHED) || (stage.hasStatus(Status.RUNNING) && stage
				.hasOutput())) {
			if (stage.hasOutput()) {
				final Optional<IResourceRenderer<Message>> renderer = Optional.of(pipegraph.getContext().getRegistry())
						.filter(registry -> registry.has(IResourceRenderer.class, stage.getOutputClass().toString()))
						.map(registry -> registry.create(IResourceRenderer.class, stage.getOutputClass().toString()));
				if (renderer.filter(r -> r.canRenderStream()).isPresent()) {
					element.appendChild(renderer.get().renderStream(stage.readOutput(),
							params.withFallback(renderer.get().getDefaultArguments())));
				} else if (params.hasPath("rawIndex")) {
					if (!params.hasPath("raw") && renderer.isPresent()) {
						element.appendChild(renderer.get()
								.render(CollectionUtil.getUpToIth(stage.readOutput(), params.getInt("rawIndex")),
										params.withFallback(renderer.get().getDefaultArguments())));
					} else {
						element.appendChild(new Pre().appendText(
								CollectionUtil.getUpToIth(stage.readOutput(), params.getInt("rawIndex")).toString()));
					}
				} else {
					long numResources = stage.readOutput().count();
					if (numResources == 1) {
						return createContent(params.withValue("rawIndex", ConfigValueFactory.fromAnyRef(0)));
					}
					final Ul list = new Ul().setCSSClass("list-group");
					element.appendChild(list);
					LongStream.range(0, numResources).mapToObj(index -> new Li().setCSSClass("list-group-item")
							.appendChild(new A().setHref(ConfigUtil
									.encodeURL(params.withValue("rawIndex", ConfigValueFactory.fromAnyRef(index))))
									.appendText(Long.toString(index)))).forEach(list::appendChild);
				}
			} else {
				element.appendText("Stage: " + stage + " has no output.");
			}
		} else {
			element.appendChild(stage.getTask().render(params)
					.orElseGet(() -> new B().appendText("Stage: " + stage + " not completed.")));
		}
		return element;
	}

	@Override public String getTarget() {
		return "stage";
	}
}
