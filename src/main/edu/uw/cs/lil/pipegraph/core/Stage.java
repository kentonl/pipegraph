package edu.uw.cs.lil.pipegraph.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import edu.uw.cs.lil.pipegraph.task.ITask;

public class Stage {
	public static final Logger			log	= LoggerFactory
			.getLogger(Stage.class);

	private final Config				arguments;
	private final Context				context;
	private final Map<String, String>	inputs;
	private final String				name;
	private final File					output;
	private Status						status;
	private final ITask<?>				task;
	private final String				type;

	public Stage(String name, Config config, Context context) {
		this.name = name;
		this.type = config.getString("type");
		this.arguments = config.hasPath("args")
				? config.getObject("args").toConfig() : ConfigFactory.empty();
		final Config inputConfig = config.hasPath("inputs")
				? config.getConfig("inputs") : ConfigFactory.empty();
		final String scope = getScope(name);
		this.inputs = inputConfig.entrySet().stream().collect(
				Collectors.<Entry<String, ConfigValue>, String, String> toMap(
						entry -> entry.getKey(), entry -> scope
								+ inputConfig.getString(entry.getKey())));
		this.output = new File(context.getDirectory(), name);
		this.context = context;
		this.status = Status.WAITING;
		this.task = context.getRegistry().create(ITask.class, type);
	}

	private static String getScope(String name) {
		final int scopeIndex = name.lastIndexOf(".");
		if (scopeIndex > 0) {
			return name.substring(0, scopeIndex + 1);
		} else {
			return "";
		}
	}

	public Config getArguments() {
		return arguments;
	}

	public Context getContext() {
		return context;
	}

	public Map<String, String> getInputs() {
		return inputs;
	}

	public String getName() {
		return name;
	}

	public Class<? extends Message> getOutputClass() {
		return task.getOutputClass();
	}

	public Status getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public boolean hasInput(String inputName) {
		return new File(context.getDirectory(), inputs.get(inputName)).exists();
	}

	public boolean hasOutput() {
		if (!output.exists()) {
			return false;
		}
		try (final InputStream in = new FileInputStream(output)) {
			Any.parseFrom(in).unpack(getOutputClass());
			return true;
		} catch (NoSuchMethodError | IOException e) {
			return false;
		}
	}

	public boolean hasStatus(Status other) {
		return status.equals(other);
	}

	public boolean isOutputReady() {
		return hasStatus(Status.COMPLETED) || hasStatus(Status.CACHED);
	}

	public <T extends Message> T read(String inputName, Class<T> clazz) {
		try (final InputStream in = new FileInputStream(
				new File(context.getDirectory(), inputs.get(inputName)))) {
			return Any.parseFrom(in).unpack(clazz);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> T readOutput() {
		try (final InputStream in = new FileInputStream(output)) {
			return (T) Any.parseFrom(in).unpack(getOutputClass());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run(Map<String, Stage> stages) {
		if (!inputs.values().stream()
				.allMatch(s -> stages.get(s).isOutputReady())) {
			throw new IllegalArgumentException(
					"Not all input stages are ready.");
		}
		if (hasOutput() && inputs.values().stream()
				.allMatch(s -> stages.get(s).hasStatus(Status.CACHED))) {
			status = Stage.Status.CACHED;
		} else {
			status = Stage.Status.RUNNING;
			try {
				MDC.put("stage-name", name);
				write(task.run(this));
				MDC.remove("stage-name");
				status = Stage.Status.COMPLETED;
			} catch (final Exception e) {
				log.error("Job failed.", e);
				status = Stage.Status.FAILED;
			}
		}
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("%s(%s) [%s]", getName(),
				inputs.entrySet().stream()
						.map(entry -> String.format("%s=%s", entry.getKey(),
								entry.getValue()))
				.collect(Collectors.joining(",")), status);
	}

	private <T extends Message> void write(T value) {
		try (final OutputStream out = new FileOutputStream(output)) {
			Any.pack(value).writeTo(out);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static enum Status {
		CACHED, COMPLETED, FAILED, RUNNING, WAITING
	}
}
