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

public class DataRoot extends Data {
    private LocalDateTime start;
    private LocalDateTime finish;
    private Long duration;
    private ConcurrentMap<String, DataCluster> data;

    public DataRoot() {
        this.start = LocalDateTime.now();
    }

    public LocalDateTime getStart() { return start; }

    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getFinish() { return finish; }

    public void setFinish(LocalDateTime finish) { this.finish = finish; }

    public Long getDuration() { return duration; }

    public void setDuration(Long duration) { this.duration = duration; }

    public ConcurrentMap<String, DataCluster> getData() { return data; }

    public void setData(ConcurrentMap<String, DataCluster> data) { this.data = data; }

    @Override
    public boolean isEmpty(){ return this.data == null || this.data.isEmpty(); }

    @Override
    public void register(Tracker tracker){
        if (this.data == null) this.data = new ConcurrentHashMap<>();

        String cluster = tracker.getCluster();
        if (cluster == null) return;

        if (Profiler.settings().isBlacklisted(cluster, DataType.CLUSTER)){
            return;
        }

        if (Profiler.logging().isRootPolicy(DATA)){
            DataCluster dataCluster = this.data.get(cluster);
            if (dataCluster == null) dataCluster = new DataCluster();
            dataCluster.register(tracker);
        }
    }

    @Override
    public void calculate() {
        this.finish = LocalDateTime.now();
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);

        if (isEmpty()) return;

        this.data.values().forEach(DataCluster::calculate);

        if (Profiler.logging().isRootPolicy(HITS)){
            List<Hits> hitsList = this.data.values().stream().map(DataCluster::getHits).collect(Collectors.toList());
            this.setHits(Hits.ofBatch(hitsList));
        }

        if (Profiler.logging().isRootPolicy(STAT)){
            List<Stat> statList = this.data.values().stream().map(DataCluster::getStat).collect(Collectors.toList());
            this.setStat(Stat.ofBatch(statList));
        }
    }
}