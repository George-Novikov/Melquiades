package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

public abstract class HistoryReport {
    public static final String FILE_NAME = "profiler.history";
    public abstract void consume(DataRoot data);
}
