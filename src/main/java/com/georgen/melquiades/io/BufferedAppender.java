package com.georgen.melquiades.io;

import com.georgen.melquiades.model.ErrorCallback;
import com.georgen.melquiades.util.SystemHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BufferedAppender implements AutoCloseable  {

    private final BufferedWriter writer;
    private final Path path;
    private ErrorCallback errorCallback;

    public BufferedAppender(String path) throws IOException {
        this(Paths.get(path));
    }

    public BufferedAppender(Path path) throws IOException {
        this(path, null);
    }

    public BufferedAppender(String path, ErrorCallback errorCallback) throws IOException {
        this(Paths.get(path), errorCallback);
    }

    public BufferedAppender(Path path, ErrorCallback errorCallback) throws IOException {
        if (!Files.exists(path)){
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        this.path = path;
        this.writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        this.errorCallback = errorCallback;

        if (SystemHelper.isUnixSystem()){
            SystemHelper.setFilePermissions(path.toFile());
        }
    }

    public Path getPath() { return path; }

    public ErrorCallback getErrorCallback() { return errorCallback; }

    public void setErrorCallback(ErrorCallback errorCallback) { this.errorCallback = errorCallback; }

    public void append(String message) {
        try {
            writer.append(message);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            if (errorCallback != null) errorCallback.doCallback(e);
        }
    }

    public void append(String... parts) {
        try {
            for (String part : parts) {
                writer.append(part);
            }
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            if (errorCallback != null) errorCallback.doCallback(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
