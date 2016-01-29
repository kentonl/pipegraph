package edu.uw.pipegraph.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.B;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Pre;
import com.typesafe.config.Config;

import edu.uw.pipegraph.core.Pipegraph;
import edu.uw.pipegraph.core.Stage;
import edu.uw.pipegraph.web.renderer.IResourceRenderer;

public class StageHandler extends TargetedHandler {
	public static final Logger	log	= LoggerFactory
			.getLogger(StageHandler.class);

	private final Pipegraph		pipegraph;

	public StageHandler(Pipegraph pipegraph) {
		this.pipegraph = pipegraph;
	}

	@Override
	public Node createContent(Config params) {
		final Stage stage = pipegraph.getStage(params.getString("name"));
		final Div element = new Div();
		if (stage.isOutputReady()) {
			if (stage.hasOutput()) {
				if (!params.hasPath("raw") && pipegraph.getContext()
						.getRegistry().has(IResourceRenderer.class,
								stage.getOutputClass().toString())) {
					final IResourceRenderer<?> renderer = pipegraph.getContext()
							.getRegistry().create(IResourceRenderer.class,
									stage.getOutputClass().toString());
					element.appendChild(renderer.render(stage.readOutput(),
							params.withFallback(
									renderer.getDefaultArguments())));
				} else {
					element.appendChild(new Pre()
							.appendText(stage.readOutput().toString()));
				}
			} else {
				element.appendText("Stage: " + stage + " has no output.");
			}
		} else {
			element.appendChild(stage.getTask().render(params)
					.orElseGet(() -> new B().appendText(
							"Stage: " + stage + " not completed.")));
		}
		return element;
	}

	@Override
	public String getTarget() {
		return "stage";
	}
}
