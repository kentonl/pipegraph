package edu.uw.cs.lil.pipegraph.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.uw.cs.lil.pipegraph.util.HtmlUtil;
import edu.uw.cs.lil.pipegraph.util.MapUtil;

public abstract class TargetedHandler extends AbstractHandler {
	public abstract Node createContent(Config params);

	public abstract String getTarget();

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		if (target.equals("/" + getTarget())) {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println(HtmlUtil.createPage(
					createContent(ConfigFactory.parseMap(MapUtil.mapToMap(
							request.getParameterMap(), k -> k, v -> v[0]))))
					.write());
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
		}
	}
}
