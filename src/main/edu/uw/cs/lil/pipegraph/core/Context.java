package edu.uw.cs.lil.pipegraph.core;

import java.io.File;
import java.util.Arrays;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import edu.uw.cs.lil.pipegraph.registry.Registry;
import edu.uw.cs.lil.pipegraph.util.LambdaUtil;

public class Context {
	public static final Logger		log	= LoggerFactory
			.getLogger(Context.class);

	private final File				directory;
	private final ExtensionRegistry	extensions;
	private final Registry			registry;

	public Context(File root, String filename) {
		final Reflections reflections = new Reflections(
				new ConfigurationBuilder()
						.setUrls(ClasspathHelper.forJavaClassPath()));
		this.directory = getDirectory(root, filename);
		this.directory.mkdirs();
		this.extensions = getExtensions(reflections);
		this.registry = new Registry(reflections);
	}

	private static File getDirectory(File root, String filename) {
		if (!filename.endsWith(".conf")) {
			throw new IllegalArgumentException(
					"Invalid extension for " + filename);
		}
		return new File(root,
				filename.substring(0, filename.length() - ".conf".length()));
	}

	private static ExtensionRegistry getExtensions(Reflections reflections) {
		final ExtensionRegistry extensions = ExtensionRegistry.newInstance();
		reflections.getSubTypesOf(GeneratedMessage.class).stream()
				.flatMap(c -> Arrays.stream(c.getDeclaredFields())
						.filter(f -> GeneratedExtension.class
								.isAssignableFrom(f.getType()))
						.map(LambdaUtil.rethrow(
								f -> (GeneratedExtension<?, ?>) f.get(c))))
				.forEach(extensions::add);
		return extensions;
	}

	public File getDirectory() {
		return directory;
	}

	public ExtensionRegistry getExtensions() {
		return extensions;
	}

	public Registry getRegistry() {
		return registry;
	}
}
