package com.github.kentonl.pipegraph.util;

import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Script;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C3Util {
    public static final Logger log = LoggerFactory.getLogger(C3Util.class);

    private C3Util() {
    }

    // See http://c3js.org for details.
    public static Div renderGraph(final String id, final Config data, final Config axis, final Config grid) {
        final Div chart = new Div().setId(id);
        final Config c3Config = ConfigUtil.builder().add("bindto", "#" + id).add("data", data.root())
                .add("axis", axis.root()).add("grid", grid.root()).build();

        final Script renderJavascript = new Script("text/javascript")
                .appendText("c3.generate(" + c3Config.root().render(ConfigRenderOptions.concise()) + ")");

        return new Div().appendChild(chart).appendChild(renderJavascript);
    }
}
