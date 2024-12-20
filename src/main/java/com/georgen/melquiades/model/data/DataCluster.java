package com.georgen.melquiades.model.data;

import java.util.concurrent.ConcurrentMap;

public class DataCluster extends Data {

    private ConcurrentMap<String, DataGroup> data;

    public ConcurrentMap<String, DataGroup> getData() { return data; }

    public void setData(ConcurrentMap<String, DataGroup> data) { this.data = data; }

    @Override
    public void calculate() {

    }
}
