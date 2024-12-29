package com.georgen.melquiades.model.data;

import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;
import com.georgen.melquiades.core.trackers.Tracker;

public abstract class Data {
    private Hits hits;
    private Stat stat;

    public Hits getHits() { return hits; }

    public void setHits(Hits hits) { this.hits = hits; }

    public Stat getStat() { return stat; }

    public void setStat(Stat stat) { this.stat = stat; }

    public boolean hasHits() { return hits != null; }

    public boolean hasStat() { return stat != null; }

    public abstract boolean isEmpty();

    public abstract void register(Tracker tracker);

    public abstract void calculate();
}
