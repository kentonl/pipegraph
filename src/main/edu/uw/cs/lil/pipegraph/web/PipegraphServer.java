package edu.uw.cs.lil.pipegraph.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.cs.lil.pipegraph.core.Pipegraph;

public class PipegraphServer extends Server {
	public static final Logger log = LoggerFactory
			.getLogger(PipegraphServer.class);

	public PipegraphServer(Pipegraph graph) {
		super(0);

		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setResourceBase("src/main/resources/public");

		final HandlerList handlers = new HandlerList();
		handlers.setHandlers(
				new Handler[] { new StageHandler(graph), resourceHandler });

		setHandler(handlers);
	}
}
