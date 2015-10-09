package edu.uw.cs.lil.pipegraph.task.repo;

import edu.uw.cs.lil.pipegraph.task.IntegerTask;
import edu.uw.cs.lil.pipegraph.task.MultiplyTask;
import edu.uw.cs.lil.pipegraph.task.StringTask;
import edu.uw.cs.lil.pipegraph.task.SumTask;

public class CommonTaskRepository extends TaskRepository {
	public CommonTaskRepository() {
		super(StringTask::new, IntegerTask::new, SumTask::new,
				MultiplyTask::new);
	}
}
