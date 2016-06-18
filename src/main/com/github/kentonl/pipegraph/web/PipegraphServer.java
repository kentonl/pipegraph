package com.github.kentonl.pipegraph.web;

import com.github.kentonl.pipegraph.web.handler.StageHandler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import com.github.kentonl.pipegraph.core.Pipegraph;
import com.github.kentonl.pipegraph.web.handler.LogHandler;
import com.github.kentonl.pipegraph.web.handler.OverviewHandler;

public class PipegraphServer extends Server {
    public static final Logger log = LoggerFactory.getLogger(PipegraphServer.class);

    public PipegraphServer(Pipegraph graph, Optional<Integer> port) {
        super(port.orElse(0));

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setResourceBase("src/main/resources/public");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{new OverviewHandler(graph),
                new StageHandler(graph), new LogHandler(graph.getContext()), resourceHandler});

        setHandler(handlers);
    }

    public String getURL() {
        try {
            return "http://" + InetAddress.getLocalHost().getCanonicalHostName()
                    + ":"
                    + ((ServerConnector) getConnectors()[0]).getLocalPort();
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
