package com.georgen.melquiades.io;

import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.util.LogRotator;
import com.georgen.melquiades.util.SystemHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BufferAppender implements AutoCloseable  {

    private final BufferedWriter writer;
    private final Path path;
    private ErrorHandler errorHandler;

    public BufferAppender(String path) throws IOException {
        this(Paths.get(path));
    }

    public BufferAppender(Path path) throws IOException {
        this(path, null);
    }

    public BufferAppender(String path, ErrorHandler errorHandler) throws IOException {
        this(Paths.get(path), errorHandler);
    }

    public BufferAppender(Path path, ErrorHandler errorHandler) throws IOException {
        if (!Files.exists(path)){
            SystemHelper.createFile(path);
        }

        this.path = path;
        this.writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        this.errorHandler = errorHandler;
    }

    public Path getPath() { return path; }

    public ErrorHandler getErrorHandler() { return errorHandler; }

    public void setErrorHandler(ErrorHandler errorHandler) { this.errorHandler = errorHandler; }

    public void append(String message) {
        try {
            writer.append(message);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            if (errorHandler != null) errorHandler.handle(e);
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
            if (errorHandler != null) errorHandler.handle(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
