package com.georgen.melquiades.model.data;

import java.util.concurrent.ConcurrentMap;

public class ClusterData extends Data {
    private ConcurrentMap<String, GroupData> data;

    public ConcurrentMap<String, GroupData> getData() { return data; }

    public void setData(ConcurrentMap<String, GroupData> data) { this.data = data; }
}
