package com.georgen.melquiades.model.data;

import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;

public abstract class Data {
    private Hits hits;
    private Stat stat;

    public Hits getHits() { return hits; }

    public void setHits(Hits hits) { this.hits = hits; }

    public Stat getStat() { return stat; }

    public void setStat(Stat stat) { this.stat = stat; }

    public abstract void calculate();
}
