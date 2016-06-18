package com.github.kentonl.pipegraph.web.handler;

import com.github.kentonl.pipegraph.core.Pipegraph;
import com.github.kentonl.pipegraph.core.Stage;
import com.github.kentonl.pipegraph.util.ConfigUtil;
import com.github.kentonl.pipegraph.util.ConfigUtil.ConfigBuilder;
import com.github.kentonl.pipegraph.util.HtmlUtil;
import com.github.kentonl.pipegraph.util.MapUtil;
import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import com.typesafe.config.Config;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverviewHandler extends TargetedHandler {
    public static final Logger log = LoggerFactory
            .getLogger(OverviewHandler.class);

    private final Pipegraph pipegraph;

    public OverviewHandler(Pipegraph pipegraph) {
        this.pipegraph = pipegraph;
    }

    private static Span renderLogsReference(Stage stage) {
        return new Span().appendChild(new A()
                .setHref("log?name=" + stage.getName()).appendText("log"));
    }

    private static Span renderStageReference(Stage stage, boolean raw) {
        final ConfigBuilder builder = new ConfigBuilder().add("name",
                stage.getName());
        final Span span = new Span();
        if (raw) {
            span.appendText(" (");
            span.appendChild(new A()
                    .setHref("stage" + ConfigUtil
                            .encodeURL(builder.add("raw", true).build()))
                    .appendText("raw"));
            span.appendText(")");
        } else {
            span.appendChild(new A()
                    .setHref("stage" + ConfigUtil.encodeURL(builder.build()))
                    .appendText(stage.getName()));
        }
        return span;
    }

    @Override
    public Node createContent(Config params) {
        final Table overview = new Table().setCSSClass("table table-hover");
        final Thead tableHeader = new Thead();
        overview.appendChild(tableHeader);
        final Tr headerRow = new Tr();
        tableHeader.appendChild(headerRow);
        headerRow.appendChild(new Th().appendText("Stage"))
                .appendChild(new Th().appendText("Dependencies"))
                .appendChild(new Th().appendText("Logs"))
                .appendChild(new Th().appendText("Status"))
                .appendChild(new Th().appendText("File size"))
                .appendChild(new Th().appendText("Duration"))
                .appendChild(new Th().appendText("Progress"));
        final Tbody tableBody = new Tbody();
        overview.appendChild(tableBody);
        for (final Stage stage : pipegraph.getStages().values()) {
            final Tr bodyRow = new Tr();
            tableBody.appendChild(bodyRow);
            bodyRow.appendChild(
                    new Td().appendChild(renderStageReference(stage, false))
                            .appendChild(renderStageReference(stage, true)));
            bodyRow.appendChild(new Td().appendChild(HtmlUtil
                    .mapToDescriptionList(MapUtil.mapToMap(stage.getInputs(),
                            name -> renderStageReference(
                                    pipegraph.getStage(name), false)))
                    .setCSSClass("dl-horizontal")));
            bodyRow.appendChild(
                    new Td().appendChild(renderLogsReference(stage)));

            bodyRow.appendChild(new Td().appendChild(new Span()
                    .setCSSClass("label label-" + stage.getStatus().getLabelType())
                    .appendText(stage.getStatus().toString())));

            Td size = new Td();
            if (stage.hasStatus(Stage.Status.CACHED, Stage.Status.COMPLETED, Stage.Status.RUNNING)) {
                size.appendChild(new Span()
                        .setCSSClass("badge")
                        .appendText(FileUtils.byteCountToDisplaySize(stage.size())));
            }
            bodyRow.appendChild(size);

            Td duration = new Td();
            if (stage.hasStatus(Stage.Status.COMPLETED, Stage.Status.RUNNING)) {
                duration.appendChild(new Span()
                        .setCSSClass("badge")
                        .appendText(stage.getTimer().toString()));
            }
            bodyRow.appendChild(duration);

            double progressPercentage = (100.0 * stage.getProgressCount()) /
                    stage.getProgressTotal();

            bodyRow.appendChild(new Td().appendChild(new Div()
                    .setCSSClass("progress")
                    .appendChild(new Div()
                            .setCSSClass("progress-bar progress-bar-striped progress-bar-"
                                    + stage.getStatus().getLabelType())
                            .setStyle(String.format(
                                    "min-width: 2em; width: %d%%;",
                                    Math.round(progressPercentage)))
                            .appendText(String.format(
                                    "%d/%d (%.2f%%)",
                                    stage.getProgressCount(),
                                    stage.getProgressTotal(),
                                    progressPercentage)))));
        }
        return overview;
    }

    @Override
    public String getTarget() {
        return "";
    }
}
