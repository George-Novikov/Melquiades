package com.georgen.melquiades.model.handlers;

import com.georgen.melquiades.io.BufferAppender;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ErrorLogger implements ErrorHandler {

    public static final String DEFAULT_FILE_NAME = "profiler.error.log";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private Path logPath;

    public ErrorLogger(){
        this(DEFAULT_FILE_NAME);
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
            appender.append(DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            appender.append(Arrays.toString(e.getStackTrace()));
        } catch (Exception er){
            // swallow
        }
    }
}
