package com.georgen.melquiades.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;
import com.georgen.melquiades.model.settings.DataType;
import com.georgen.melquiades.model.trackers.Tracker;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.georgen.melquiades.model.settings.LoggingPolicy.*;

@JsonPropertyOrder({"hits", "stat", "data"})
public class DataGroup extends Data {

    private ConcurrentMap<String, DataProcess> data;

    public ConcurrentMap<String, DataProcess> getData() { return data; }

    public void setData(ConcurrentMap<String, DataProcess> data) { this.data = data; }

    @JsonIgnore
    @Override
    public boolean isEmpty(){ return this.data == null || this.data.isEmpty(); }

    @Override
    public void register(Tracker tracker) {
        if (this.data == null) this.data = new ConcurrentHashMap<>();

        String process = tracker.getProcess();
        if (process == null) return;

        if (Profiler.settings().isBlacklisted(process, DataType.PROCESS)){
            return;
        }

        if (Profiler.logging().isGroupPolicy(DATA)){
            DataProcess dataProcess = this.data.get(process);
            if (dataProcess == null) dataProcess = new DataProcess();
            dataProcess.register(tracker);
            this.data.put(process, dataProcess);
        }
    }

    @Override
    public void calculate() {
        if (isEmpty()) return;

        this.data.values().forEach(DataProcess::calculate);

        if (Profiler.logging().isGroupPolicy(HITS)){
            List<Hits> hitsList = this.data.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(DataProcess::getHits)
                    .collect(Collectors.toList());
            this.setHits(Hits.ofBatch(hitsList));
        }

        if (Profiler.logging().isGroupPolicy(STAT)){
            List<Stat> statList = this.data.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(DataProcess::getStat)
                    .collect(Collectors.toList());
            this.setStat(Stat.ofBatch(statList));
        }
    }
}
