package edu.uw.pipegraph.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.protobuf.Message;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import edu.uw.pipegraph.task.ITask;

public class Stage {
	public static final Logger			log	= LoggerFactory
			.getLogger(Stage.class);

	private final Config				arguments;
	private final Context				context;
	private final Map<String, String>	inputs;
	private final String				name;
	private final File					outputDir;
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
		this.outputDir = new File(context.getDirectory(), name);
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

	private static int numProtos(File dir) {
		return dir.listFiles().length;
	}

	private static <T extends Message> T readProto(File dir, Class<T> clazz,
			int i) {
		final File protoFile = new File(dir, Integer.toString(i));
		try (final InputStream in = new FileInputStream(protoFile)) {
			return unpackProto(in, clazz);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T extends Message> Stream<T> readProtos(File dir,
			Class<T> clazz) {
		return IntStream.range(0, numProtos(dir))
				.mapToObj(i -> readProto(dir, clazz, i));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Message> T unpackProto(InputStream in,
			Class<T> clazz) {
		try {
			return (T) clazz.getMethod("parseFrom", InputStream.class)
					.invoke(null, in);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
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

	public ITask<?> getTask() {
		return task;
	}

	public String getType() {
		return type;
	}

	public boolean hasInput(String inputName) {
		return new File(context.getDirectory(), inputs.get(inputName)).exists();
	}

	public boolean hasOutput() {
		return outputDir.exists();
	}

	public boolean hasStatus(Status other) {
		return status.equals(other);
	}

	public boolean isOutputReady() {
		return hasStatus(Status.COMPLETED) || hasStatus(Status.CACHED);
	}

	public <T extends Message> int numOutputs() {
		return numProtos(outputDir);
	}

	public <T extends Message> Stream<T> read(String inputName,
			Class<T> clazz) {
		return readProtos(
				new File(context.getDirectory(), inputs.get(inputName)), clazz);
	}

	public <T extends Message> T read(String inputName, Class<T> clazz, int i) {
		return readProto(
				new File(context.getDirectory(), inputs.get(inputName)), clazz,
				i);
	}

	public <T extends Message> List<T> readList(String inputName,
			Class<T> clazz) {
		return read(inputName, clazz).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> Stream<T> readOutput() {
		return (Stream<T>) readProtos(outputDir, getOutputClass());
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> T readOutput(int i) {
		return (T) readProto(outputDir, getOutputClass(), i);
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

	private <T extends Message> void write(Stream<T> values) {
		outputDir.mkdirs();
		final AtomicInteger count = new AtomicInteger(0);
		values.forEach(v -> {
			try (final OutputStream out = new FileOutputStream(new File(
					outputDir, Integer.toString(count.getAndIncrement())))) {
				v.writeTo(out);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static enum Status {
		CACHED, COMPLETED, FAILED, RUNNING, WAITING;

		public String getLabelType() {
			switch (this) {
				case CACHED:
					return "info";
				case COMPLETED:
					return "success";
				case FAILED:
					return "danger";
				case RUNNING:
					return "warning";
				case WAITING:
					return "default";
			}
			throw new RuntimeException("Unknown label type.");
		}

	}
}
