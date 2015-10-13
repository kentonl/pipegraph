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
	private final ITask					task;
	private final String				type;

	public Stage(String name, Config config, Context context) {
		this.name = name;
		this.type = config.getString("type");
		this.arguments = config.hasPath("args")
				? config.getObject("args").toConfig() : ConfigFactory.empty();
		final Config inputConfig = config.hasPath("inputs")
				? config.getConfig("inputs") : ConfigFactory.empty();
		this.inputs = inputConfig.entrySet().stream().collect(
				Collectors.<Entry<String, ConfigValue>, String, String> toMap(
						entry -> entry.getKey(),
						entry -> inputConfig.getString(entry.getKey())));
		this.output = new File(context.getDirectory(), name);
		this.context = context;
		this.status = Status.WAITING;
		this.task = context.getRegistry().create(ITask.class, type);
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
			return Any.parseFrom(in).is(getOutputClass());
		} catch (final IOException e) {
			throw new RuntimeException(e);
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

	public void run() {
		if (hasOutput()) {
			status = Stage.Status.CACHED;
		} else {
			status = Stage.Status.RUNNING;
			task.run(this);
			status = Stage.Status.COMPLETED;
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

	public <T extends Message> void write(T value) {
		try (final OutputStream out = new FileOutputStream(output)) {
			Any.pack(value).writeTo(out);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static enum Status {
		CACHED, COMPLETED, RUNNING, WAITING
	}
}
