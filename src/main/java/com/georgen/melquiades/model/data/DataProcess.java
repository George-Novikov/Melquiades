package com.georgen.melquiades.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;
import com.georgen.melquiades.core.trackers.Tracker;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import static com.georgen.melquiades.model.settings.LoggingPolicy.*;

@JsonPropertyOrder({"hits", "stat", "data"})
public class DataProcess extends Data {

    private Queue<Double> data;

    public Queue<Double> getData() { return data; }

    public void setData(Queue<Double> data) { this.data = data; }

    @JsonIgnore
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
            double duration = tracker.getDuration() / 1000.0;
            data.add(duration > 0 ? duration : 0.0);
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

    public void averageWith(DataProcess outerData){
        if (this.hasHits()){
            Hits outerHits = outerData.getHits();
            this.getHits().averageWith(outerHits); // null check is inside
        }

        if (this.hasStat()){
            Stat outerStat = outerData.getStat();
            this.getStat().averageWith(outerStat); // null check is inside
        }
    }
}
