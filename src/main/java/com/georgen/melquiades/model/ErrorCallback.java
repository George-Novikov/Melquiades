package com.georgen.melquiades.model;

@FunctionalInterface
public interface ErrorCallback {
    void doCallback(Exception e);
}
