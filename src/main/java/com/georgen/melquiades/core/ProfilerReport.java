package com.georgen.melquiades.core;


import com.georgen.melquiades.model.data.ClusterData;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

public class ProfilerReport {
    private LocalDateTime start;
    private LocalDateTime finish;
    private Double duration;
    private ConcurrentMap<String, ClusterData> data;

    public LocalDateTime getStart() { return start; }

    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getFinish() { return finish; }

    public void setFinish(LocalDateTime finish) { this.finish = finish; }

    public Double getDuration() { return duration; }

    public void setDuration(Double duration) { this.duration = duration; }

    public ConcurrentMap<String, ClusterData> getData() { return data; }

    public void setData(ConcurrentMap<String, ClusterData> data) { this.data = data; }
}
