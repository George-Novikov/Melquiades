package com.georgen.melquiades.model.data;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;
import com.georgen.melquiades.model.settings.DataType;
import com.georgen.melquiades.model.trackers.Tracker;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.georgen.melquiades.model.settings.LoggingPolicy.*;

public class DataProcess extends Data {

    private Queue<Double> data;

    public Queue<Double> getData() { return data; }

    public void setData(Queue<Double> data) { this.data = data; }

    @Override
    public boolean isEmpty(){ return this.data == null || this.data.isEmpty(); }

    @Override
    public void register(Tracker tracker){
        if (this.data == null) this.data = new ConcurrentLinkedQueue<>();

        if (Profiler.logging().isProcessPolicy(HITS)){
            if (!hasHits()) this.setHits(new Hits());
            this.getHits().register(tracker);
        }

        if (Profiler.logging().isProcessPolicy(DATA)){
            data.add(tracker.getDuration() / 1000.0);
        }
    }

    @Override
    public void calculate() {
        if (isEmpty()) return;

        if (Profiler.logging().isProcessPolicy(HITS)){
            this.getHits().calculate();
        }

        if (Profiler.logging().isProcessPolicy(STAT)){
            this.setStat(Stat.of(this.data));
        }
    }
}
