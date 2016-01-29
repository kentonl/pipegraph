package edu.uw.pipegraph.web.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Pre;
import com.typesafe.config.Config;

public class LogHandler extends TargetedHandler {
	public static final Logger log = LoggerFactory.getLogger(LogHandler.class);

	public LogHandler() {
	}

	@Override
	public Node createContent(Config params) {
		final Path logFile = Paths.get("logs",
				params.getString("name") + ".log");
		if (logFile.toFile().exists()) {
			try {
				return new Pre().appendText(
						Files.lines(logFile).collect(Collectors.joining("\n")));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return new Div().appendText("Empty log.");
		}
	}

	@Override
	public String getTarget() {
		return "log";
	}
}
