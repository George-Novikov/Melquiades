package com.georgen.melquiades.model.data;

import java.util.concurrent.ConcurrentMap;

public class GroupData extends Data {
    private ConcurrentMap<String, ProcessData> data;

    public ConcurrentMap<String, ProcessData> getData() { return data; }

    public void setData(ConcurrentMap<String, ProcessData> data) { this.data = data; }
}
