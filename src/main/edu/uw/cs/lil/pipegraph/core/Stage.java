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

import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import edu.uw.cs.lil.pipegraph.CommonProto.Resource;
import edu.uw.cs.lil.pipegraph.task.ITask;

public class Stage {
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

	public GeneratedExtension<Resource, ?> getOutputExtension() {
		return task.getOutputExtension();
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
		return output.exists();
	}

	public <T> T read(String inputName,
			GeneratedExtension<Resource, T> extension) {
		try (final InputStream in = new FileInputStream(
				new File(context.getDirectory(), inputs.get(inputName)))) {
			return Resource.parseFrom(in, context.getExtensions())
					.getExtension(extension);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T readOutput() {
		try (final InputStream out = new FileInputStream(output)) {
			return Resource.parseFrom(out, context.getExtensions())
					.getExtension(
							(GeneratedExtension<Resource, T>) getOutputExtension());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		task.run(this);
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

	@SuppressWarnings("unchecked")
	public <T> void write(T value) {
		try (final OutputStream out = new FileOutputStream(output)) {
			Resource.newBuilder()
					.setExtension(
							(GeneratedExtension<Resource, T>) getOutputExtension(),
							value)
					.build().writeTo(out);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static enum Status {
		COMPLETED, RUNNING, WAITING
	}
}
