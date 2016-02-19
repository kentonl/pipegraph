package edu.uw.pipegraph.core;

import com.google.common.base.Stopwatch;
import com.google.protobuf.Message;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.uw.pipegraph.task.ITask;
import edu.uw.pipegraph.util.CollectionUtil;
import edu.uw.pipegraph.util.LambdaUtil;

public class Stage {
    public static final Logger log = LoggerFactory
            .getLogger(Stage.class);

    private final Config arguments;
    private final Context context;
    private final Map<String, String> inputs;
    private final String name;
    private final File output;
    private final ITask<?> task;
    private final String type;
    private final Stopwatch timer;
    private int progressCount = 0;
    private int progressTotal = 0;
    private Status status;

    public Stage(String name, Config config, Context context) {
        this.name = name;
        this.type = config.getString("type");
        this.arguments = config.hasPath("args")
                ? config.getObject("args").toConfig() : ConfigFactory.empty();
        final Config inputConfig = config.hasPath("inputs")
                ? config.getConfig("inputs") : ConfigFactory.empty();
        final String scope = getScope(name);
        this.inputs = inputConfig.entrySet().stream().collect(
                Collectors.<Entry<String, ConfigValue>, String, String>toMap(
                        entry -> entry.getKey(), entry -> scope
                                + inputConfig.getString(entry.getKey())));
        this.output = new File(context.getDirectory(), name);
        this.context = context;
        this.status = Status.WAITING;
        this.task = context.getRegistry().create(ITask.class, type);
        this.timer = Stopwatch.createUnstarted();
        this.progressCount = 0;
        this.progressTotal = 1;
    }

    private static String getScope(String name) {
        final int scopeIndex = name.lastIndexOf(".");
        if (scopeIndex > 0) {
            return name.substring(0, scopeIndex + 1);
        } else {
            return "";
        }
    }

    @SuppressWarnings({"resource", "unchecked"})
    private static <T extends Message> Stream<T> readProtos(File filename,
                                                            Class<T> clazz) {
        FileInputStream in;
        try {
            in = new FileInputStream(filename);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Method parseDelimitedFrom;
        try {
            parseDelimitedFrom = clazz.getMethod("parseDelimitedFrom",
                    InputStream.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return CollectionUtil.streamWhile(
                LambdaUtil.rethrowSupplier(
                        () -> (T) parseDelimitedFrom.invoke(null, in)),
                Objects::nonNull);
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

    public int getProgressCount() {
        return progressCount;
    }

    public int getProgressTotal() {
        return progressTotal;
    }

    public void setProgress(int progressCount, int progressTotal) {
        this.progressCount = progressCount;
        this.progressTotal = progressTotal;
    }

    public boolean hasInput(String inputName) {
        return new File(context.getDirectory(), inputs.get(inputName)).exists();
    }

    public boolean hasOutput() {
        return output.exists();
    }

    public boolean hasStatus(Status other) {
        return status.equals(other);
    }

    public boolean isOutputReady() {
        return hasStatus(Status.COMPLETED) || hasStatus(Status.CACHED);
    }

    public <T extends Message> Stream<T> read(String inputName,
                                              Class<T> clazz) {
        return readProtos(
                new File(context.getDirectory(), inputs.get(inputName)), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Message> Stream<T> readOutput() {
        return (Stream<T>) readProtos(output, getOutputClass());
    }

    public Stopwatch getTimer() {
        return timer;
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
                timer.start();
                writeOutput(task.run(this));
                timer.stop();
                MDC.remove("stage-name");
                status = Stage.Status.COMPLETED;
                log.info("Stage '{}' completed in {}.", name, timer);
            } catch (final Exception e) {
                log.error("Job failed.", e);
                clearOutput();
                status = Stage.Status.FAILED;
            }
        }
        progressCount = progressTotal;
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

    private <T extends Message> void writeOutput(Stream<T> values) {
        try (final OutputStream out = new FileOutputStream(output)) {
            values.forEach(
                    LambdaUtil.rethrowConsumer(v -> v.writeDelimitedTo(out)));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Message> void clearOutput() {
        output.delete();
    }

    public enum Status {
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
