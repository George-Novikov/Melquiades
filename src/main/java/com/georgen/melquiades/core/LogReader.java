package com.georgen.melquiades.core;

import com.georgen.melquiades.io.BufferReader;

import java.io.IOException;

public class LogReader {

    public static final String DEFAULT_PATH = "profiler.log";

    private final BufferReader reader;
    private int position = 0;

    public LogReader() throws IOException {
        this(DEFAULT_PATH);
    }

    public LogReader(String path) throws IOException {
        reader = new BufferReader(path);
    }

    public String findline(String condition) throws IOException {
        reader.readLine(position);

        return null;
    }

}
