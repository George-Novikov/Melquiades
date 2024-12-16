package com.georgen.melquiades.model.handlers;

@FunctionalInterface
public interface ErrorHandler {
    void handle(Exception e);
}
