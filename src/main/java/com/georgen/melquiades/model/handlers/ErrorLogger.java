package com.georgen.melquiades.model.handlers;

import com.georgen.melquiades.io.BufferAppender;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ErrorLogger implements ErrorHandler {

    public static final String DEFAULT_ERROR_LOG = "profiler.error.log";

    private Path logPath;

    public ErrorLogger(){
        this(DEFAULT_ERROR_LOG);
    }

    public ErrorLogger(String logPath){
        this(Paths.get(logPath));
    }

    public ErrorLogger(Path logPath){
        this.logPath = logPath;
    }

    @Override
    public void handle(Exception e) {
        try (BufferAppender appender = new BufferAppender(logPath)) {
            appender.append(e.getMessage());
            Arrays
                    .stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .forEach(appender::append);
        } catch (Exception er){
            // swallow
        }
    }
}
