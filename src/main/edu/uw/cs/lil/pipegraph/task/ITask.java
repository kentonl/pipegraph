package edu.uw.cs.lil.pipegraph.task;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import com.google.protobuf.Message;

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

	M run(Stage stage);
}