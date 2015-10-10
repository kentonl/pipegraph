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

public class Stage {
	private final static String			DEFAULT_SUBNAME	= "data";

	private final Config				arguments;
	private final Context				context;
	private final Map<String, String>	inputs;
	private final String				name;
	private final File					outputDir;
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
		this.outputDir = new File(context.getDirectory(), name);
		this.outputDir.mkdirs();
		this.context = context;
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

	public String getType() {
		return type;
	}

	public Resource read(String inputName) {
		return read(inputName, DEFAULT_SUBNAME);
	}

	public <T> T read(String inputName,
			GeneratedExtension<Resource, T> extension) {
		return read(inputName, DEFAULT_SUBNAME, extension);
	}

	public Resource read(String inputName, String subname) {
		try (final InputStream in = new FileInputStream(new File(
				new File(context.getDirectory(), inputs.get(inputName)),
				subname))) {
			return Resource.parseFrom(in, context.getExtensions());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T read(String inputName, String subname,
			GeneratedExtension<Resource, T> extension) {
		try (final InputStream in = new FileInputStream(new File(
				new File(context.getDirectory(), inputs.get(inputName)),
				subname))) {
			return Resource.parseFrom(in, context.getExtensions())
					.getExtension(extension);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T readOutput(GeneratedExtension<Resource, T> extension) {
		return readOutput(DEFAULT_SUBNAME, extension);
	}

	public <T> T readOutput(String subname,
			GeneratedExtension<Resource, T> extension) {
		try (final InputStream out = new FileInputStream(
				new File(outputDir, subname))) {
			return Resource.parseFrom(out, context.getExtensions())
					.getExtension(extension);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getName(),
				inputs.entrySet().stream()
						.map(entry -> String.format("%s=%s", entry.getKey(),
								entry.getValue()))
				.collect(Collectors.joining(",")));
	}

	public <T> void write(GeneratedExtension<Resource, T> extension, T value) {
		write(DEFAULT_SUBNAME, extension, value);
	}

	public <T> void write(String subname,
			final GeneratedExtension<Resource, T> extension, final T value) {
		try (final OutputStream out = new FileOutputStream(
				new File(outputDir, subname))) {
			Resource.newBuilder().setExtension(extension, value).build()
					.writeTo(out);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
