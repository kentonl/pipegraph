package edu.uw.cs.lil.pipegraph.task;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

import com.google.protobuf.Message;
import com.hp.gagawa.java.Node;
import com.typesafe.config.Config;

import edu.uw.cs.lil.pipegraph.core.Stage;
import edu.uw.cs.lil.pipegraph.registry.IRegisterable;

public interface ITask<M extends Message> extends IRegisterable {

	@SuppressWarnings("unchecked")
	default Class<M> getOutputClass() {
		return (Class<M>) Arrays.stream(getClass().getGenericInterfaces())
				.filter(t -> t instanceof ParameterizedType)
				.map(t -> (ParameterizedType) t)
				.filter(pt -> ITask.class
						.isAssignableFrom((Class<?>) pt.getRawType()))
				.findAny().get().getActualTypeArguments()[0];
	}

	@SuppressWarnings("unused")
	default Optional<Node> render(Config params) {
		return Optional.empty();
	}

	M run(Stage stage);
}