package com.georgen.melquiades.model.data;

import com.georgen.melquiades.core.Hits;
import com.georgen.melquiades.core.Stat;

import java.util.concurrent.ConcurrentMap;

public abstract class Data {
    private String name;
    private Hits hits;
    private Stat stat;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Hits getHits() { return hits; }

    public void setHits(Hits hits) { this.hits = hits; }

    public Stat getStat() { return stat; }

    public void setStat(Stat stat) { this.stat = stat; }
}
