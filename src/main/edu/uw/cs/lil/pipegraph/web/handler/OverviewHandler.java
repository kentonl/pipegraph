package edu.uw.cs.lil.pipegraph.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import com.hp.gagawa.java.elements.Ul;
import com.typesafe.config.Config;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;
import edu.uw.cs.lil.pipegraph.core.Stage;

public class OverviewHandler extends TargetedHandler {
	public static final Logger	log	= LoggerFactory
			.getLogger(OverviewHandler.class);

	private final Pipegraph		pipegraph;

	public OverviewHandler(Pipegraph pipegraph) {
		this.pipegraph = pipegraph;
	}

	private static Span renderLogsReference(Stage stage) {
		return new Span().appendChild(new A()
				.setHref("log?name=" + stage.getName()).appendText("log"));
	}

	private static Span renderStageReference(Stage stage) {
		if (stage.isOutputReady()) {
			return new Span().appendChild(
					new A().setHref("stage?name=" + stage.getName())
							.appendText(stage.getName()));
		} else {
			return new Span().appendText(stage.getName());
		}
	}

	@Override
	public Node createContent(Config params) {
		final Table overview = new Table().setCSSClass(
				"table table-bordered table-hover table-condensed");
		final Thead tableHeader = new Thead();
		overview.appendChild(tableHeader);
		final Tr headerRow = new Tr();
		tableHeader.appendChild(headerRow);
		headerRow.appendChild(new Th().appendText("Stage"))
				.appendChild(new Th().appendText("Dependencies"))
				.appendChild(new Th().appendText("Logs"))
				.appendChild(new Th().appendText("Status"));
		final Tbody tableBody = new Tbody();
		overview.appendChild(tableBody);
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
					new Td().appendChild(renderLogsReference(stage)));
			bodyRow.appendChild(
					new Td().appendText(stage.getStatus().toString()));
		}
		return overview;
	}

	@Override
	public String getTarget() {
		return "";
	}
}
