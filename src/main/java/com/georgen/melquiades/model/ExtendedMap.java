package com.georgen.melquiades.model;

import com.georgen.melquiades.core.Hits;

import java.util.HashMap;

public class ExtendedMap extends HashMap<String, Object> {
    private String name;
    private Integer type;
    private Hits hits;

    public ExtendedMap(String name, Integer type, Hits hits) {
        this.name = name;
        this.type = type;
        this.hits = hits;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }

    public void setType(Integer type) { this.type = type; }

    public Hits getHits() { return hits; }

    public void setHits(Hits hits) { this.hits = hits; }
}
