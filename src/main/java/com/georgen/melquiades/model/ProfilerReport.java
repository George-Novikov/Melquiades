package com.georgen.melquiades.model;


import com.georgen.melquiades.model.data.DataCluster;
import com.georgen.melquiades.model.trackers.Tracker;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

public class ProfilerReport {
    private LocalDateTime start;
    private LocalDateTime finish;
    private Double duration;
    private ConcurrentMap<String, DataCluster> data;

    public LocalDateTime getStart() { return start; }

    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getFinish() { return finish; }

    public void setFinish(LocalDateTime finish) { this.finish = finish; }

    public Double getDuration() { return duration; }

    public void setDuration(Double duration) { this.duration = duration; }

    public ConcurrentMap<String, DataCluster> getData() { return data; }

    public void setData(ConcurrentMap<String, DataCluster> data) { this.data = data; }

    public void register(Tracker tracker){
        String cluster = tracker.getCluster();

    }
}
