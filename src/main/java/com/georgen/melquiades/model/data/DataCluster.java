package com.georgen.melquiades.model.data;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.Stat;
import com.georgen.melquiades.model.settings.DataType;
import com.georgen.melquiades.model.trackers.Tracker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.georgen.melquiades.model.settings.LoggingPolicy.*;

public class DataCluster extends Data {

    private ConcurrentMap<String, DataGroup> data;

    public ConcurrentMap<String, DataGroup> getData() { return data; }

    public void setData(ConcurrentMap<String, DataGroup> data) { this.data = data; }

    @Override
    public boolean isEmpty(){ return this.data == null || this.data.isEmpty(); }

    @Override
    public void register(Tracker tracker) {
        if (this.data == null) this.data = new ConcurrentHashMap<>();

        String group = tracker.getGroup();
        if (group == null) return;

        if (Profiler.settings().isBlacklisted(group, DataType.GROUP)){
            return;
        }

        if (Profiler.logging().isClusterPolicy(DATA)){
            DataGroup dataGroup = this.data.get(group);
            if (dataGroup == null) dataGroup = new DataGroup();
            dataGroup.register(tracker);
        }
    }

    @Override
    public void calculate() {
        if (isEmpty()) return;

        this.data.values().forEach(DataGroup::calculate);

        if (Profiler.logging().isClusterPolicy(HITS)){
            List<Hits> hitsList = this.data.values().stream().map(DataGroup::getHits).collect(Collectors.toList());
            this.setHits(Hits.ofBatch(hitsList));
        }

        if (Profiler.logging().isClusterPolicy(STAT)){
            List<Stat> statList = this.data.values().stream().map(DataGroup::getStat).collect(Collectors.toList());
            this.setStat(Stat.ofBatch(statList));
        }
    }
}
