package edu.uw.pipegraph.core;

import com.google.common.base.Preconditions;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

import edu.uw.pipegraph.registry.Registry;
import edu.uw.pipegraph.util.LambdaUtil;

public class Context {
    public static final Logger log = LoggerFactory.getLogger(Context.class);

    private final File directory;
    private final ExtensionRegistry extensions;
    private final Registry registry;
    private final String experimentName;

    public Context(File root, File configFile) {
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath()));
        this.experimentName = getExperimentName(configFile);
        this.directory = new File(root, experimentName);
        this.directory.mkdirs();
        this.extensions = getExtensions(reflections);
        this.registry = new Registry(reflections);
    }

    private static String getExperimentName(File configFile) {
        Preconditions.checkArgument(configFile.getName().endsWith(".conf"), "Invalid extension of experiment file.");
        return configFile.getName().substring(0, configFile.getName().length() - ".conf".length());
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

    public String getExperimentName() {
        return experimentName;
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
