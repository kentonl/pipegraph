package edu.uw.cs.lil.pipegraph.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import edu.uw.cs.lil.pipegraph.Common.Resource;

public class Pipe {
	private final static String	DEFAULT_SUBNAME	= "data";
	private final File			dir;

	public Pipe(File root, String name) {
		this.dir = new File(root, name);
		dir.mkdirs();
	}

	public <T> T read(GeneratedExtension<Resource, T> extension) {
		return read(DEFAULT_SUBNAME, extension);
	}

	public <T> T read(String subname,
			GeneratedExtension<Resource, T> extension) {
		final ExtensionRegistry registry = ExtensionRegistry.newInstance();
		registry.add(extension);
		try (final InputStream in = new FileInputStream(
				new File(dir, subname))) {
			return Resource.parseFrom(in, registry).getExtension(extension);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return dir.getName();
	}

	public <T> void write(GeneratedExtension<Resource, T> extension, T value) {
		write(DEFAULT_SUBNAME, extension, value);
	}

	public <T> void write(String subname,
			final GeneratedExtension<Resource, T> extension, final T value) {
		try (final OutputStream out = new FileOutputStream(
				new File(dir, subname))) {
			Resource.newBuilder().setExtension(extension, value).build()
					.writeTo(out);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
