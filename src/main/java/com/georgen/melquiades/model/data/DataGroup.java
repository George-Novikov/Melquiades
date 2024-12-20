package com.georgen.melquiades.model.data;

import java.util.concurrent.ConcurrentMap;

public class DataGroup extends Data {

    private ConcurrentMap<String, DataProcess> data;

    public ConcurrentMap<String, DataProcess> getData() { return data; }

    public void setData(ConcurrentMap<String, DataProcess> data) { this.data = data; }

    @Override
    public void calculate() {

    }
}
