package edu.uw.cs.lil.pipegraph.task.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.uw.cs.lil.pipegraph.task.ITask;

public class TaskRepository {
	private final Map<String, Supplier<? extends ITask>> repo;

	@SafeVarargs
	public TaskRepository(Supplier<? extends ITask>... taskSuppliers) {
		repo = new HashMap<>();
		for (final Supplier<? extends ITask> t : taskSuppliers) {
			register(t);
		}
	}

	public ITask create(String type) {
		return repo.get(type).get();
	}

	public void register(Supplier<? extends ITask> taskSupplier) {
		final ITask task = taskSupplier.get();
		if (repo.containsKey(task.getType())) {
			throw new IllegalArgumentException(
					"Repository already contains task of type: "
							+ task.getType());
		}
		repo.put(task.getType(), taskSupplier);
	}
}
