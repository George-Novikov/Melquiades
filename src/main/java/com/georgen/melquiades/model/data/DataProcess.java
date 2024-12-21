package com.georgen.melquiades.model.data;

import com.georgen.melquiades.model.Stat;

import java.util.List;

public class DataProcess extends Data {

    private List<Double> data;

    public List<Double> getData() { return data; }

    public void setData(List<Double> data) { this.data = data; }

    @Override
    public void calculate() {
        this.setStat(Stat.of(this.data));
    }
}
