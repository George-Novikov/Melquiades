package com.georgen.melquiades.model.trackers;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class NamedTracker implements Tracker {

    private final UUID uuid;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration = -1;
    private Phase phase = Phase.RUNNING;
    private String cluster;
    private String group;
    private String process;

    public NamedTracker(String process) {
        this(Tracker.DEFAULT_CLUSTER, Tracker.DEFAULT_GROUP, process);
    }

    public NamedTracker(String group, String process) {
        this(Tracker.DEFAULT_CLUSTER, group, process);
    }

    public NamedTracker(Class javaClass, String process){
        this(Tracker.DEFAULT_CLUSTER, javaClass.getSimpleName(), process);
    }

    public NamedTracker(String cluster, String group, String process) {
        this.uuid = UUID.randomUUID();
        this.start = LocalDateTime.now();
        this.cluster = cluster;
        this.group = group;
        this.process = process;
    }

    public UUID getUuid() { return uuid; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public long getDuration() { return duration; }

    public Phase getPhase() { return phase; }

    public String getCluster() { return cluster; }

    public void setCluster(String cluster) { this.cluster = cluster; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public String getProcess() { return process; }

    public void setProcess(String process) { this.process = process; }

    public boolean hasCluster(){ return this.cluster != null && !this.cluster.isEmpty(); }

    public boolean hasGroup(){ return this.group != null && !this.group.isEmpty(); }

    public boolean hasProcess(){ return this.process != null && !this.process.isEmpty(); }

    public boolean hasDefaultCluster(){ return Tracker.DEFAULT_CLUSTER.equals(this.cluster); }

    public boolean hasDefaultGroup(){ return Tracker.DEFAULT_GROUP.equals(this.cluster); }

    public Tracker finish(Object... args){
        this.finish = LocalDateTime.now();
        this.phase = Phase.FINISHED;
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);
        Profiler.getInstance().process(this);
        return this;
    }

    @Override
    public Tracker error(Exception e) {
        this.phase = Phase.ERROR;
        Profiler.getInstance().process(this);
        return this;
    }

    @Override
    public String toString() { return this.print(); }
}
