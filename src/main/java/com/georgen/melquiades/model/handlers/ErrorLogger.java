package com.georgen.melquiades.model.handlers;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ErrorLogger implements ErrorHandler {

    private Path logPath;

    public ErrorLogger(String logPath){
        this(Paths.get(logPath));
    }

    public ErrorLogger(Path logPath){
        this.logPath = logPath;
    }

    @Override
    public void handle(Exception e) {

    }
}
